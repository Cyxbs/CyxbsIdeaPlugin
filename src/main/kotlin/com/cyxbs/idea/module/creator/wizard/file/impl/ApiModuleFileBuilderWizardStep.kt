package com.cyxbs.idea.module.creator.wizard.file.impl

import com.cyxbs.idea.module.creator.modules.properties.IAddModuleProperties
import com.cyxbs.idea.module.creator.wizard.file.IApiModuleFileBuilder
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/27 13:00
 */
class ApiModuleFileBuilderWizardStep(
  parent: NewProjectWizardStep,
  val builder: IApiModuleFileBuilder
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
    if (builder.isNeedApiModule) {
      generateApiModuleFile()
      addTemplateProperties()
    }
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
    builder.addTemplateProperties(
      "MODULE_PROPERTIES" to mModuleProperties.map { "${it.key}=${it.value}" },
    )
  }
}