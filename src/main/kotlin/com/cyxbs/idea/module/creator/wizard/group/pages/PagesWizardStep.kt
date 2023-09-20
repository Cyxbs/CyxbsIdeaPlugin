package com.cyxbs.idea.module.creator.wizard.group.pages

import com.cyxbs.idea.module.creator.wizard.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep

class PagesWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this, true),
    ApiModuleWizardStep(this, initialValue = true, isEnabled = false),
//    DependenciesWizardStep(this, findLibraries()),
//    DependModuleWizardStep(this, findModules()),
  )
}