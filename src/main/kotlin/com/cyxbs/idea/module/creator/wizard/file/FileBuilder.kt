package com.cyxbs.idea.module.creator.wizard.file

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.intellij.openapi.project.Project
import java.io.File

/**
 * 没有采用
 *
 * @author 985892345
 * 2023/9/21 08:50
 */
object FileBuilder {

  fun appendModulePlugin(moduleFile: File, isSingleModule: Boolean) {
    createGradle(moduleFile).addText { lines ->
      val resultMap = hashMapOf<Int, String>()
      val pluginsIndex = lines.indexOfFirst { it.matches(Regex("plugins +\\{ *")) }
      if (pluginsIndex >= 0) {
        if (isSingleModule) {
          if (!lines.any { it.contains("id(\"module-single\")") }) {
            resultMap[pluginsIndex + 1] = "  id(\"module-single\")"
          }
        } else {
          if (!lines.any { it.contains("id(\"module-manager\")") }) {
            resultMap[pluginsIndex + 1] = "  id(\"module-manager\")"
          }
        }
      } else {
        val pluginName = if (isSingleModule) "module-single" else "module-manager"
        resultMap[0] = "plugins {"
        resultMap[1] = "  id(\"$pluginName\")"
        resultMap[2] = "}"
      }
      resultMap
    }
  }

  fun appendDependLibrary(moduleFile: File, dependTexts: List<String>) {
    appendDepend(moduleFile, dependTexts, "dependLibrary")
  }

  fun appendDependModule(moduleFile: File, dependTexts: List<String>) {
    appendDepend(moduleFile, dependTexts, "dependModule")
  }

  fun createSrc(moduleFile: File, stepName: String) {
    val mainDir = moduleFile.resolve("src").resolve("main")
    mainDir.resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(stepName)
      .resolve(moduleFile.name.replace("-", File.separator))
      .mkdirs()
    mainDir.resolve("AndroidManifest.xml")
      .writeText(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
            "  <application>\n" +
            "  </application>\n" +
            "</manifest>"
      )
  }

  fun insertInclude(project: Project, moduleName: String, stepName: String) {
    val basePath = project.basePath ?: return
    if (!checkCyxbsMobileLite(basePath)) return
    val settingsGradleFile = File(basePath).resolve("settings.gradle.kts")
    settingsGradleFile.replaceText { oldList ->
      val indexStart = oldList.indexOfFirst { it.contains("// cyxbs-$stepName") }
      val indexEnd = oldList.indexOfLast { it.contains("// cyxbs-$stepName") }
      val includeText = if (moduleName.startsWith("api-")) {
        "include(\":cyxbs-$stepName:${moduleName.substringAfter("api-")}:$moduleName\")"
      } else {
        "include(\":cyxbs-$stepName:$moduleName\")"
      }
      oldList.subList(indexStart + 1, indexEnd)
        .toMutableList()
        .apply {
          add(includeText)
          sort()
          addAll(size, oldList.subList(indexEnd, oldList.size))
          addAll(0, oldList.subList(0, indexStart + 1))
        }
    }
  }

  private fun createGradle(moduleFile: File): File {
    moduleFile.mkdirs()
    val gradleFile = moduleFile.resolve("build.gradle.kts")
    if (!gradleFile.exists()) {
      gradleFile.createNewFile()
    }
    return gradleFile
  }

  private fun appendDepend(moduleFile: File, dependTexts: List<String>, dependBlockName: String) {
    createGradle(moduleFile).addText { lines ->
      val resultMap = linkedMapOf<Int, String>()
      val dependLibraryIndex = lines.indexOfFirst { it.contains(dependBlockName) }
      if (dependLibraryIndex >= 0) {
        var index = dependLibraryIndex + 1
        dependTexts.forEach { depend ->
          if (!lines.any { it.contains(depend) }) {
            resultMap[index++] = "  $depend"
          }
        }
      } else {
        var index = lines.size
        resultMap[index++] = ""
        resultMap[index++] = "$dependBlockName {"
        dependTexts.forEach {
          resultMap[index++] = "  $it"
        }
        resultMap[index] = "}"
      }
      resultMap
    }
  }

  private fun File.addText(lines: (List<String>) -> Map<Int, String>) {
    if (exists() && canWrite()) {
      val readLines = readLines()
      val textByIndex = lines.invoke(readLines)
      val newLines = readLines.toMutableList().apply {
        textByIndex.forEach {
          add(it.key, it.value)
        }
      }
      writeText(newLines.joinToString("\n"))
    }
  }

  private fun File.replaceText(lines: (List<String>) -> List<String>) {
    if (exists() && canWrite()) {
      val newLines = lines.invoke(readLines())
      writeText(newLines.joinToString("\n"))
    }
  }
}