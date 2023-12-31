package com.cyxbs.idea.module.utils

import com.intellij.openapi.util.IconLoader
import java.awt.GridBagConstraints
import java.io.File

object Utils

internal val CyxbsIcon = IconLoader.getIcon("/icons/cyxbs_icon.svg", Utils::class.java)

// 检查是否是掌邮极速版项目
internal fun checkCyxbsMobileLite(projectPath: String?): Boolean {
  if (projectPath == null) return false
  val file = File(projectPath)
  return file.resolve("cyxbs-applications").exists()
      || file.resolve("cyxbs-components").exists()
      || file.resolve("cyxbs-functions").exists()
      || file.resolve("cyxbs-pages").exists()
}

internal fun gridConstraint(col: Int, row: Int, xWeight: Double = 1.0, yWeight: Double = 1.0): GridBagConstraints {
  return GridBagConstraints().apply {
    fill = GridBagConstraints.BOTH
    gridx = col
    gridy = row
    weightx = xWeight
    weighty = yWeight
  }
}

internal fun String.capitalized(): String {
  return replaceFirstChar { it.uppercase() }
}


