package com.cyxbs.idea.module.creator.wizard.dependencies.data

data class TreeNodeData(
  val title: String,
  val description: List<String>,
  val checkableType: CheckableType,
  val treeNode: TreeNodeType,
)

sealed interface CheckableType {
  object Catalog : CheckableType
  data class Checkbox(
    var isDefault: Boolean = false,
    var isEnabled: Boolean = true,
  ) : CheckableType
}

sealed interface TreeNodeType {
  object LeafNode : TreeNodeType
  data class ParentNode(
    val children: List<TreeNodeData>,
    val isExpand: Boolean = true,
  ) : TreeNodeType
}
