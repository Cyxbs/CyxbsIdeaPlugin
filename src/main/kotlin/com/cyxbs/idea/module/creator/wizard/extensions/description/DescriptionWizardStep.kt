package com.cyxbs.idea.module.creator.wizard.extensions.description

import com.cyxbs.idea.module.creator.modules.properties.IAddModuleProperties
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 21:59
 */
class DescriptionWizardStep(
  val parent: NewProjectWizardStep,
  val properties: IAddModuleProperties,
) : AbstractNewProjectWizardStep(parent) {

  private val mDescriptionProperty = propertyGraph.property("")

  override fun setupUI(builder: Panel) {
    super.setupUI(builder)
    with(builder) {
      row("模块描述:") {
        textField()
          .bindText(mDescriptionProperty)
          .columns(COLUMNS_LARGE)
      }
    }
  }

  override fun setupProject(project: Project) {
    super.setupProject(project)
    properties.addModuleProperties(
      "idea.plugin.module.builder.description" to mDescriptionProperty.get().ifEmpty { "No description" }
    )
  }
}