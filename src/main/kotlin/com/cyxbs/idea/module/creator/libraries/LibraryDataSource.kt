package com.cyxbs.idea.module.creator.libraries

import com.cyxbs.idea.module.creator.libraries.data.CyxbsLibrary
import com.cyxbs.idea.module.creator.libraries.data.DependItem
import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.intellij.openapi.project.Project
import org.jetbrains.concurrency.runAsync
import java.io.File

object LibraryDataSource {

  private val mCyxbsLibraries = mutableListOf<CyxbsLibrary>()

  fun copyCyxbsLibraries(): List<CyxbsLibrary> = synchronized(mCyxbsLibraries) {
    mCyxbsLibraries.toList()
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
      synchronized(mCyxbsLibraries) {
        val result = mutableListOf<CyxbsLibrary>()
        readData(bashPath, result)
        mCyxbsLibraries.clear()
        mCyxbsLibraries.addAll(result)
      }
    }
  }

  private fun readData(rootProjectPath: String, result: MutableList<CyxbsLibrary>) {
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
      result.add(createCyxbsLibrary(it))
    }
  }

  private fun createCyxbsLibrary(file: File): CyxbsLibrary {
    return CyxbsLibraryImpl(file)
  }

  private data class CyxbsLibraryImpl(override val file: File) : CyxbsLibrary {
    override val group: String
      get() = file.nameWithoutExtension

    override val description: List<String> by lazy { readDescription(mReadLines, group) }

    override val depends: List<DependItem> by lazy { readDepend(mReadLines, group) }

    private val mReadLines by lazy { file.readLines() }
  }

  private fun readDescription(lines: List<String>, group: String): List<String> {
    val objectRegex = Regex(" *object +$group *\\{ *")
    lines.forEachIndexed { index, line ->
      if (line.matches(objectRegex)) {
        return readInfo(lines, 0, index)
      }
    }
    return emptyList()
  }

  private fun readDepend(lines: List<String>, group: String): List<DependItem> {
    var leftCount = 0
    var objectEndIndex = 0
    val objectRegex = Regex(" *object +$group *\\{ *")
    for (index in lines.indices) {
      val line = lines[index]
      if (line.matches(objectRegex)) {
        // 读取到第一行 object Xxx {
        leftCount++
        continue
      }
      if (leftCount > 0) {
        if (line.contains("{")) leftCount++
        if (line.contains("}")) leftCount--
        if (leftCount == 0) {
          // 找到 object Xxx { 的末尾 }
          objectEndIndex = index
          break
        }
      }
    }
    return readDepend(lines, objectEndIndex, lines.size)
  }

  private fun readDepend(lines: List<String>, start: Int, end: Int): List<DependItem> {
    if (start > end) return emptyList()
    val result = mutableListOf<DependItem>()
    var lastFunIndex = start
    val funRegex = Regex(" *fun +DependLibraryScope\\.depend[a-zA-Z0-9]+\\(.+\\{ *")
    for (index in start until end) {
      val line = lines[index]
      if (line.matches(funRegex)) {
        val name =  line.substringAfter("depend").substringBefore("(")
        val info = readInfo(lines, lastFunIndex + 1, index - 1)
        result.add(DependItem(name, info))
        lastFunIndex = index
      }
    }
    return result
  }

  /**
   * 解析 [start] - [end] 行内的注释
   */
  private fun readInfo(lines: List<String>, start: Int, end: Int): List<String> {
    if (start > end) return emptyList()
    val result = mutableListOf<String>()
    val multilineInfoRegex = Regex("^ *\\*.+")
    val lineInfoRegex = Regex("^ *//.+")
    for (index in start .. end) {
      val line = lines[index]
      when {
        line.matches(multilineInfoRegex) -> {
          val exclude = listOf("*/", "@author", "@email", "date")
          if (exclude.all { !line.contains(it) }) {
            result.add(line.substringAfter("*"))
          }
        }
        line.matches(lineInfoRegex) -> {
          result.add(line.substringAfter("//"))
        }
      }
    }
    return result
  }
}