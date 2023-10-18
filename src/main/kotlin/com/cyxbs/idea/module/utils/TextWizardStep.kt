package com.cyxbs.idea.module.utils

import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.android.tools.idea.wizard.model.WizardModel
import com.intellij.ui.components.JBLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * .
 *
 * @author 985892345
 * 2023/10/7 14:46
 */
class TextWizardStep(
  val text: String,
) : SkippableWizardStep<TextWizardStep.TextWizardModel>(
  TextWizardModel(), ""
) {

  class TextWizardModel : WizardModel() {
    override fun handleFinished() {
    }
  }

  override fun getComponent(): JComponent {
    return JPanel(GridBagLayout()).apply {
      add(JBLabel(text), GridBagConstraints().apply {
        fill = GridBagConstraints.CENTER
      })
    }
  }
}