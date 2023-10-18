package com.cyxbs.idea.module.creator.wizard.cyxbs.api

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent

/**
 * .
 *
 * @author 985892345
 * 2023/10/7 11:19
 */
class ApiWizardStep(
  project: Project,
  val moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker,
  val cyxbsGroup: CyxbsGroup,
  title: String = "",
) : SkippableWizardStep<ApiWizardModel>(
  ApiWizardModel(project, moduleParent, projectSyncInvoker, cyxbsGroup),
  title
) {

  private val rootPanel by lazy { createMainPanel() }

  override fun getComponent(): JComponent {
    return rootPanel
  }

  private fun createMainPanel(): JComponent {
    return panel {
      row("模块名字:") {
        model.moduleName.set("api-$moduleParent")
        textField()
          .text(model.moduleName.get())
          .enabled(false)
          .columns(COLUMNS_MEDIUM)
          .horizontalAlign(HorizontalAlign.FILL)
          .comment("位置: ${cyxbsGroup.groupName}/${moduleParent}/${model.moduleName.get()}")
      }
      row("模块分组:") {
        segmentedButton(listOf(cyxbsGroup)) { it.step }
          .gap(RightGap.SMALL)
      }
    }
  }
}