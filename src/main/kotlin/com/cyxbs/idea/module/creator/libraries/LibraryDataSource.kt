package com.cyxbs.idea.module.creator.libraries

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType
import com.intellij.openapi.project.Project
import org.jetbrains.concurrency.runAsync
import java.io.File

object LibraryDataSource {

  private val mLibrariesMap = LinkedHashMap<String, TreeNodeData>()

  /**
   * 复制一份数据
   */
  fun getData(): Map<String, TreeNodeData> {
    return synchronized(mLibrariesMap) {
      @Suppress("UNCHECKED_CAST")
      LinkedHashMap(mLibrariesMap)
    }
  }

  /**
   * 读取依赖信息
   */
  fun loadData(project: Project?) {
    val bashPath = project?.basePath ?: return
    if (!checkCyxbsMobileLite(bashPath)) {
      return
    }
    runAsync {
      synchronized(mLibrariesMap) {
        val result = LinkedHashMap<String, TreeNodeData>()
        readData(bashPath, result)
        mLibrariesMap.clear()
        mLibrariesMap.putAll(result)
      }
    }
  }

  private fun readData(rootProjectPath: String, result: LinkedHashMap<String, TreeNodeData>) {
    val libraryDir = File(rootProjectPath)
      .resolve("build-logic")
      .resolve("dependencies")
      .resolve("library")
    val ktFiles = libraryDir
      .resolve("src")
      .resolve("main")
      .resolve("kotlin")
      .listFiles { file ->
        file.isFile && file.name.substringAfterLast(".") == "kt"
      } ?: return
    ktFiles.sort()
    ktFiles.forEach {
      val data = readFile(it)
      if (data != null) {
        result[it.nameWithoutExtension] = data
      }
    }
  }

  private fun readFile(file: File): TreeNodeData? {
    val librariesName = file.readLines().filter {
      it.startsWith("fun DependLibraryScope.depend")
    }.map {
      it.substringAfter("fun DependLibraryScope.depend")
        .substringBeforeLast("(")
    }
    if (librariesName.isEmpty()) return null
    return TreeNodeData(
      file.nameWithoutExtension, "",
      CheckableType.Catalog,
      TreeNodeType.ParentNode(
        librariesName.map {
          TreeNodeData(it, "", CheckableType.Checkbox(), TreeNodeType.LeafNode)
        }
      )
    )
  }
}