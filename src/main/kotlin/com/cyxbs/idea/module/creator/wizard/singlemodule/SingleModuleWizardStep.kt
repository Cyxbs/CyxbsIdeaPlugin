package com.cyxbs.idea.module.creator.wizard.singlemodule

import com.cyxbs.idea.module.creator.wizard.file.FileBuilder
import com.cyxbs.idea.module.creator.wizard.group.GroupManager
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*

open class SingleModuleWizardStep(
  parent: NewProjectWizardStep,
  initialValue: Boolean = false,
  private val isEnabled: Boolean = true
) : AbstractNewProjectWizardStep(parent) {

  private val singleModuleProperty = propertyGraph.property(initialValue)

  override fun setupUI(builder: Panel) {
    with(builder) {
      row {
        checkBox("开启单模块调试")
          .bindSelected(singleModuleProperty)
          .enabled(isEnabled)
      }.bottomGap(BottomGap.SMALL)
    }
  }

  override fun setupProject(project: Project) {
    val moduleFile = GroupManager.getModuleFile(project) ?: return
    FileBuilder.appendModulePlugin(moduleFile, singleModuleProperty.get())
  }
}