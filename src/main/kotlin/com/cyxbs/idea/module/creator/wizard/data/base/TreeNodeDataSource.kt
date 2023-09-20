package com.cyxbs.idea.module.creator.wizard.data.base

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import org.jetbrains.concurrency.runAsync

abstract class TreeNodeDataSource {

  private val mLibraryMap = LinkedHashMap<String, TreeNodeData>()

  /**
   * 复制一份数据
   */
  fun getData(): Map<String, TreeNodeData> {
    return synchronized(mLibraryMap) {
      @Suppress("UNCHECKED_CAST")
      LinkedHashMap(mLibraryMap)
    }
  }

  /**
   * 读取依赖信息
   */
  fun loadData(rootProjectPath: String?) {
    if (rootProjectPath == null) return
    if (!checkCyxbsMobileLite(rootProjectPath)) {
      return
    }
    runAsync {
      synchronized(mLibraryMap) {
        val result = LinkedHashMap<String, TreeNodeData>()
        readData(rootProjectPath, result)
        mLibraryMap.clear()
        mLibraryMap.putAll(result)
      }
    }
  }

  protected abstract fun readData(rootProjectPath: String, result: LinkedHashMap<String, TreeNodeData>)
}