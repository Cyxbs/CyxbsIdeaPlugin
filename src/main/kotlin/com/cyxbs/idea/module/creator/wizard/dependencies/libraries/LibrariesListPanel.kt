package com.cyxbs.idea.module.creator.wizard.dependencies.libraries

import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType.LeafNode
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType.ParentNode
import com.intellij.icons.AllIcons
import com.intellij.ui.*
import com.intellij.util.ui.tree.TreeUtil
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JTree
import javax.swing.KeyStroke
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreeSelectionModel

/**
 * 用于显示依赖列表的选择控件
 *
 * 逻辑来自官方 StarterLibrariesStep 中的 librariesList
 *
 * @param selectedCallback 被选中的所有列表回调
 */
class LibrariesListPanel(
  private val selectedCallback: (List<TreeNodeData>) -> Unit,
) : CheckboxTreeBase(
  object : CheckboxTree.CheckboxTreeCellRenderer() {
    override fun customizeRenderer(
      tree: JTree?,
      value: Any?,
      selected: Boolean,
      expanded: Boolean,
      leaf: Boolean,
      row: Int,
      hasFocus: Boolean
    ) {
      if (value !is DefaultMutableTreeNode) return
      val renderer = textRenderer
      val library = value.userObject
      if (library is TreeNodeData) {
        when (library.checkableType) {
          CheckableType.Catalog -> {
            renderer.icon = AllIcons.Nodes.PpLibFolder
            renderer.append(library.title, SimpleTextAttributes.REGULAR_ATTRIBUTES)
          }
          is CheckableType.Checkbox -> {
            renderer.icon = AllIcons.Nodes.PpLib
            renderer.append(library.title, SimpleTextAttributes.REGULAR_ATTRIBUTES)
          }
        }
      }
    }
  }, null
) {

  private val mSelectedLibraries: MutableSet<TreeNodeData> = mutableSetOf()

  init {
    rowHeight = 0
    isRootVisible = false
    selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
  }

  fun update(libraries: List<TreeNodeData>) {
    // 需要展开的树节点
    val expandTreeNode = mutableListOf<DefaultMutableTreeNode>()
    val checkboxTreeNodes = mutableListOf<CheckedTreeNode>()
    // 递归添加树节点
    fun createNode(library: TreeNodeData): DefaultMutableTreeNode {
      val libraryNode = when (val nodeType = library.checkableType) {
        CheckableType.Catalog -> DefaultMutableTreeNode(library)
        is CheckableType.Checkbox -> {
          if (nodeType.isDefault) {
            mSelectedLibraries.add(library)
          }
          CheckedTreeNode(library).also {
            it.isChecked = mSelectedLibraries.contains(library)
            it.isEnabled = nodeType.isEnabled
            checkboxTreeNodes.add(it)
          }
        }
      }
      when (val treeNode = library.treeNode) {
        LeafNode -> Unit
        is ParentNode -> {
          treeNode.children.forEach {
            libraryNode.add(createNode(it))
          }
          if (treeNode.isExpand) {
            expandTreeNode.add(libraryNode)
          }
        }
      }
      return libraryNode
    }
    // 根节点，不会显示，他下面有两种节点，一种 CyxbsLibrary，一种 CyxbsLibraryCategory
    val rootNode = CheckedTreeNode()
    // 重置已选节点
    mSelectedLibraries.clear()

    for (library in libraries) {
      rootNode.add(createNode(library))
    }
    model = DefaultTreeModel(rootNode)
    expandTreeNode.forEach {
      expandPath(TreeUtil.getPath(rootNode, it))
    }
    if (checkboxTreeNodes.isNotEmpty()) {
      val toExpand = checkboxTreeNodes.find { isExpanded(TreeUtil.getPath(rootNode, it.parent)) }
      if (toExpand != null) {
        selectionModel.clearSelection()
        // 设置初始状态时显示的节点为第一个展开的 checkobox
        selectionModel.selectionPath = TreeUtil.getPath(rootNode, toExpand)
      }
    }
    updateSelectedLibraries()
  }

  init {
    // 被选中或者被取消选中时的回调
    addCheckboxTreeListener(object : CheckboxTreeListener {
      override fun nodeStateChanged(node: CheckedTreeNode) {
        val library = node.userObject as? TreeNodeData ?: return
        if (library.treeNode !is LeafNode) return
        if (node.isChecked) {
          mSelectedLibraries.add(library)
        } else {
          mSelectedLibraries.remove(library)
        }
        updateSelectedLibraries()
        repaint()
      }
    })
  }

  // 更新已选列表
  fun updateSelectedLibraries() {
    val selected = mutableListOf<TreeNodeData>()
    walkCheckedTree(getLibrariesRoot()) {
      val library = (it.userObject as? TreeNodeData)
      if (library != null && library.treeNode is LeafNode && it.isChecked) {
        selected.add(library)
      }
    }
    selectedCallback.invoke(selected)
  }

  // 深度遍历列表
  fun walkCheckedTree(root: CheckedTreeNode?, visitor: (CheckedTreeNode) -> Unit) {
    if (root == null) return
    fun walkTreeNode(root: TreeNode, visitor: (CheckedTreeNode) -> Unit) {
      if (root is CheckedTreeNode) {
        visitor.invoke(root)
      }
      for (child in root.children()) {
        walkTreeNode(child, visitor)
      }
    }
    walkTreeNode(root, visitor)
  }

  fun getLibrariesRoot(): CheckedTreeNode? {
    return model.root as? CheckedTreeNode
  }

  init {
    // 启用 Enter 键选中
    inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pick-node")
    actionMap.put("pick-node", object : AbstractAction() {
      override fun actionPerformed(e: ActionEvent?) {
        val selection = selectionPath
        if (selection != null) {
          if (selection.lastPathComponent is CheckedTreeNode) {
            val node = selection.lastPathComponent as CheckedTreeNode
            setNodeState(node, !node.isChecked)
          } else if (selection.lastPathComponent is DefaultMutableTreeNode) {
            if (isExpanded(selection)) {
              collapsePath(selection)
            } else {
              expandPath(selection)
            }
          }
        }
      }
    })
  }
}