package com.cyxbs.idea.module.creator.wizard.dependencies

import com.cyxbs.idea.module.creator.libraries.LibraryDataSource
import com.cyxbs.idea.module.creator.libraries.data.CyxbsLibrary
import com.cyxbs.idea.module.creator.modules.ModulesDataSource
import com.cyxbs.idea.module.creator.modules.data.*
import com.cyxbs.idea.module.creator.utils.capitalized
import com.cyxbs.idea.module.creator.wizard.cyxbs.ICyxbsParentWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.applications.ApplicationsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.components.ComponentsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.functions.FunctionsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.pages.PagesWizardStep
import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

object DependWizardStepManager {

  private var mCyxbsDependWizardStep: CyxbsDependWizardStep? = null

  private val mApplicationsDependWizardStep = JPanel(GridBagLayout()).apply {
    add(panel {
      row {
        text("${CyxbsGroup.Applications.step} 类型模块会自动依赖所有子模块")
          .horizontalAlign(HorizontalAlign.CENTER)
      }
      row {
        text("如果想做特殊设置，请在 build-logic 中单独设置")
          .horizontalAlign(HorizontalAlign.CENTER)
      }
      group("最后") {
        row {
          text("创建后请在 build-logic 中实现单独的 ApplicationConfig")
            .horizontalAlign(HorizontalAlign.CENTER)
        }
      }
    }, GridBagConstraints().apply {
      fill = GridBagConstraints.CENTER
    })
  }

  private var mSelectedModules: List<TreeNodeData> = emptyList()
  private var mSelectedLibraries: List<TreeNodeData> = emptyList()

  fun createWizardSteps(
    wizardContext: WizardContext,
    modulesProvider: ModulesProvider
  ): Array<ModuleWizardStep> {
    val wizardStep = CyxbsDependWizardStep(wizardContext) { modules, libraries ->
      // 不能使用源集合
      mSelectedModules = modules.toList()
      mSelectedLibraries = libraries.toList()
    }
    mCyxbsDependWizardStep = wizardStep
    return arrayOf(wizardStep)
  }

  fun getStepMap(groupName: String, parent: ICyxbsParentWizardStep): Map<String, NewProjectWizardStep> {
    val map = LinkedHashMap<String, NewProjectWizardStep>()
    // 如果是单独点击的 cyxbs-pages、cyxbs-functions 等就只显示对应的模块选项
    when (groupName) {
      CyxbsGroup.Pages.groupName -> map[CyxbsGroup.Pages.step] = PagesWizardStep(parent)
      CyxbsGroup.Functions.groupName -> map[CyxbsGroup.Functions.step] = FunctionsWizardStep(parent)
      CyxbsGroup.Components.groupName -> map[CyxbsGroup.Components.step] = ComponentsWizardStep(parent)
      CyxbsGroup.Applications.groupName -> map[CyxbsGroup.Applications.step] = ApplicationsWizardStep(parent)
      else -> {
        map[CyxbsGroup.Pages.step] = PagesWizardStep(parent)
        map[CyxbsGroup.Functions.step] = FunctionsWizardStep(parent)
        map[CyxbsGroup.Components.step] = ComponentsWizardStep(parent)
        map[CyxbsGroup.Applications.step] = ApplicationsWizardStep(parent)
      }
    }
    return map
  }

  fun updateDependWizardStep(step: String) {
    when (step) {
      CyxbsGroup.Applications.step -> mCyxbsDependWizardStep?.setSpecificContentPanel(mApplicationsDependWizardStep)
      CyxbsGroup.Components.step -> mCyxbsDependWizardStep?.update(
        LibraryDataSource.copyCyxbsLibraries().toTreeNodeData(),
        listOf(ModulesDataSource.copyComponentModules().toTreeNodeData())
      )
      CyxbsGroup.Functions.step -> mCyxbsDependWizardStep?.update(
        LibraryDataSource.copyCyxbsLibraries().toTreeNodeData(),
        ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules().toTreeNodeData(),
            source.copyFunctionModules().toTreeNodeData(),
          )
        }
      )
      CyxbsGroup.Pages.step -> mCyxbsDependWizardStep?.update(
        LibraryDataSource.copyCyxbsLibraries().toTreeNodeData(),
        ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules().toTreeNodeData(),
            source.copyFunctionModules().toTreeNodeData(),
            source.copyPageModules().toTreeNodeData(),
          )
        }
      )
    }
  }

  /**
   * 获取当前已经选择依赖的模块，转换为 depend* 形式
   */
  fun getDependModules(): List<String> {
    return mSelectedModules.map { data ->
      println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "getDependModules   data = $data")
      val name = data.title.split("-").joinToString("") { it.capitalized() }
      "depend$name()"
    }
  }

  /**
   * 获取当前已经选择的依赖，转换为 depend* 形式
   */
  fun getDependLibraries(): List<String> {
    return mSelectedLibraries.map { data ->
      println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "getDependLibraries   data = $data")
      "depend${data.title}()"
    }
  }

  private fun List<CyxbsLibrary>.toTreeNodeData(): List<TreeNodeData> {
    return map { library ->
      TreeNodeData(library.group, library.description, CheckableType.Catalog, TreeNodeType.ParentNode(
        library.depends.map {
          TreeNodeData(it.name, it.description, CheckableType.Checkbox(), TreeNodeType.LeafNode)
        }
      ))
    }
  }

  private inline fun <reified T : CommonModule> List<T>.toTreeNodeData(): TreeNodeData {
    val childrenNode = TreeNodeType.ParentNode(
      map {
        if (it.api != null) it.api!! else it
      }.map {
        TreeNodeData(it.name, it.description, CheckableType.Checkbox(), TreeNodeType.LeafNode)
      }
    )
    return when (T::class) {
      ApplicationModule::class -> TreeNodeData(CyxbsGroup.Applications.groupName, listOf(
        "壳工程",
        "1. 用于以后单模块打包多应用",
      ), CheckableType.Catalog, childrenNode)
      ComponentModule::class -> TreeNodeData(CyxbsGroup.Components.groupName, listOf(
        "公用组件层",
        "1. 不包含 api 模块",
        "2. 模块内代码基本都可向外暴露",
        "3. 被大部分模块使用",
      ), CheckableType.Catalog, childrenNode)
      FunctionModule::class -> TreeNodeData(CyxbsGroup.Functions.groupName, listOf(
        "功能层",
        "1. 不包含界面",
        "2. 并不是大部分模块都需要",
        "",
        "默认需要 api 模块，但如果不存在其他模块需要依赖时可不设置 api 模块"
      ), CheckableType.Catalog, childrenNode)
      PageModule::class -> TreeNodeData(CyxbsGroup.Pages.groupName, listOf(
        "页面层",
        "1. 包含页面布局",
        "",
        "强制提供 api 模块"
      ), CheckableType.Catalog, childrenNode)
      else -> error("未知类型: ${T::class.qualifiedName}")
    }
  }
}