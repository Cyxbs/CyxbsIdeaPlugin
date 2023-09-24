package com.cyxbs.idea.module.creator

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.CyxbsNewProjectWizardStep
import com.cyxbs.idea.module.creator.libraries.LibraryDataSource
import com.cyxbs.idea.module.creator.modules.ModulesDataSource
import com.cyxbs.idea.module.creator.wizard.file.FileBuilderWizardStep
import com.cyxbs.idea.module.creator.wizard.group.GroupManager
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.*
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.roots.ui.configuration.actions.NewModuleAction
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon


class CyxbsModuleBuilder : AbstractNewProjectWizardBuilder() {

  object Icons {
    val CyxbsIcon = IconLoader.getIcon("/icons/cyxbs_icon.svg", Icons::class.java)
  }

  override fun getDescription(): String = "掌上重邮极速版模块构建工具"

  override fun getNodeIcon(): Icon = Icons.CyxbsIcon

  override fun getPresentableName(): String = "CyxbsModuleBuilder"

  // 排在前面点
  override fun getWeight(): Int = JVM_WEIGHT + 2

  override fun createWizardSteps(
    wizardContext: WizardContext,
    modulesProvider: ModulesProvider
  ): Array<ModuleWizardStep> {
    return GroupManager.createWizardSteps(wizardContext, modulesProvider)
  }

  /**
   * context.project.name   项目名称
   * context.projectName    选中的模块名称
   */
  override fun createStep(context: WizardContext): NewProjectWizardStep {
    LibraryDataSource.loadData(context.project)
    ModulesDataSource.loadData(context.project)
    return RootNewProjectWizardStep(context)
      .chain(::CyxbsNewProjectWizardStep)
      .chain(::FileBuilderWizardStep) // 创建文件的需要放在最后
  }

  override fun isAvailable(): Boolean {
    // 只在创建子模块时显示，参考官方 NewProjectWizardModuleBuilder 的写法
    return super.isAvailable() && Thread.currentThread().stackTrace.any { element ->
      element.className == NewModuleAction::class.java.name
    } && ProjectManager.getInstance().openProjects.any {
      // 因为 isAvailable 调用很靠前，此时拿不到当前打开的项目信息，所以就检查打开的全部项目是否存在掌邮极速版才显示模块模版
      // 如果你打开了掌邮极速版又打开其他项目，那确实会在其他项目中显示，这无解
      it.isOpen && checkCyxbsMobileLite(it.basePath)
    }
  }
}