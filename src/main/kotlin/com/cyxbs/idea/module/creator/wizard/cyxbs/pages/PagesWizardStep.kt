package com.cyxbs.idea.module.creator.wizard.cyxbs.pages

import com.cyxbs.idea.module.creator.wizard.cyxbs.base.BaseCyxbsWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.singlemodule.ISingleModuleParentWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.GraphProperty

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 19:39
 */
class PagesWizardStep(
  parentStep: NewProjectWizardStep
) : BaseCyxbsWizardStep(parentStep) {
  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(mParentWizardStepImpl),
  )

  private inner class ParentWizardStepImpl : ISingleModuleParentWizardStep {
    override val wizardStep: NewProjectWizardStep
      get() = this@PagesWizardStep
    override val singleModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(true)
  }
}