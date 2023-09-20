package com.cyxbs.idea.module.creator.wizard.dependencies

import com.cyxbs.idea.module.creator.utils.gridConstraint
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.desription.LibraryDescriptionPanel
import com.cyxbs.idea.module.creator.wizard.dependencies.libraries.LibrariesListPanel
import com.cyxbs.idea.module.creator.wizard.dependencies.selected.SelectedLibrariesPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class DependenciesPanel(
  private val selectedCallback: (List<TreeNodeData>) -> Unit
) : JPanel(GridBagLayout()) {

  // 右上方的依赖描述
  private val mLibraryDescriptionPanel = LibraryDescriptionPanel()

  // 右下方的已选列表
  private val mSelectedLibrariesPanel = SelectedLibrariesPanel { libraryInfo ->
    // 不能直接从 selectedLibraryIds 中删除，因为可能这个库是被另一个库包含的 (设置在 CyxbsLibrary.includesLibraries 中)
    mLibrariesListPanel.walkCheckedTree(mLibrariesListPanel.getLibrariesRoot()) {
      if (it.userObject == libraryInfo && it.isEnabled) {
        mLibrariesListPanel.setNodeState(it, false)
      }
    }
    mLibrariesListPanel.updateSelectedLibraries()
  }

  // 左边的依赖列表
  private val mLibrariesListPanel: LibrariesListPanel = LibrariesListPanel {
    mSelectedLibrariesPanel.update(it)
    selectedCallback.invoke(it)
  }

  init {
    val height = 260
    setLeftPanel(height)
    setRightPanel(height)
    observeLibrariesSelected()
  }

  fun update(libraries: List<TreeNodeData>) {
    mLibrariesListPanel.update(libraries)
  }

  private fun setLeftPanel(height: Int) {
    val scrollPanel = JBScrollPane(mLibrariesListPanel)
    scrollPanel.preferredSize = Dimension(0, height)
    add(scrollPanel, gridConstraint(0, 0))
  }

  private fun setRightPanel(height: Int) {
    val rightPanel = JPanel(GridBagLayout())
    rightPanel.preferredSize = Dimension(0, height)
    add(rightPanel, gridConstraint(1, 0))
    rightPanel.border = JBUI.Borders.emptyLeft(UIUtil.DEFAULT_HGAP * 2)
    rightPanel.apply {
      add(mLibraryDescriptionPanel, gridConstraint(0, 0))
      add(JBScrollPane(mSelectedLibrariesPanel).apply {
        preferredSize = Dimension(0, (height * 0.6).toInt())
      }, gridConstraint(0, 1))
    }
  }

  // 观察被选中的列表
  private fun observeLibrariesSelected() {
    mLibrariesListPanel.selectionModel.addTreeSelectionListener { event ->
      if (event.isAddedPath) {
        when (val item = (event.path?.lastPathComponent as? DefaultMutableTreeNode)?.userObject) {
          is TreeNodeData -> mLibraryDescriptionPanel.update(item.description)
        }
      }
    }
  }
}