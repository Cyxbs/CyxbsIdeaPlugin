package com.cyxbs.idea.module.creator.wizard.group.components

import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project

class ComponentsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(this),
  )

  override fun setupProject(project: Project) {
    super.setupProject(project)
//    val moduleFile = GroupManager.getModuleFile(project) ?: return
//    FileBuilder.createSrc(moduleFile, "components")
//    FileBuilder.insertInclude(project, moduleFile.name, "components")
  }
}