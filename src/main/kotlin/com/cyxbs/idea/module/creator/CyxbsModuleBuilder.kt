package com.cyxbs.idea.module.creator

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.CyxbsNewProjectWizardStep
import com.cyxbs.idea.module.creator.libraries.LibraryDataSource
import com.cyxbs.idea.module.creator.modules.ModulesDataSource
import com.cyxbs.idea.module.creator.wizard.dependencies.DependWizardStepManager
import com.cyxbs.idea.update.PluginVersionChecker
import com.cyxbs.idea.update.UpdateNewWizardStep
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.*
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.roots.ui.configuration.actions.NewModuleAction
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Icon
import javax.swing.JPanel


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
    LibraryDataSource.loadData(wizardContext.project)
    ModulesDataSource.loadData(wizardContext.project)
    return DependWizardStepManager.createWizardSteps(wizardContext, modulesProvider)
  }

  /**
   * context.project.name       项目名称
   * context.projectName        选中的模块名称
   * context.projectDirectory   选中的模块路径
   */
  override fun createStep(context: WizardContext): NewProjectWizardStep {
    return RootNewProjectWizardStep(context).run {
      if (!checkCyxbsMobileLite(context.project?.basePath)) {
        chain(::createNotCyxbsMobileLiteWizardStep)
      } else {
        val updateNewWizardStep = PluginVersionChecker.check(context.project!!)
        if (updateNewWizardStep != null) {
          chain(updateNewWizardStep)
        } else {
          chain(::CyxbsNewProjectWizardStep)
        }
      }
    }
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

  private fun createNotCyxbsMobileLiteWizardStep(
    parent: NewProjectWizardStep
  ): NewProjectWizardStep = object : AbstractNewProjectWizardStep(parent) {
    override fun setupUI(builder: Panel) {
      builder.row {
        cell(JPanel(GridBagLayout()).apply {
          add(JBLabel("检测到不是掌上重邮项目，所以该插件不能使用"), GridBagConstraints().apply {
            fill = GridBagConstraints.CENTER
          })
        }).horizontalAlign(HorizontalAlign.FILL)
      }
    }
  }
}