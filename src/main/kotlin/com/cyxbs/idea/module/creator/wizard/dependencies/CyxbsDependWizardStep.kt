package com.cyxbs.idea.module.creator.wizard.dependencies

import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.JComponent

class CyxbsDependWizardStep(
  private val context: WizardContext,
  private val selected: (modules: List<TreeNodeData>, libraries: List<TreeNodeData>) -> Unit
) : ModuleWizardStep() {

  private val mModules = mutableListOf<TreeNodeData>()
  private val mLibraries = mutableListOf<TreeNodeData>()

  private val mSelectedModules = mutableListOf<TreeNodeData>()
  private val mSelectedLibraries = mutableListOf<TreeNodeData>()

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
      mSelectedLibraries.clear()
      mSelectedModules.clear()
    } else {
      mTopLevelPanel.removeAll()
      mTopLevelPanel.addToCenter(mContentPanel)
      mLibrariesPanel.update(mLibraries)
      mModulesPanel.update(mModules)
    }
  }

  // 每次进入下一页都会回调该方法，但这个类的对象都是同一个
  override fun getComponent(): JComponent {
    return mTopLevelPanel
  }

  override fun updateDataModel() {
    // 因为在 updateDataModel 后会重新触发 _init，然后选择的数据被情况
    // 所以在这里对外面进行回调
    selected.invoke(mSelectedModules, mSelectedLibraries)
  }
}