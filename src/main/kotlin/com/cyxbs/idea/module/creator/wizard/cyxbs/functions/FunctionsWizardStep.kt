package com.cyxbs.idea.module.creator.wizard.cyxbs.functions

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.cyxbs.idea.module.creator.wizard.cyxbs.ICyxbsParentWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.base.BaseCyxbsWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.apimodule.ApiModuleWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.apimodule.IApiModuleParentWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.description.DescriptionWizardStep
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
  val parent: ICyxbsParentWizardStep
) : BaseCyxbsWizardStep(parent.wizardStep) {

  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    DescriptionWizardStep(this, this),
    SingleModuleWizardStep(mParentWizardStepImpl),
    ApiModuleWizardStep(mParentWizardStepImpl),
  )

  override val group: CyxbsGroup
    get() = CyxbsGroup.Functions
  override val newProjectName: String
    get() = parent.newProjectName
  override val isNeedApiModule: Boolean
    get() = mParentWizardStepImpl.apiModuleProperty.get()
  override val isNeedSingleModule: Boolean
    get() = mParentWizardStepImpl.singleModuleProperty.get()
  override val dependModules: List<String>
    get() = parent.dependModules
  override val dependLibraries: List<String>
    get() = parent.dependLibraries

  private inner class ParentWizardStepImpl : ISingleModuleParentWizardStep, IApiModuleParentWizardStep {
    override val wizardStep: NewProjectWizardStep
      get() = this@FunctionsWizardStep
    override val singleModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(false)
    override val apiModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(true)
  }
}