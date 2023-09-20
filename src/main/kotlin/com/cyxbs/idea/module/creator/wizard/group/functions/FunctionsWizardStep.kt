package com.cyxbs.idea.module.creator.wizard.group.functions

import com.cyxbs.idea.module.creator.wizard.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep

class FunctionsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this),
    ApiModuleWizardStep(this, true),
//    DependenciesWizardStep(this, findLibraries()),
//    DependModuleWizardStep(this, findModules()),
  )
}