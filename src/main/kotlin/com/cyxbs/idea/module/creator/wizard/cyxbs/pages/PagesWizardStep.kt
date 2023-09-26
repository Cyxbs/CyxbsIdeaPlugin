package com.cyxbs.idea.module.creator.wizard.cyxbs.pages

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.cyxbs.idea.module.creator.wizard.cyxbs.ICyxbsParentWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.base.BaseCyxbsWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.description.DescriptionWizardStep
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
  val parent: ICyxbsParentWizardStep
) : BaseCyxbsWizardStep(parent.wizardStep) {

  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    DescriptionWizardStep(this, this),
    SingleModuleWizardStep(mParentWizardStepImpl),
  )

  override val group: CyxbsGroup
    get() = CyxbsGroup.Pages
  override val newProjectName: String
    get() = parent.newProjectName
  override val isNeedApiModule: Boolean
    get() = true
  override val isNeedSingleModule: Boolean
    get() = mParentWizardStepImpl.singleModuleProperty.get()
  override val dependModules: List<String>
    get() = parent.dependModules
  override val dependLibraries: List<String>
    get() = parent.dependLibraries

  private inner class ParentWizardStepImpl : ISingleModuleParentWizardStep {
    override val wizardStep: NewProjectWizardStep
      get() = this@PagesWizardStep
    override val singleModuleProperty: GraphProperty<Boolean>
      get() = wizardStep.propertyGraph.property(true)
  }
}