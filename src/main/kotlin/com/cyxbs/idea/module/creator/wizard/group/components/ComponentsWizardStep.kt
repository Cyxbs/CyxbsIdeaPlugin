package com.cyxbs.idea.module.creator.wizard.group.components

import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep

class ComponentsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this),
//    DependenciesWizardStep(this, emptyList()),
//    DependModuleWizardStep(this, emptyList()),
  )
}