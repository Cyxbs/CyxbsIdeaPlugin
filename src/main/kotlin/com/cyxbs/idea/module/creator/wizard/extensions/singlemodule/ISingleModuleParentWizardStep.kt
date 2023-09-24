package com.cyxbs.idea.module.creator.wizard.extensions.singlemodule

import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.GraphProperty

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 15:48
 */
interface ISingleModuleParentWizardStep {

  val wizardStep: NewProjectWizardStep

  val singleModuleProperty: GraphProperty<Boolean>

  val singleModuleEnabled: Boolean
    get() = true
}