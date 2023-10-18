package com.cyxbs.idea.module.creator

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleDescriptionProvider
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.others.OthersWizardStep
import com.cyxbs.idea.module.libraries.LibrariesDataSource
import com.cyxbs.idea.module.modules.properties.CyxbsProperties
import com.cyxbs.idea.module.modules.ModulesDataSource
import com.cyxbs.idea.module.utils.CyxbsIcon
import com.cyxbs.idea.module.utils.TextWizardStep
import com.cyxbs.idea.module.utils.checkCyxbsMobileLite
import com.cyxbs.idea.update.PluginVersionChecker
import com.intellij.openapi.project.Project
import javax.swing.Icon

/**
 * .
 *
 * @author 985892345
 * 2023/10/7 14:18
 */
class CyxbsModuleBuilder : ModuleDescriptionProvider {

  override fun getDescriptions(project: Project): Collection<ModuleGalleryEntry> {
    if (checkCyxbsMobileLite(project.basePath)) {
      CyxbsProperties.init(project)
      ModulesDataSource.loadData(project)
      LibrariesDataSource.loadData(project)
      return listOf(CyxbsModuleGalleryEntry())
    }
    return emptyList()
  }

  class CyxbsModuleGalleryEntry : ModuleGalleryEntry {

    override val description: String
      get() = "掌上重邮极速版模块构建工具"
    override val icon: Icon
      get() = CyxbsIcon
    override val name: String
      get() = "CyxbsModuleBuilder"

    /**
     * 由于这里拿不到当前点击的模块，所以暂时就只显示 OthersWizardStep
     *
     * idea 那边插件可以使用 WizardContext#projectName 拿到
     */
    override fun createStep(
      project: Project,
      moduleParent: String,
      projectSyncInvoker: ProjectSyncInvoker
    ): SkippableWizardStep<*> {
      val update = PluginVersionChecker.check(project)
      return when (update) {
        true -> OthersWizardStep(project, moduleParent, projectSyncInvoker)
        false -> TextWizardStep("项目目录下发现更新的插件 jar 包，请安装更新后再使用")
        // 强制要求使用者放置 jar 包，防止有人把更新包删了用来逃避更新
        null -> TextWizardStep("未在项目目录下找到插件 jar 包，目录下必须存在插件 jar 包")
      }
    }
  }
}