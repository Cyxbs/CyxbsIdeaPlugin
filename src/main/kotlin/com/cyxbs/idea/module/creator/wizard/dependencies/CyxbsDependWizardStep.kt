package com.cyxbs.idea.module.creator.wizard.dependencies

import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.file.FileBuilder
import com.cyxbs.idea.module.creator.wizard.group.GroupManager
import com.intellij.ide.projectWizard.NewProjectWizardCollector.Companion.logGitChanged
import com.intellij.ide.projectWizard.NewProjectWizardCollector.Companion.logGitFinished
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.JComponent
import com.intellij.ide.projectWizard.NewProjectWizardCollector.Companion.logProjectCreated
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

class CyxbsDependWizardStep(
  private val context: WizardContext
) : ModuleWizardStep() {

  private val mLibraries = mutableListOf<TreeNodeData>()
  private val mModules = mutableListOf<TreeNodeData>()

  private val mSelectedLibraries = mutableListOf<TreeNodeData>()
  private val mSelectedModules = mutableListOf<TreeNodeData>()

  private val mTopLevelPanel = BorderLayoutPanel()

  private val mContentPanel by lazy {
    panel {
      group("依赖模块:") {
        row {
          cell(mModulesPanel).horizontalAlign(HorizontalAlign.FILL)
        }
      }
      group("添加依赖:") {
        row {
          cell(mLibrariesPanel).horizontalAlign(HorizontalAlign.FILL)
        }
      }
    }.withVisualPadding()
  }

  private val mModulesPanel by lazy {
    DependenciesPanel {
      mSelectedModules.clear()
      mSelectedModules.addAll(it)
    }
  }

  private val mLibrariesPanel by lazy {
    DependenciesPanel {
      mSelectedLibraries.clear()
      mSelectedLibraries.addAll(it)
    }
  }

  private var mSpecificContentPanel: JComponent? = null

  fun update(libraries: Collection<TreeNodeData>, modules: Collection<TreeNodeData>) {
    mLibraries.clear()
    mLibraries.addAll(libraries)
    mModules.clear()
    mModules.addAll(modules)
    mSpecificContentPanel = null
  }

  fun setSpecificContentPanel(panel: JComponent) {
    mSpecificContentPanel = panel
  }

  override fun _init() {
    super._init()
    val specificPanel = mSpecificContentPanel
    if (specificPanel != null) {
      mTopLevelPanel.removeAll()
      mTopLevelPanel.addToCenter(specificPanel)
    } else {
      mTopLevelPanel.removeAll()
      mTopLevelPanel.addToCenter(mContentPanel)
      mSelectedLibraries.clear()
      mSelectedModules.clear()
      mLibrariesPanel.update(mLibraries)
      mModulesPanel.update(mModules)
    }
  }

  // 每次进入下一页都会回调该方法，但这个类的对象都是同一个
  override fun getComponent(): JComponent {
    return mTopLevelPanel
  }

  override fun updateDataModel() {
    println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "updateDataModel")
    val moduleFile = GroupManager.getModuleFile(context.project) ?: return
    println(".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "moduleFile = $moduleFile")
    FileBuilder.appendDependModule(moduleFile, mSelectedModules.map { data ->
      val name = data.title.split("-").joinToString("") { ele ->
        ele.replaceFirstChar { it.uppercaseChar() }
      }
      "depend$name()"
    })
    FileBuilder.appendDependLibrary(moduleFile, mSelectedLibraries.map { data ->
      "depend${data.title}()"
    })
//    val project = context.project ?: return
//    project.projectFile?.refresh(true, true)
  }
}