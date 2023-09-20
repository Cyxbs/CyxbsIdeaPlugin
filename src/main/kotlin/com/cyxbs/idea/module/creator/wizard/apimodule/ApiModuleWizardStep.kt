package com.cyxbs.idea.module.creator.wizard.apimodule

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected

class ApiModuleWizardStep(
  parent: NewProjectWizardStep,
  private val initialValue: Boolean = false,
  private val isEnabled: Boolean = true,
) : AbstractNewProjectWizardStep(parent) {

  private val apiModuleProperty = propertyGraph.property(initialValue)

  override fun setupUI(builder: Panel) {
    with(builder) {
      row {
        checkBox("创建 api 模块 (会自动依赖 api 模块)")
          .enabled(isEnabled)
          .bindSelected(apiModuleProperty)
      }.bottomGap(BottomGap.SMALL)
    }
  }

  override fun setupProject(project: Project) {
    // todo

  }
}