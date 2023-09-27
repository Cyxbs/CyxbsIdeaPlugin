package com.cyxbs.idea.module.creator.utils

import java.awt.GridBagConstraints
import java.io.File

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

