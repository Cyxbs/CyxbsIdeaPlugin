package com.cyxbs.idea.module.creator.model

import com.android.tools.idea.npw.model.MultiTemplateRenderer
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.observable.core.BoolProperty
import com.android.tools.idea.observable.core.BoolValueProperty
import com.android.tools.idea.observable.core.OptionalProperty
import com.android.tools.idea.observable.core.OptionalValueProperty
import com.android.tools.idea.observable.core.StringProperty
import com.android.tools.idea.observable.core.StringValueProperty
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.Recipe
import com.android.tools.idea.wizard.template.TemplateData
import com.cyxbs.idea.module.creator.file.ChildModuleFileBuilder
import com.cyxbs.idea.module.creator.file.ParentModuleFileBuilder
import com.cyxbs.idea.module.creator.wizard.base.BaseModuleModel
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.intellij.openapi.project.Project

/**
 * .
 *
 * @author 985892345
 * 2023/10/5 15:10
 */
class CyxbsWizardModel(
  project: Project,
  moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker
) : BaseModuleModel(project, moduleParent, projectSyncInvoker) {

  // 必需参数
  val cyxbsGroup: OptionalProperty<CyxbsGroup?> = OptionalValueProperty()

  // 非必需参数
  val isSingleModule: BoolProperty = BoolValueProperty(false)
  val isNeedApiModule: BoolProperty = BoolValueProperty(false)
  val dependModules: OptionalProperty<List<String>?> = OptionalValueProperty()
  val dependLibraries: OptionalProperty<List<String>?> = OptionalValueProperty()
  val description: StringProperty = StringValueProperty()

  // 这里点击最后 Finish 的回调，在这个地方创建模版文件
  override val renderer: MultiTemplateRenderer.TemplateRenderer = object : ModuleTemplateRenderer() {
    override val recipe: Recipe get() = { td: TemplateData ->
      td as ModuleTemplateData
      val moduleName = moduleName.get()
      if (!moduleName.contains("/")) {
        if (isNeedApiModule.get()) {
          ChildModuleFileBuilder.generate(td, this,
            moduleName, "api-${moduleName}", cyxbsGroup.value)
        }
        ParentModuleFileBuilder.generate(td, this, moduleName,
          cyxbsGroup.value, isSingleModule.get(), dependModules.valueOrNull, dependLibraries.valueOrNull,
          description.get())
      } else {
        val parentModule = moduleName.substringBefore("/")
        val childModule = moduleName.substringAfter("/")
        ChildModuleFileBuilder.generate(td, this, parentModule, childModule, cyxbsGroup.value)
      }
    }
  }
}