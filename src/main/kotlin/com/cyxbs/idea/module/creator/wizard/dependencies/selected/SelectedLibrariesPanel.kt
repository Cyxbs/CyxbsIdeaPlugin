package com.cyxbs.idea.module.creator.wizard.dependencies.selected

import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.intellij.icons.AllIcons
import com.intellij.ide.starters.JavaStartersBundle
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.openapi.ui.popup.IconButton
import com.intellij.ui.InplaceButton
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.BorderLayout
import java.awt.Cursor
import javax.swing.JLabel

/**
 * 用于显示已被选中的列表控件
 *
 * 逻辑来自官方 StarterLibrariesStep 中的 selectedLibrariesPanel
 */
class SelectedLibrariesPanel(
  private val libraryRemoveListener: ((TreeNodeData) -> Unit)
): JBPanelWithEmptyText(BorderLayout()) {

  private val scrollablePanel: ScrollablePanel = ScrollablePanel(VerticalLayout(UIUtil.DEFAULT_VGAP))
  private val scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true)

  init {
    emptyText.text = JavaStartersBundle.message("hint.dependencies.not.selected")
    this.background = UIUtil.getListBackground()

    add(scrollPane, BorderLayout.CENTER)

    scrollablePanel.border = JBUI.Borders.empty(5)
    scrollablePanel.background = UIUtil.getListBackground()
    scrollPane.isVisible = false
  }

  fun update(libraries: Collection<TreeNodeData>) {
    scrollablePanel.removeAll()

    for (library in libraries) {
      val dependencyPanel = BorderLayoutPanel()
      dependencyPanel.background = UIUtil.getListBackground()

      val dependencyLabel = JLabel(library.title)
      dependencyLabel.border = JBUI.Borders.empty(0, UIUtil.DEFAULT_HGAP / 2, UIUtil.DEFAULT_VGAP, 0)

      val removeButton = InplaceButton(
        IconButton(
          JavaStartersBundle.message("button.tooltip.remove"),
          AllIcons.Actions.Close, AllIcons.Actions.CloseHovered)
      ) {
        libraryRemoveListener.invoke(library)
      }
      removeButton.setTransform(0, - JBUIScale.scale(2.coerceAtLeast(dependencyLabel.font.size / 15)))
      removeButton.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

      dependencyPanel.addToLeft(removeButton)
      dependencyPanel.addToCenter(dependencyLabel)

      scrollablePanel.add(dependencyPanel)
    }
    scrollPane.isVisible = scrollablePanel.componentCount > 0

    scrollablePanel.revalidate()
    scrollPane.revalidate()
    revalidate()
  }
}