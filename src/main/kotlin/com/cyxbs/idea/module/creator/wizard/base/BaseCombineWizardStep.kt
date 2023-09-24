package com.cyxbs.idea.module.creator.wizard.base

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:01
 */
abstract class BaseCombineWizardStep(
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