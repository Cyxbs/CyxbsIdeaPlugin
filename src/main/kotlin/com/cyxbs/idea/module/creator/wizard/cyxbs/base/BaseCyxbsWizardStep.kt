package com.cyxbs.idea.module.creator.wizard.cyxbs.base

import com.cyxbs.idea.module.creator.wizard.base.BaseCombineWizardStep
import com.intellij.ide.projectWizard.generators.AssetsNewProjectWizardStep
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:02
 */
abstract class BaseCyxbsWizardStep(parentStep: NewProjectWizardStep) : BaseCombineWizardStep(parentStep) {

  private val mAssetsNewProjectWizardStep by lazy {
    CyxbsAssetsNewProjectWizardStep(this)
  }

  override fun setupProject(project: Project) {
    super.setupProject(project)
    mAssetsNewProjectWizardStep.setupProject(project)
  }

  /**
   * 添加文件或者文件夹
   */
  fun addAssets(vararg assets: GeneratorAsset) {
    mAssetsNewProjectWizardStep.addAssets(*assets)
  }

  /**
   * 设置模版文件中的字段属性
   */
  fun addTemplateProperties(properties: Map<String, Any>) {
    mAssetsNewProjectWizardStep.addTemplateProperties(properties)
  }

  /**
   * 打开某文件
   */
  fun addFilesToOpen(vararg relativeCanonicalPaths: String) {
    mAssetsNewProjectWizardStep.addFilesToOpen(*relativeCanonicalPaths)
  }

  // 用于生成模版文件的类
  private inner class CyxbsAssetsNewProjectWizardStep(parent: NewProjectWizardStep) : AssetsNewProjectWizardStep(parent) {
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