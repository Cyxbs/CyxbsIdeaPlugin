package com.cyxbs.idea.update

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

/**
 * .
 *
 * @author 985892345
 * 2023/9/27 14:12
 */
class UpdateNewWizardStep(
  parentStep: NewProjectWizardStep,
  private val text: String
) : AbstractNewProjectWizardStep(parentStep) {

  override fun setupUI(builder: Panel) {
    super.setupUI(builder)
    builder.row {
      cell(JPanel(GridBagLayout()).apply {
        add(JBLabel(text), GridBagConstraints().apply {
          fill = GridBagConstraints.CENTER
        })
      }).horizontalAlign(HorizontalAlign.FILL)
    }
  }
}