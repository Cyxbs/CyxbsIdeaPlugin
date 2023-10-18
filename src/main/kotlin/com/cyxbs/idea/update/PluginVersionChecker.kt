package com.cyxbs.idea.update

import com.cyxbs.idea.BuildConfig
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import java.io.File

/**
 * 检查本地项目内是否存在高版本的插件，存在的话则不允许低版本使用，强制要求升级
 *
 * @author 985892345
 * 2023/9/27 13:30
 */
object PluginVersionChecker {

  /**
   * @return true: 不用更新, false: 需要更新, null: 未在项目目录下找到 jar 包
   */
  fun check(project: Project): Boolean? {
    val oldVersion = BuildConfig.VERSION
    val newVersion = getNewVersion(project) ?: return null
    return compareVersion(oldVersion, newVersion) >= 0
  }

  private fun getNewVersion(project: Project): String? {
    val projectFile = File(project.basePath!!)
    val list = projectFile.listFiles()!!
      .filter { it.name.matches(Regex("CyxbsIdeaPlugin-[0-9]+\\.[0-9]+\\.jar")) }
      .sortedWith { a, b ->
        -compareVersion(a.name, b.name)
      }
    var first: String? = null
    list.forEachIndexed { index, file ->
      if (index == 0) {
        first = file.nameWithoutExtension
          .substringAfter("-")
      } else {
        file.delete() // 清除低版本 jar 包
      }
    }
    return first
  }

  /**
   * https://leetcode.cn/problems/compare-version-numbers/description/
   * 时间复杂度: O(n+m)
   * 空间复杂度: O(1)
   */
  private fun compareVersion(version1: String, version2: String): Int {
    val n = version1.length
    val m = version2.length
    var i = 0
    var j = 0
    while (i < n || j < m) {
      var x = 0
      while (i < n && version1[i] != '.') {
        x = x * 10 + (version1[i] - '0')
        i++
      }
      i++ // 跳过点号
      var y = 0
      while (j < m && version2[j] != '.') {
        y = y * 10 + (version2[j] - '0')
        j++
      }
      j++ // 跳过点号
      if (x != y) {
        return if (x > y) 1 else -1
      }
    }
    return 0
  }
}