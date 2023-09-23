package com.cyxbs.idea.module.creator.wizard.file

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.projectWizard.generators.AssetsNewProjectWizardStep
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import org.jetbrains.kotlin.idea.gradleTooling.capitalize
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * .
 *
 * @author 985892345
 * 2023/9/23 19:41
 */
class FileBuilderWizardStep(
  parent: NewProjectWizardStep
) : AssetsNewProjectWizardStep(parent) {

  companion object {
    lateinit var StepName: String
    lateinit var ModuleName: String
    var NeedApiModule = false
    var IsSingleModule = false
    var Modules: List<String> = emptyList()
    var Libraries: List<String> = emptyList()
  }

  override fun setupAssets(project: Project) {
    println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "setupAssets")
    outputDirectory = project.basePath!!
    addTemplateProperties()
    val ftManager = FileTemplateManager.getInstance(ProjectManager.getInstance().defaultProject)
    val modulePath = "cyxbs-$StepName/$ModuleName"
    addAssets(GeneratorEmptyDirectory("$modulePath/src/main/java/com/cyxbs/$StepName/${ModuleName.replace("-", "/")}"))
    addAssets(GeneratorTemplateFile("$modulePath/build.gradle.kts", ftManager.getCodeTemplate("Cyxbs build.gradle.kts")))
    addAssets(GeneratorTemplateFile("$modulePath/src/main/AndroidManifest.xml", ftManager.getCodeTemplate("Cyxbs AndroidManifest.xml")))
    if (NeedApiModule) {
      addAssets(GeneratorEmptyDirectory("$modulePath/api-$ModuleName/src/main/java/com/cyxbs/$StepName/api/$ModuleName"))
      addAssets(GeneratorTemplateFile("$modulePath/api-$ModuleName/build.gradle.kts", ftManager.getCodeTemplate("Cyxbs api build.gradle.kts")))
      addAssets(GeneratorTemplateFile("$modulePath/api-$ModuleName/src/main/AndroidManifest.xml", ftManager.getCodeTemplate("Cyxbs AndroidManifest.xml")))
    }
    if (IsSingleModule) {

    }
    syncGradle(project)
  }

  private fun addTemplateProperties() {
    addTemplateProperties(
      "IS_SINGLE_MODULE" to IsSingleModule,
      "MODULES" to Modules,
      "LIBRARIES" to Libraries,
      "PACKAGE" to "com.cyxbs.$StepName.${ModuleName.replace("-", "/")}",
      "SINGLE_MODULE_ENTRY" to ModuleName.split("-").joinToString("") { it.replaceFirstChar { it.uppercaseChar() } },
    )
  }

  // 借鉴自 AbstractGradleModuleBuilder 中的代码
  private fun syncGradle(project: Project) {
    // execute when current dialog is closed
    ApplicationManager.getApplication().invokeLater({
      reloadProject(project)
    }, ModalityState.NON_MODAL, project.disposed)
  }

  private fun reloadProject(project: Project) {
    ExternalProjectsManagerImpl.getInstance(project).runWhenInitialized {
      val importSpec = ImportSpecBuilder(project, GradleConstants.SYSTEM_ID)
      val externalProjectPath = project.basePath!!
      ExternalSystemUtil.refreshProject(externalProjectPath, importSpec)
    }
  }
}