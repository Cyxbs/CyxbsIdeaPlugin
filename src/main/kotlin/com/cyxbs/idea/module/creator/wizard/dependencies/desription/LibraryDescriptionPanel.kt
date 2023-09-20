package com.cyxbs.idea.module.creator.wizard.dependencies.desription

import com.intellij.ide.starters.JavaStartersBundle
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.uiDesigner.core.AbstractLayout.DEFAULT_VGAP
import com.intellij.util.ui.*
import java.awt.*
import javax.swing.JTextArea

/**
 * 用于显示依赖描述的控件
 *
 * 逻辑来自官方 StarterLibrariesStep 中的 libraryDescriptionPanel
 */
class LibraryDescriptionPanel : ScrollablePanel(VerticalLayout(DEFAULT_VGAP)) {

  private val descriptionText: JTextArea = JTextArea()

  private val emptyState: StatusText = object : StatusText(this) {
    override fun isStatusVisible(): Boolean {
      return UIUtil.uiChildren(this@LibraryDescriptionPanel)
        .filter { obj: Component -> obj.isVisible }
        .isEmpty
    }
  }

  init {
    this.border = JBUI.Borders.empty(DEFAULT_VGAP)

    descriptionText.background = JBColor.PanelBackground
    descriptionText.isFocusable = false
    descriptionText.lineWrap = true
    descriptionText.wrapStyleWord = true
    descriptionText.isEditable = false
    descriptionText.font = JBUI.Fonts.label()
    add(descriptionText)

    emptyState.text = JavaStartersBundle.message("hint.no.library.selected")

    showEmptyState()
  }

  fun update(description: String?) {
    descriptionText.text = description
    showDescriptionUi()
  }

  private fun showEmptyState() {
    for (component in this.components) {
      component.isVisible = false
    }
    revalidate()
    repaint()
  }

  private fun showDescriptionUi() {
    for (component in this.components) {
      component.isVisible = true
    }
    revalidate()
    repaint()
  }

  override fun paintComponent(g: Graphics?) {
    super.paintComponent(g)
    emptyState.paint(this, g)
  }

  override fun getComponentGraphics(graphics: Graphics?): Graphics {
    return JBSwingUtilities.runGlobalCGTransform(this, super.getComponentGraphics(graphics))
  }
}