package com.cyxbs.idea.module.creator.wizard.cyxbs

import com.intellij.ide.wizard.NewProjectWizardStep

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 08:57
 */
interface ICyxbsParentWizardStep {

  val wizardStep: NewProjectWizardStep

  val newProjectName: String

  val dependModules: List<String>

  val dependLibraries: List<String>
}