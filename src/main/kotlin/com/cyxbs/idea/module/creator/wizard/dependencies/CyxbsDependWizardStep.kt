package com.cyxbs.idea.module.creator.wizard.dependencies

import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.file.FileBuilderWizardStep
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.JComponent

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
    FileBuilderWizardStep.Modules = mSelectedModules.map { data ->
      val name = data.title.split("-").joinToString("") { ele ->
        ele.replaceFirstChar { it.uppercaseChar() }
      }
      "depend$name()"
    }
    FileBuilderWizardStep.Libraries = mSelectedLibraries.map { data ->
      "depend${data.title}()"
    }
  }
}