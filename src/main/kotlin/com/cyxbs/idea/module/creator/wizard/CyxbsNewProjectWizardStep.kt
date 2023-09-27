package com.cyxbs.idea.module.creator.wizard

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.cyxbs.ICyxbsParentWizardStep
import com.cyxbs.idea.module.creator.wizard.dependencies.DependWizardStepManager
import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStepBase
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.observable.util.transform
import com.intellij.openapi.observable.util.trim
import com.intellij.openapi.ui.validation.*
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.JPanel

class CyxbsNewProjectWizardStep(
  parent: NewProjectWizardStep,
) : AbstractNewProjectWizardMultiStepBase(parent) {

  override val label: String
    get() = "模块分组:"

  override fun initSteps(): Map<String, NewProjectWizardStep> {
    return DependWizardStepManager.getStepMap(context.projectName, mCyxbsParentWizardStep)
  }

  private val mCyxbsParentWizardStep = ParentWizardStepImpl()

  private val newProjectNameProperty = propertyGraph.lazyProperty { "" }
  private var newProjectName by newProjectNameProperty

  override fun setupUI(builder: Panel) {
    with(builder) {
      row("模块名字:") {
        val commentProperty = stepProperty.joinCanonicalPath(newProjectNameProperty)
          .transform { "位置: cyxbs-$it" }
        textField()
          .bindText(newProjectNameProperty.trim())
          .columns(COLUMNS_MEDIUM)
          .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
          .trimmedTextValidation(CHECK_NON_EMPTY, mGroupCheck, mRepeatModuleCheck)
          .focused()
          .gap(RightGap.SMALL)
          .comment(commentProperty.get(), 100)
          .apply { commentProperty.afterChange { comment?.text = it } }
      }
      onApply {
        // 点击 next 到下一页时回调
        DependWizardStepManager.updateDependWizardStep(step)
      }
    }
    super.setupUI(builder)
  }

  private inner class ParentWizardStepImpl : ICyxbsParentWizardStep {
    override val wizardStep: NewProjectWizardStep
      get() = this@CyxbsNewProjectWizardStep
    override val newProjectName: String
      get() = this@CyxbsNewProjectWizardStep.newProjectName
    override val dependModules: List<String>
      get() = DependWizardStepManager.getDependModules()
    override val dependLibraries: List<String>
      get() = DependWizardStepManager.getDependLibraries()
  }


  private val mGroupCheck = validationErrorFor<String> { input ->
    when {
      !input.matches(Regex("^[a-z].*")) -> "模块名只能以小写字母开头"
      !input.matches(Regex("^[a-z][a-z0-9]*$")) -> "模块名只能包含小写字母和数字"
      input.length < 3 -> "模块名长度不能小于三个字符"
      else -> null
    }
  }

  private val mRepeatModuleCheck = validationErrorFor<String> { input ->
    val rootProjectPath = context.project?.basePath ?: return@validationErrorFor null
    val rootProjectFile = File(rootProjectPath)
    val group = CyxbsGroup.values().map { it.name }
    val isValid = rootProjectFile.listFiles { file ->
      group.contains(file.name)
    }!!.all {
      !it.resolve(input).exists()
    }
    if (isValid) null else {
      "模块名不允许重复"
    }
  }
}