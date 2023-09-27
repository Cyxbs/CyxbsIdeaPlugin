package com.cyxbs.idea.module.creator

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleDescriptionProvider
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.android.tools.idea.wizard.model.WizardModel
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import javax.swing.Icon
import javax.swing.JComponent

/**
 * .
 *
 * @author 985892345
 * 2023/9/27 15:16
 */
class AndroidCyxbsModuleBuilder : ModuleDescriptionProvider {

  object Icons {
    val CyxbsIcon = IconLoader.getIcon("/icons/cyxbs_icon.svg", Icons::class.java)
  }

  override fun getDescriptions(project: Project): Collection<ModuleGalleryEntry> {
    return listOf(
      AndroidCyxbsModuleGalleryEntry(),
    )
  }

  private class AndroidCyxbsModuleGalleryEntry : ModuleGalleryEntry {
    override val description: String
      get() = "掌上重邮极速版模块构建工具"
    override val icon: Icon
      get() = Icons.CyxbsIcon
    override val name: String
      get() = "CyxbsModuleBuilder"

    override fun createStep(
      project: Project,
      moduleParent: String,
      projectSyncInvoker: ProjectSyncInvoker
    ): SkippableWizardStep<*> {
      return AndroidCyxbsSkippableWizardStep()
    }
  }

  private class AndroidCyxbsSkippableWizardStep: SkippableWizardStep<AndroidCyxbsWizardModel>(
    AndroidCyxbsWizardModel(),
    "Test Title"
  ) {
    override fun getComponent(): JComponent {
      return panel {
        row("test") {
          textField()
            .columns(COLUMNS_LARGE)
        }
      }.withVisualPadding()
    }
  }

  private class AndroidCyxbsWizardModel : WizardModel() {
    override fun handleFinished() {

    }
  }
}