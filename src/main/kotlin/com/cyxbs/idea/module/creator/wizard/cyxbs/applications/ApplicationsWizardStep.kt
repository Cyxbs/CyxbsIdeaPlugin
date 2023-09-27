package com.cyxbs.idea.module.creator.wizard.cyxbs.applications

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.cyxbs.idea.module.creator.wizard.cyxbs.ICyxbsParentWizardStep
import com.cyxbs.idea.module.creator.wizard.base.BaseCyxbsWizardStep
import com.cyxbs.idea.module.creator.wizard.extensions.description.DescriptionWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:03
 */
class ApplicationsWizardStep(
  val parent: ICyxbsParentWizardStep
) : BaseCyxbsWizardStep(parent.wizardStep) {

  private val mParentWizardStepImpl = ParentWizardStepImpl()

  override fun createStep(): List<NewProjectWizardStep> = listOf(
    DescriptionWizardStep(this, this),
  )

  override val group: CyxbsGroup
    get() = CyxbsGroup.Applications
  override val newProjectName: String
    get() = parent.newProjectName
  override val isNeedApiModule: Boolean
    get() = false
  override val isNeedSingleModule: Boolean
    get() = false
  override val dependModules: List<String>
    get() = emptyList()
  override val dependLibraries: List<String>
    get() = emptyList()

  private inner class ParentWizardStepImpl {

  }
}