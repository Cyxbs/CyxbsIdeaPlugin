package com.cyxbs.idea.module.creator.wizard.dependencies.desription

import com.intellij.ide.starters.JavaStartersBundle
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.JBColor
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.uiDesigner.core.AbstractLayout.DEFAULT_VGAP
import com.intellij.util.ui.*
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.*
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import kotlin.math.max

/**
 * 用于显示依赖描述的控件
 *
 * 逻辑来自官方 StarterLibrariesStep 中的 libraryDescriptionPanel
 */
class LibraryDescriptionPanel : ScrollablePanel(VerticalLayout(DEFAULT_VGAP)) {

  private val descriptionText: JTextArea = JTextArea()
  private val linksPanel: JPanel = JPanel(WrappedFlowLayout())

  init {
    this.border = JBUI.Borders.empty(DEFAULT_VGAP)

    descriptionText.background = JBColor.PanelBackground
    descriptionText.isFocusable = false
    descriptionText.lineWrap = true
    descriptionText.wrapStyleWord = true
    descriptionText.isEditable = false
    descriptionText.font = JBUI.Fonts.label()
    add(descriptionText)

    linksPanel.border = JBUI.Borders.emptyTop(UIUtil.DEFAULT_VGAP * 2)
    add(linksPanel)

    showEmptyState()
  }

  fun update(description: List<String>) {
    descriptionText.text = description.filterNot { it.startsWith("http") }.joinToString("\n")
    addDescriptionLinks(linksPanel, description.filter { it.startsWith("http") })
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

  override fun getComponentGraphics(graphics: Graphics?): Graphics {
    return JBSwingUtilities.runGlobalCGTransform(this, super.getComponentGraphics(graphics))
  }

  private fun addDescriptionLinks(linksPanel: JPanel, urls: List<String>) {
    linksPanel.removeAll()
    for (url in urls) {
      if (url.contains('{')) continue // URL templates are not supported

      val linkLabel = HyperlinkLabel()
      linkLabel.font = JBUI.Fonts.smallFont()
      linkLabel.setHyperlinkTarget(url)
      linkLabel.toolTipText = url

      linksPanel.add(BorderLayoutPanel().apply {
        addToCenter(linkLabel)
        border = JBUI.Borders.emptyRight(UIUtil.DEFAULT_HGAP / 2)
      })
    }
    linksPanel.revalidate()
    linksPanel.repaint()
  }

  // do not add horizontal gap - it is inserted before the first component
  private class WrappedFlowLayout : FlowLayout(LEADING, 0, UIUtil.DEFAULT_VGAP) {
    override fun preferredLayoutSize(target: Container): Dimension {
      val baseSize = super.preferredLayoutSize(target)
      if (alignOnBaseline) return baseSize
      return getWrappedSize(target)
    }

    private fun getWrappedSize(target: Container): Dimension {
      val parent = SwingUtilities.getUnwrappedParent(target)
      val maxWidth = parent.width - (parent.insets.left + parent.insets.right)
      return getDimension(target, maxWidth)
    }

    private fun getDimension(target: Container, maxWidth: Int): Dimension {
      val insets = target.insets
      var height = insets.top + insets.bottom
      var width = insets.left + insets.right
      var rowHeight = 0
      var rowWidth = insets.left + insets.right
      var isVisible = false
      var start = true

      synchronized(target.treeLock) {
        for (i in 0 until target.componentCount) {
          val component = target.getComponent(i)
          if (component.isVisible) {
            isVisible = true
            val size = component.preferredSize
            if (rowWidth + hgap + size.width > maxWidth && !start) {
              height += vgap + rowHeight
              width = max(width, rowWidth)
              rowWidth = insets.left + insets.right
              rowHeight = 0
            }
            rowWidth += hgap + size.width
            rowHeight = max(rowHeight, size.height)
            start = false
          }
        }

        height += vgap + rowHeight
        width = max(width, rowWidth)
        if (!isVisible) {
          return super.preferredLayoutSize(target)
        }

        return Dimension(width, height)
      }
    }

    override fun minimumLayoutSize(target: Container): Dimension {
      return if (alignOnBaseline) super.minimumLayoutSize(target) else getWrappedSize(target)
    }
  }
}