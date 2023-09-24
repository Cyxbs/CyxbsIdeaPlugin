package com.cyxbs.idea.module.creator.wizard.extensions.apimodule

import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.GraphProperty

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 19:26
 */
interface IApiModuleParentWizardStep {

  val wizardStep: NewProjectWizardStep

  val apiModuleProperty: GraphProperty<Boolean>

  val apiModuleEnabled: Boolean
    get() = true
}