package com.cyxbs.idea.module.creator.wizard.combine

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel

/**
 * 组合多个 AbstractNewProjectWizardStep
 */
abstract class CombineWizardStep(
  parentStep: NewProjectWizardStep
) : AbstractNewProjectWizardStep(parentStep) {

  private val mNewProjectWizardSteps by lazy { createStep() }

  abstract fun createStep(): List<NewProjectWizardStep>

  override fun setupUI(builder: Panel) {
    mNewProjectWizardSteps.forEach { it.setupUI(builder) }
  }

  override fun setupProject(project: Project) {
    mNewProjectWizardSteps.forEach { it.setupProject(project) }
  }
}