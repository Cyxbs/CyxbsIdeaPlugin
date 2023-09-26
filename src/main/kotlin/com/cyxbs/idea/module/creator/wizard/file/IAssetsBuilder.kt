package com.cyxbs.idea.module.creator.wizard.file

import com.cyxbs.idea.module.creator.modules.data.CyxbsGroup
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 17:16
 */
interface IAssetsBuilder {

  val group: CyxbsGroup

  val newProjectName: String

  val isNeedApiModule: Boolean

  val isNeedSingleModule: Boolean

  val dependModules: List<String>

  val dependLibraries: List<String>

  /**
   * 获取文件模版的管理类
   */
  val template: FileTemplateManager

  /**
   * 添加文件模版的属性
   *
   * 注意：会覆盖相同的 key
   */
  fun addTemplateProperties(vararg properties: Pair<String, Any>)

  /**
   * 添加文件或者文件夹
   */
  fun addAssets(vararg assets: GeneratorAsset)

  /**
   * 设置模版文件中的字段属性
   */
  fun addTemplateProperties(properties: Map<String, Any>)

  /**
   * 打开某文件
   */
  fun addFilesToOpen(vararg relativeCanonicalPaths: String)
}