package com.cyxbs.idea.module.creator.wizard.file

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.cyxbs.idea.module.creator.utils.capitalized
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 17:15
 */
class ModuleFileBuilderWizardStep(
  parent: NewProjectWizardStep,
  val builder: IAssetsBuilder
) : AbstractNewProjectWizardStep(parent), IAddModuleProperties {

  private val mModuleProperties = HashMap<String, String>()

  /**
   * 添加模块相关的属性，会生成 module.properties 文件
   */
  override fun addModuleProperties(vararg pair: Pair<String, String>) {
    mModuleProperties.putAll(pair)
  }

  override fun setupProject(project: Project) {
    super.setupProject(project)
    generateModuleFile()
    if (builder.isNeedApiModule) {
      generateApiModuleFile()
    }
    addTemplateProperties()
  }

  private fun generateModuleFile() {
    val modulePath = "${builder.group.groupName}/${builder.newProjectName}"
    val srcPath = "$modulePath/src"
    val mainPath = "$srcPath/main"
    val javaPath = "$mainPath/java"
    val codePath = "$javaPath/com/cyxbs/${builder.group.step}/${builder.newProjectName}"

    // build.gradle.kts
    builder.addAssets(GeneratorTemplateFile(
      "$modulePath/build.gradle.kts",
      builder.template.getCodeTemplate(
        when (builder.group) {
          CyxbsGroup.Applications -> "Cyxbs app build.gradle.kts"
          CyxbsGroup.Components, CyxbsGroup.Functions, CyxbsGroup.Pages -> "Cyxbs build.gradle.kts"
        }
      )
    ))
    // AndroidManifest.xml
    builder.addAssets(GeneratorTemplateFile(
      "$mainPath/AndroidManifest.xml",
      builder.template.getCodeTemplate("Cyxbs AndroidManifest.xml")
    ))
    // README.kt
    val readmePath = "$codePath/README.kt"
    builder.addAssets(GeneratorTemplateFile(
      readmePath,
      builder.template.getCodeTemplate("Cyxbs README.kt")
    ))
    builder.addFilesToOpen(readmePath)
    // 单模块调试
    val singlePackageName = "com/cyxbs/${builder.group.step}/${builder.newProjectName}/single"
    if (builder.isNeedSingleModule) {
      builder.addAssets(GeneratorTemplateFile(
        "$srcPath/single/$singlePackageName/${builder.newProjectName.capitalized()}SingleModuleEntry.kt",
        builder.template.getCodeTemplate("Cyxbs SingleModuleEntry.kt")
      ))
    }
    // module.properties
    builder.addAssets(GeneratorTemplateFile(
      "$modulePath/module.properties",
      builder.template.getCodeTemplate("Cyxbs module.properties")
    ))
  }

  private fun generateApiModuleFile() {
    val modulePath = "${builder.group.groupName}/${builder.newProjectName}/api-${builder.newProjectName}"
    val srcPath = "$modulePath/src"
    val mainPath = "$srcPath/main"
    val javaPath = "$mainPath/java"
    val codePath = "$javaPath/com/cyxbs/${builder.group.step}/api/${builder.newProjectName}"

    // build.gradle.kts
    builder.addAssets(GeneratorTemplateFile(
      "$modulePath/build.gradle.kts",
      builder.template.getCodeTemplate("Cyxbs api build.gradle.kts")
    ))
    // AndroidManifest.xml
    builder.addAssets(GeneratorTemplateFile(
      "$mainPath/AndroidManifest.xml",
      builder.template.getCodeTemplate("Cyxbs AndroidManifest.xml")
    ))
    // 代码文件
    builder.addAssets(GeneratorEmptyDirectory(codePath))
    // module.properties
    builder.addAssets(GeneratorTemplateFile(
      "$modulePath/module.properties",
      builder.template.getCodeTemplate("Cyxbs module.properties")
    ))
  }

  private fun addTemplateProperties() {
    val packageName = "com.cyxbs.${builder.group.step}.${builder.newProjectName}"
    builder.addTemplateProperties(
      "README_PACKAGE" to packageName,
      "BUILD_GRADLE_IS_SINGLE_MODULE" to builder.isNeedSingleModule,
      "BUILD_GRADLE_MODULES" to builder.dependModules.apply { println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "it = $this") },
      "BUILD_GRADLE_LIBRARIES" to builder.dependLibraries,
      "SINGLE_MODULE_ENTRY" to builder.newProjectName.capitalized(),
      "SINGLE_MODULE_PACKAGE" to "$packageName.single",
      "MODULE_PROPERTIES" to mModuleProperties.map { "${it.key}=${it.value}" },
    )
  }
}