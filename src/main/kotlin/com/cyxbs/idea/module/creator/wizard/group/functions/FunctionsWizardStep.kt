package com.cyxbs.idea.module.creator.wizard.group.functions

import com.cyxbs.idea.module.creator.wizard.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project

class FunctionsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this),
    ApiModuleWizardStep(this, true),
  )

  override fun setupProject(project: Project) {
    super.setupProject(project)
//    val moduleFile = GroupManager.getModuleFile(project) ?: return
//    FileBuilder.createSrc(moduleFile, "functions")
//    FileBuilder.insertInclude(project, moduleFile.name, "functions")
  }
}