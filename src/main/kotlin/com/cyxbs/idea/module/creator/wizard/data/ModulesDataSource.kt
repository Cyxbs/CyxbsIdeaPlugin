package com.cyxbs.idea.module.creator.wizard.data

import com.cyxbs.idea.module.creator.wizard.data.base.TreeNodeDataSource
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.group.GroupManager

object ModulesDataSource : TreeNodeDataSource() {
  override fun readData(rootProjectPath: String, result: LinkedHashMap<String, TreeNodeData>) {
    GroupManager.readData(rootProjectPath, result)
  }
}