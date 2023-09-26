package com.cyxbs.idea.module.creator.libraries.data

import java.io.File

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 21:12
 */
interface CyxbsLibrary {
  val group: String // 文件名字
  val file: File
  val description: List<String>
  val depends: List<DependItem>
}

data class DependItem(
  val name: String, // depend* 后面的 *
  val description: List<String>,
)