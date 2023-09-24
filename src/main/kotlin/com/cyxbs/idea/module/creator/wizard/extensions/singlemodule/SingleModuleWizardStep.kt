package com.cyxbs.idea.module.creator.wizard.extensions.singlemodule

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ui.dsl.builder.*

open class SingleModuleWizardStep(
  val parent: ISingleModuleParentWizardStep
) : AbstractNewProjectWizardStep(parent.wizardStep) {

  override fun setupUI(builder: Panel) {
    with(builder) {
      row {
        checkBox("开启单模块调试")
          .bindSelected(parent.singleModuleProperty)
          .enabled(parent.singleModuleEnabled)
      }.bottomGap(BottomGap.SMALL)
    }
  }
}