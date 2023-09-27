package com.cyxbs.idea.module.creator.wizard.base

import com.cyxbs.idea.module.creator.modules.properties.IAddModuleProperties
import com.cyxbs.idea.module.creator.wizard.file.IApiModuleFileBuilder
import com.cyxbs.idea.module.creator.wizard.file.ICommonModuleFileBuilder
import com.cyxbs.idea.module.creator.wizard.file.impl.ApiModuleFileBuilderWizardStep
import com.cyxbs.idea.module.creator.wizard.file.impl.CommonModuleFileBuilderWizardStep
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.projectWizard.generators.AssetsNewProjectWizardStep
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:02
 */
abstract class BaseCyxbsWizardStep(
  parentStep: NewProjectWizardStep
) : BaseCombineWizardStep(parentStep), IApiModuleFileBuilder, ICommonModuleFileBuilder, IAddModuleProperties {

  private val mApiModuleFileBuilder by lazy {
    ApiModuleFileBuilderWizardStep(this, this)
  }

  private val mCommonModuleFileBuilder by lazy {
    CommonModuleFileBuilderWizardStep(this, this)
  }

  private val mAssetsNewProjectWizardStep by lazy {
    CyxbsAssetsNewProjectWizardStep(this)
  }

  override fun setupProject(project: Project) {
    mAssetsNewProjectWizardStep.outputDirectory = project.basePath!!
    super.setupProject(project)
    mApiModuleFileBuilder.setupProject(project)
    mCommonModuleFileBuilder.setupProject(project)
    mAssetsNewProjectWizardStep.setupProject(project)
  }

  override val template: FileTemplateManager by lazy {
    FileTemplateManager.getInstance(ProjectManager.getInstance().defaultProject)
  }

  override fun addTemplateProperties(vararg properties: Pair<String, Any>) {
    mAssetsNewProjectWizardStep.addTemplateProperties(*properties)
  }

  override fun addAssets(vararg assets: GeneratorAsset) {
    mAssetsNewProjectWizardStep.addAssets(*assets)
  }

  override fun addTemplateProperties(properties: Map<String, Any>) {
    mAssetsNewProjectWizardStep.addTemplateProperties(properties)
  }

  override fun addFilesToOpen(vararg relativeCanonicalPaths: String) {
    mAssetsNewProjectWizardStep.addFilesToOpen(*relativeCanonicalPaths)
  }

  override fun addModuleProperties(vararg pair: Pair<String, String>) {
    mApiModuleFileBuilder.addModuleProperties(*pair)
    mCommonModuleFileBuilder.addModuleProperties(*pair)
  }

  // 用于生成模版文件的类
  private inner class CyxbsAssetsNewProjectWizardStep(
    parent: NewProjectWizardStep
  ) : AssetsNewProjectWizardStep(parent) {

    override fun setupAssets(project: Project) {
      syncGradle(project)
    }

    // 借鉴自 AbstractGradleModuleBuilder 中的代码
    private fun syncGradle(project: Project) {
      // execute when current dialog is closed
      ApplicationManager.getApplication().invokeLater({
        ExternalProjectsManagerImpl.getInstance(project).runWhenInitialized {
          val importSpec = ImportSpecBuilder(project, GradleConstants.SYSTEM_ID)
          val externalProjectPath = project.basePath!!
          ExternalSystemUtil.refreshProject(externalProjectPath, importSpec)
        }
      }, ModalityState.NON_MODAL, project.disposed)
    }
  }
}