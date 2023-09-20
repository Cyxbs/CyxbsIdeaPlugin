package com.cyxbs.idea.module.creator.wizard.group.applications

import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ui.dsl.builder.*

class ApplicationsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
  )

  override fun setupUI(builder: Panel) {
    super.setupUI(builder)
    with(builder) {

    }
  }
}