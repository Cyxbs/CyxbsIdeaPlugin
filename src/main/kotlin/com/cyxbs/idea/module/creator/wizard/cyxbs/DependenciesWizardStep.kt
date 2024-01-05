package com.cyxbs.idea.module.creator.wizard.cyxbs

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
import kotlin.reflect.KClass

/**
 * 二级页面
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

  // 选择模块面板
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

  // 选择依赖面板
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
        mModulesPanel.update(
          listOf(
            ModulesDataSource.copyComponentModules()
              .toTreeNodeData(ComponentModule::class, isSingleModule)
          )
        )
        mLibrariesPanel.update(
          LibrariesDataSource.copyCyxbsLibraries()
            .toTreeNodeData(isSingleModule)
        )
      }

      CyxbsGroup.Functions -> {
        mModulesPanel.update(ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules()
              .toTreeNodeData(ComponentModule::class, isSingleModule)
              .apply { configDefault(CyxbsProperties.functionsDefaultModules) },
            source.copyFunctionModules()
              .toTreeNodeData(FunctionModule::class, isSingleModule)
              .apply { configDefault(CyxbsProperties.functionsDefaultModules) },
          )
        })
        mLibrariesPanel.update(
          LibrariesDataSource.copyCyxbsLibraries()
            .toTreeNodeData(isSingleModule)
            .onEach { it.configDefault(CyxbsProperties.functionsDefaultLibraries) }
        )
      }

      CyxbsGroup.Pages -> {
        mModulesPanel.update(ModulesDataSource.let { source ->
          listOf(
            source.copyComponentModules()
              .toTreeNodeData(ComponentModule::class, isSingleModule)
              .apply { configDefault(CyxbsProperties.pagesDefaultModules) },
            source.copyFunctionModules()
              .toTreeNodeData(FunctionModule::class, isSingleModule)
              .apply { configDefault(CyxbsProperties.pagesDefaultModules) },
            source.copyPageModules()
              .toTreeNodeData(PageModule::class, isSingleModule)
              .apply { configDefault(CyxbsProperties.pagesDefaultModules) },
          )
        })
        mLibrariesPanel.update(
          LibrariesDataSource.copyCyxbsLibraries()
            .toTreeNodeData(isSingleModule)
            .onEach { it.configDefault(CyxbsProperties.pagesDefaultLibraries) }
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
  private fun <T : CommonModule> List<T>.toTreeNodeData(
    moduleClass: KClass<T>,
    isSingleModule: Boolean
  ): TreeNodeData {
    fun getCheckbox(module: T): CheckableType.Checkbox = CheckableType.Checkbox(
      isDefault = isSingleModule && CyxbsProperties.singleModuleDefaultModules.any { module.name.contains(it) }
    )
    val childrenNode = TreeNodeType.ParentNode(
      map { module ->
        val api = module.api
        val children = module.children
        if (api == null && children.isEmpty()) {
          // 叶节点，当前父模块
          TreeNodeData(module.name, module.description, getCheckbox(module), TreeNodeType.LeafNode)
        } else if (api != null && children.isEmpty()) {
          // 叶节点，但只显示 api 模块 (拥有 api 模块的不显示父模块)
          TreeNodeData(api.name, api.description, getCheckbox(module), TreeNodeType.LeafNode)
        } else {
          // 以父模块作为目录，所有子模块作为其下的叶节点
          val moduleList = mutableListOf<CyxbsModule>()
          if (module.file.resolve("build.gradle.kts").exists()) moduleList.add(module)
          if (api != null) moduleList.add(api)
          moduleList.addAll(children)
          TreeNodeData(module.name, module.description, CheckableType.Catalog,
            TreeNodeType.ParentNode(
              moduleList.map { TreeNodeData(it.name, it.description, getCheckbox(module), TreeNodeType.LeafNode) }
            )
          )
        }
      }
    )
    return when (moduleClass) {
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

      else -> error("未知类型: ${moduleClass.qualifiedName}")
    }
  }

  /**
   * 配置 [CheckableType.Checkbox.isDefault]
   * @param moduleName 由 [TreeNodeData.title] 进行匹配
   *
   */
  private fun TreeNodeData.configDefault(moduleName: List<String>) {
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
          if (moduleName.any { data.title == it }) {
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
  }
}