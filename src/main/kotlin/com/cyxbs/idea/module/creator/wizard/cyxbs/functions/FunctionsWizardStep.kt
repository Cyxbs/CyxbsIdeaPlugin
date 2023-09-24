package com.cyxbs.idea.module.creator.wizard.cyxbs.functions

import com.cyxbs.idea.module.creator.wizard.cyxbs.base.BaseCyxbsWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.apimodule.IApiModuleParentWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.singlemodule.ISingleModuleParentWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.singlemodule.SingleModuleWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.GraphProperty

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 19:37
 */
class FunctionsWizardStep(
  parentStep: NewProjectWizardStep
) : BaseCyxbsWizardStep(parentStep) {

  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    SingleModuleWizardStep(mParentWizardStepImpl),
    ApiModuleWizardStep(mParentWizardStepImpl),
  )

  private inner class ParentWizardStepImpl : ISingleModuleParentWizardStep, IApiModuleParentWizardStep {
    override val wizardStep: NewProjectWizardStep
      get() = this@FunctionsWizardStep
    override val singleModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(false)
    override val apiModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(true)
  }
}