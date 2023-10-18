package com.cyxbs.idea.module.creator.file

import com.android.tools.idea.wizard.template.renderIf
import com.cyxbs.idea.module.utils.capitalized
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.ProjectManager

/**
 * 文件模版
 *
 * @author 985892345
 * 2023/10/5 16:12
 */

/**
 * @param dependModules 如果为 null，则不生成 dependModule {}
 * @param dependLibraries 如果为 null，则不生成 dependLibrary {}
 */
fun ftBuildGradle(
  plugins: List<String>,
  dependModules: List<String>?,
  dependLibraries: List<String>?,
): String {
  val pluginsBlock = """
    plugins {
      ${plugins.joinToString("\n") { "id(\"$it\")" }}
    }
  """.trimIndent()
  val dependModulesBlock = renderIf(dependModules != null) {
    "dependModule {${if (dependModules!!.isEmpty()) "" else dependModules.joinToString( "\n")}\n}"
  }
  val dependLibrariesBlock = renderIf(dependLibraries != null) {
    "dependLibrary {${if (dependLibraries!!.isEmpty()) "" else dependLibraries.joinToString( "\n")}\n}"
  }
  return """
    $pluginsBlock
    
    $dependModulesBlock
    
    $dependLibrariesBlock
  """.trimIndent()
}


fun ftAndroidManifest() = """
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
    </application>
  </manifest>
""".trimIndent()


fun ftSingleModuleEntry(
  packageName: String,
  moduleName: String,
): String {
  val template = FileTemplateManager.getInstance(ProjectManager.getInstance().defaultProject)
  val header = template.getDefaultTemplate("File Header.java").text
  return """
    package $packageName

    import com.cyxbs.components.singlemodule.ISingleModuleEntry
    import com.g985892345.provider.annotation.SingleImplProvider

    $header
    @SingleImplProvider(ISingleModuleEntry::class)
    object ${moduleName.capitalized()}SingleModuleEntry : ISingleModuleEntry {
      override fun getPage(): ISingleModuleEntry.Page {
        TODO("返回一个启动 Activity 的 Intent 或者 Fragment")
      }
    }
  """.trimIndent()
}