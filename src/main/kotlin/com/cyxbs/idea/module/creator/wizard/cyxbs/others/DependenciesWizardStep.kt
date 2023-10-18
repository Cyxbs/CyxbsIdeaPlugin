package com.cyxbs.idea.module.creator.wizard.cyxbs.others

import com.android.tools.idea.wizard.model.ModelWizardStep
import com.cyxbs.idea.module.creator.wizard.dependencies.DependenciesPanel
import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType
import com.cyxbs.idea.module.creator.model.CyxbsWizardModel
import com.cyxbs.idea.module.libraries.LibrariesDataSource
import com.cyxbs.idea.module.libraries.data.CyxbsLibrary
import com.cyxbs.idea.module.modules.ModulesDataSource
import com.cyxbs.idea.module.modules.data.*
import com.cyxbs.idea.module.modules.properties.CyxbsProperties
import com.cyxbs.idea.module.utils.capitalized
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.JBInsets
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * .
 *
 * @author 985892345
 * 2023/10/6 20:30
 */
class DependenciesWizardStep(
  model: CyxbsWizardModel,
) : ModelWizardStep<CyxbsWizardModel>(model, "DependenciesWizardStep") {

  private val mContentPanel by lazy {
    JPanel(GridLayout(2, 1)).apply {
      border = IdeBorderFactory.createEmptyBorder(JBInsets(10, 10, 10, 10))
      add(mModulesPanel)
      add(mLibrariesPanel)
    }
  }

  private val mModulesPanel by lazy {
    DependenciesPanel {
      model.dependModules.setNullableValue(
        it.map { data ->
          val name = data.title.split("-").joinToString("") { s -> s.capitalized() }
          "depend$name()"
        }
      )
    }
  }

  private val mLibrariesPanel by lazy {
    DependenciesPanel {
      model.dependLibraries.setNullableValue(
        it.map { data ->
          "depend${data.title}()"
        }
      )
    }
  }

  override fun getComponent(): JComponent {
    return mContentPanel
  }

  override fun onEntering() {
    super.onEntering()
    updateDependWizardStep()
  }

  private fun updateDependWizardStep() {
    val isSingleModule = model.isSingleModule.get()
    when (model.cyxbsGroup.value) {
      CyxbsGroup.Applications -> {
        mModulesPanel.update(emptyList())
        mLibrariesPanel.update(emptyList())
      }

      CyxbsGroup.Components -> {
        mModulesPanel.update(listOf(ModulesDataSource.copyComponentModules().toTreeNodeData(isSingleModule)))
        mLibrariesPanel.update(LibrariesDataSource.copyCyxbsLibraries().toTreeNodeData(isSingleModule))
      }

      CyxbsGroup.Functions -> {
        mModulesPanel.update(ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules().toTreeNodeData(isSingleModule).configDefault(
              CyxbsProperties.functionsDefaultModules
            ),
            source.copyFunctionModules().toTreeNodeData(isSingleModule).configDefault(
              CyxbsProperties.functionsDefaultModules
            ),
          )
        })
        mLibrariesPanel.update(LibrariesDataSource.copyCyxbsLibraries().toTreeNodeData(isSingleModule))
      }

      CyxbsGroup.Pages -> {
        mModulesPanel.update(ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules().toTreeNodeData(isSingleModule).configDefault(
              CyxbsProperties.pagesDefaultModules
            ),
            source.copyFunctionModules().toTreeNodeData(isSingleModule).configDefault(
              CyxbsProperties.pagesDefaultModules
            ),
            source.copyPageModules().toTreeNodeData(isSingleModule).configDefault(
              CyxbsProperties.pagesDefaultModules
            ),
          )
        })
        mLibrariesPanel.update(
          LibrariesDataSource.copyCyxbsLibraries().toTreeNodeData(isSingleModule)
        )
      }
    }
  }

  /**
   * 转换依赖数据为树形列表数据
   */
  private fun List<CyxbsLibrary>.toTreeNodeData(isSingleModule: Boolean): List<TreeNodeData> {
    return map { library ->
      TreeNodeData(library.group, library.description, CheckableType.Catalog, TreeNodeType.ParentNode(
        library.depends.map { dependItem ->
          TreeNodeData(dependItem.name, dependItem.description, CheckableType.Checkbox(
            isDefault = isSingleModule && CyxbsProperties.singleModuleDefaultLibraries.any { dependItem.name.contains(it) }
          ), TreeNodeType.LeafNode)
        }
      ))
    }
  }

  /**
   * 转换模块数据为树形列表数据
   */
  private inline fun <reified T : CommonModule> List<T>.toTreeNodeData(isSingleModule: Boolean): TreeNodeData {
    val childrenNode = TreeNodeType.ParentNode(
      map {
        if (it.api != null) it.api!! else it
      }.map { module ->
        TreeNodeData(module.name, module.description, CheckableType.Checkbox(
          isDefault = isSingleModule && CyxbsProperties.singleModuleDefaultModules.any { module.name.contains(it) }
        ), TreeNodeType.LeafNode)
      }
    )
    return when (T::class) {
      ApplicationModule::class -> TreeNodeData(
        CyxbsGroup.Applications.groupName, listOf(
          "壳工程",
          "1. 用于以后单模块打包多应用",
        ), CheckableType.Catalog, childrenNode
      )

      ComponentModule::class -> TreeNodeData(
        CyxbsGroup.Components.groupName, listOf(
          "公用组件层",
          "1. 不包含 api 模块",
          "2. 模块内代码基本都可向外暴露",
          "3. 被大部分模块使用",
        ), CheckableType.Catalog, childrenNode
      )

      FunctionModule::class -> TreeNodeData(
        CyxbsGroup.Functions.groupName, listOf(
          "功能层",
          "1. 不包含界面",
          "2. 并不是大部分模块都需要",
          "",
          "默认需要 api 模块，但如果不存在其他模块需要依赖时可不设置 api 模块"
        ), CheckableType.Catalog, childrenNode
      )

      PageModule::class -> TreeNodeData(
        CyxbsGroup.Pages.groupName, listOf(
          "页面层",
          "1. 包含页面布局",
          "",
          "强制提供 api 模块"
        ), CheckableType.Catalog, childrenNode
      )

      else -> error("未知类型: ${T::class.qualifiedName}")
    }
  }

  /**
   * 配置 [CheckableType.Checkbox.isDefault]
   * @param keywords 关键词集合，由 [TreeNodeData.title] 进行匹配
   */
  private fun TreeNodeData.configDefault(keywords: List<String>): TreeNodeData {
    fun config(data: TreeNodeData) {
      when (data.checkableType) {
        CheckableType.Catalog -> {
          if (data.treeNode is TreeNodeType.ParentNode) {
            data.treeNode.children.forEach {
              config(it)
            }
          }
        }
        is CheckableType.Checkbox -> {
          if (keywords.any { data.title.contains(it) }) {
            data.checkableType.isDefault = true
          }
          if (data.treeNode is TreeNodeType.ParentNode) {
            data.treeNode.children.forEach {
              config(it)
            }
          }
        }
      }
    }
    config(this)
    return this
  }
}