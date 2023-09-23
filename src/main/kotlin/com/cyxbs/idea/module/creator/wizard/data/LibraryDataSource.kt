package com.cyxbs.idea.module.creator.wizard.data

import com.cyxbs.idea.module.creator.wizard.data.base.TreeNodeDataSource
import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType
import java.io.File

object LibraryDataSource : TreeNodeDataSource() {

  override fun readData(rootProjectPath: String, result: LinkedHashMap<String, TreeNodeData>) {
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