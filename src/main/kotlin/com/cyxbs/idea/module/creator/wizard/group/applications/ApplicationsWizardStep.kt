package com.cyxbs.idea.module.creator.wizard.group.applications

import com.cyxbs.idea.module.creator.wizard.combine.CombineWizardStep
import com.cyxbs.idea.module.creator.wizard.file.FileBuilder
import com.cyxbs.idea.module.creator.wizard.group.GroupManager
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*

class ApplicationsWizardStep(
  parent: NewProjectWizardStep
) : CombineWizardStep(parent) {

  override fun createStep(): List<NewProjectWizardStep> = listOf(
  )

  override fun setupUI(builder: Panel) {
    super.setupUI(builder)
  }

  override fun setupProject(project: Project) {
    super.setupProject(project)
    val moduleFile = GroupManager.getModuleFile(project) ?: return
    FileBuilder.createSrc(moduleFile, "applications")
    FileBuilder.appendModulePlugin(moduleFile, false)
    FileBuilder.insertInclude(project, moduleFile.name, "applications")
  }
}