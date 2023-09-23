package com.cyxbs.idea.module.creator.wizard.group.pages

import com.cyxbs.idea.module.creator.wizard.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project

class PagesWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this, true),
    ApiModuleWizardStep(this, initialValue = true, isEnabled = false),
  )

  override fun setupProject(project: Project) {
    super.setupProject(project)
    println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "setupProject")
  }
}