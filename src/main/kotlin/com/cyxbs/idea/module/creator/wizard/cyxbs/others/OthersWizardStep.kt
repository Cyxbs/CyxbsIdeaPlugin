package com.cyxbs.idea.module.creator.wizard.cyxbs.others

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.cyxbs.idea.module.creator.wizard.base.BaseWizardStep
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.cyxbs.idea.module.utils.toIdea
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.selected
import javax.swing.JComponent

/**
 * .
 *
 * @author 985892345
 * 2023/10/6 20:26
 */
open class OthersWizardStep(
  project: Project,
  moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker
) : BaseWizardStep(project, moduleParent, projectSyncInvoker) {

  override fun initStep(): Map<CyxbsGroup, JComponent> {
    return linkedMapOf(
      CyxbsGroup.Pages to panel {
        createSingleModuleCheckbox()
      },
      CyxbsGroup.Functions to panel {
        createSingleModuleCheckbox()
        createApiModuleCheckbox()
      },
      CyxbsGroup.Components to panel {
        createSingleModuleCheckbox()
      }
    )
  }

  private fun Panel.createApiModuleCheckbox() {
    row {
      checkBox("创建 api 模块 (会自动依赖 api 模块)")
        .bindSelected(model.isNeedApiModule::get, model.isNeedApiModule::set)
        .apply {
          model.isNeedApiModule.addListener {
            component.isSelected = model.isNeedApiModule.get()
          }
        }
    }.bottomGap(BottomGap.SMALL)
  }

  private fun Panel.createSingleModuleCheckbox() {
    row {
      checkBox("开启单模块调试")
        .bindSelected(model.isSingleModule::get, model.isSingleModule::set)
        .apply {
          model.isSingleModule.addListener {
            component.isSelected = model.isSingleModule.get()
          }
        }
    }.bottomGap(BottomGap.SMALL)
  }

  override fun createMainPanel(): JComponent {
    model.cyxbsGroup.addListener {
      when (model.cyxbsGroup.value) {
        CyxbsGroup.Applications, CyxbsGroup.Components -> {
          model.isNeedApiModule.set(false)
          model.isSingleModule.set(false)
        }
        CyxbsGroup.Functions -> {
          model.isNeedApiModule.set(true)
          model.isSingleModule.set(false)
        }
        CyxbsGroup.Pages -> {
          model.isNeedApiModule.set(true)
          model.isSingleModule.set(true)
        }
      }
    }
    model.cyxbsGroup.value = stepProperty.get()
    stepProperty.afterChange {
      model.cyxbsGroup.value = it
    }
    return super.createMainPanel()
  }

  override fun createDependentSteps(): Collection<ModelWizardStep<*>> {
    return listOf(DependenciesWizardStep(model))
  }
}