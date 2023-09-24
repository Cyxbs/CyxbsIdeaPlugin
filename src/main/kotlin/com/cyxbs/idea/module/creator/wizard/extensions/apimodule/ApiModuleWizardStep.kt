package com.cyxbs.idea.module.creator.wizard.extensions.apimodule

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected

class ApiModuleWizardStep(
  val parent: IApiModuleParentWizardStep,
) : AbstractNewProjectWizardStep(parent.wizardStep) {

  override fun setupUI(builder: Panel) {
    with(builder) {
      row {
        checkBox("创建 api 模块 (会自动依赖 api 模块)")
          .enabled(parent.apiModuleEnabled)
          .bindSelected(parent.apiModuleProperty)
      }.bottomGap(BottomGap.SMALL)
    }
  }
}