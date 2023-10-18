package com.cyxbs.idea.module.creator.wizard.cyxbs.api

import com.android.tools.idea.npw.model.MultiTemplateRenderer
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.Recipe
import com.android.tools.idea.wizard.template.TemplateData
import com.cyxbs.idea.module.creator.wizard.base.BaseModuleModel
import com.cyxbs.idea.module.creator.file.ApiModuleFileBuilder
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.intellij.openapi.project.Project

/**
 * .
 *
 * @author 985892345
 * 2023/10/7 11:22
 */
class ApiWizardModel(
  project: Project,
  moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker,
  cyxbsGroup: CyxbsGroup,
) : BaseModuleModel(project, moduleParent, projectSyncInvoker) {

  override val renderer: MultiTemplateRenderer.TemplateRenderer = object : ModuleTemplateRenderer() {
    override val recipe: Recipe get() = { td: TemplateData ->
      td as ModuleTemplateData
      ApiModuleFileBuilder.generate(td, this, moduleName.get(), cyxbsGroup)
    }
  }
}