package com.cyxbs.idea.module.creator.wizard.cyxbs.applications

import com.cyxbs.idea.module.creator.wizard.cyxbs.base.BaseCyxbsWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:03
 */
class ApplicationsWizardStep(parentStep: NewProjectWizardStep) : BaseCyxbsWizardStep(parentStep) {

  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
  )

  private inner class ParentWizardStepImpl {

  }
}