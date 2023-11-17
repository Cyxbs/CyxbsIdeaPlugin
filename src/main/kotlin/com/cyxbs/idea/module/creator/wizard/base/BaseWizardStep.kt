package com.cyxbs.idea.module.creator.wizard.base

import com.android.tools.adtui.validation.Validator.Result
import com.android.tools.adtui.validation.ValidatorPanel
import com.android.tools.adtui.validation.createValidator
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.observable.core.ObservableBool
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.cyxbs.idea.module.creator.model.CyxbsWizardModel
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.cyxbs.idea.module.utils.toIdea
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.observable.util.transform
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.JBUI.Borders
import com.jetbrains.rd.util.first
import java.io.File
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants

/**
 * .
 *
 * @author 985892345
 * 2023/10/6 19:27
 */
abstract class BaseWizardStep(
  val project: Project,
  moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker,
  title: String = "",
) : SkippableWizardStep<CyxbsWizardModel>(
  CyxbsWizardModel(project, moduleParent, projectSyncInvoker),
  title
) {

  private val stepMap by lazy { initStep() }

  protected val propertyGraph = PropertyGraph("")

  protected val stepProperty by lazy { propertyGraph.property(stepMap.first().key) }

  private val rootPanel by lazy {
    JBScrollPane(
      validatorPanel,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    ).apply {
      border = Borders.empty() // Remove outer border line decoration
    }
  }

  // 合法性判断
  private val validatorPanel: ValidatorPanel by lazy {
    ValidatorPanel(this, createMainPanel()).apply {
      border = Borders.empty(16)
      registerValidator(model.moduleName, createValidator { input ->
        judgeInput(input)?.let { Result.fromNullableMessage(it) } ?: Result.OK
      })
    }
  }

  protected abstract fun initStep(): Map<CyxbsGroup, JComponent>

  final override fun getComponent(): JComponent {
    return rootPanel
  }

  protected open fun createMainPanel(): JComponent {
    return panel {
      row("模块名字:") {
        val commentProperty = stepProperty.transform { it.groupName }
          .joinCanonicalPath(model.moduleName.toIdea())
          .transform { "位置: $it" }
        textField()
          .apply {
            text(model.moduleName.get())
            whenTextChangedFromUi { model.moduleName.set(it) }
          }.focused()
          .horizontalAlign(HorizontalAlign.FILL)
          .apply {
            comment(commentProperty.get(), 100)
            commentProperty.afterChange { comment?.text = it }
          }
      }
      row("模块分组:") {
        segmentedButton(stepMap.keys) { it.step }
          .bind(stepProperty)
          .gap(RightGap.SMALL)
          .apply { stepProperty.afterChange { items(stepMap.keys) } }
      }
      row("模块描述:") {
        textField()
          .apply {
            text(model.description.get())
            whenTextChangedFromUi { model.description.set(it) }
          }.horizontalAlign(HorizontalAlign.FILL)
          .comment("建议一句话高度概括该模块负责哪个页面或功能")
      }
      row {
        val placeholder = placeholder()
          .horizontalAlign(HorizontalAlign.FILL)
        // todo horizontalAlign 将被移除，后面再改，官方也是用的这个，看 AbstractNewProjectWizardMultiStepBase
        placeholder.component = stepMap[stepMap.first().key]
        stepProperty.afterChange {
          placeholder.component = stepMap[it]
        }
      }
    }
  }

  public final override fun canGoForward(): ObservableBool = validatorPanel.hasErrors().not()

  /**
   * @return 返回 null 则表明合法，不返回 null 则表明非法原因
   */
  private fun judgeInput(input: String): String? {
    return when {
      !input.matches(Regex("^[a-z].*")) -> "模块名只能以小写字母开头"
      !input.replace("-", "")
        .replace("/", "")
        .matches(Regex("^[a-z][a-z0-9]*$")) -> "模块名只能包含小写字母和数字"
      input.length < 2 -> "模块名长度不能小于两个字符"
      else -> {
        val parentModule = input.substringBefore("/")
        judgeParentModuleName(parentModule)
          ?: if (input.contains("/")) {
            // 包含子模块
            judgeChildModuleName(parentModule, input.substringAfter("/"))
          } else {
            // 只有父模块
            val rootProjectFile = File(project.basePath!!)
            val group = CyxbsGroup.values().map { it.groupName }
            val isExists = rootProjectFile.listFiles { file ->
              group.contains(file.name)
            }?.any { it.resolve(input).exists() } ?: false
            if (!isExists) null else {
              "模块名不允许重复，但你可以输入斜杠 / 创建子模块"
            }
          }
      }
    }
  }

  private fun judgeParentModuleName(parentModule: String): String? {
    return listOf("api", "cyxbs", "src")
      .find { it == parentModule }
      ?.let { "父模块不能取名为 $it" }
      ?: if (parentModule.contains("-")) {
        "父模块不能包含 -"
      } else null
  }

  private fun judgeChildModuleName(parentModule: String, childModule: String): String? {
    return when {
      childModule.contains("/") -> "只能创建一层子模块"
      childModule.startsWith("api-$parentModule") -> null
      childModule.startsWith("${parentModule}-") -> {
        if (childModule.endsWith("-")) {
          "不能以 - 结尾"
        } else if (childModule.substringAfter("-").contains("-")) {
          "${childModule.substringBefore("-")} 不能再有 -"
        } else if (childModule.substringAfter("-").length == 1) {
          "${childModule.substringBefore("-")} 后的字符长度必须大于 1"
        } else null
      }

      else -> "子模块只能为 api-$parentModule 或者 ${parentModule}-xxx"
    }
  }
}