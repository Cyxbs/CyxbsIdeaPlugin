package com.cyxbs.idea.module.creator.wizard

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.wizard.group.GroupManager
import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStepBase
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.observable.util.transform
import com.intellij.openapi.observable.util.trim
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.validation.*
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

class CyxbsNewProjectWizardStep(
  parent: NewProjectWizardStep,
) : AbstractNewProjectWizardMultiStepBase(parent) {

  override val label: String
    get() = "模块分组:"

  override fun initSteps(): Map<String, NewProjectWizardStep> {
    return GroupManager.getStepMap(context.projectName, this)
  }

  private val newProjectNameProperty = propertyGraph.lazyProperty { "" }
  private var newProjectName by newProjectNameProperty

  override fun setupUI(builder: Panel) {
    if (!checkCyxbsMobileLite(context.project?.basePath)) {
      builder.row {
        cell(JPanel(GridBagLayout()).apply {
          add(JBLabel("检测到不是掌上重邮项目，所以该插件不能使用"), GridBagConstraints().apply {
            fill = GridBagConstraints.CENTER
          })
        }).horizontalAlign(HorizontalAlign.FILL)
      }
      return
    }
    with(builder) {
      row("模块名字:") {
        val commentProperty = stepProperty.joinCanonicalPath(newProjectNameProperty)
          .transform { "位置: cyxbs-$it" }
        textField()
          .bindText(newProjectNameProperty.trim())
          .columns(COLUMNS_MEDIUM)
          .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
          .trimmedTextValidation(CHECK_NON_EMPTY, GroupManager.GroupCheck { step },
            GroupManager.RepeatModuleCheck(context.project))
          .focused()
          .gap(RightGap.SMALL)
          .comment(commentProperty.get(), 100)
          .apply { commentProperty.afterChange { comment?.text = it } }
      }
      onApply {
        println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
          "onApply")
        // 点击 next 到下一页时回调
        GroupManager.updateDependWizardStep(step)
      }
    }
    stepProperty.afterChange {
      GroupManager.stepName = it
    }
    newProjectNameProperty.afterChange {
      GroupManager.moduleName = it
    }
    super.setupUI(builder)
  }

}