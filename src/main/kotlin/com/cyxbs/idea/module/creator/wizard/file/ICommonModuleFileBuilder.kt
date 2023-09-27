package com.cyxbs.idea.module.creator.wizard.file

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 17:16
 */
interface ICommonModuleFileBuilder : IModuleFileBuilder {

  val isNeedSingleModule: Boolean

  val dependModules: List<String>

  val dependLibraries: List<String>
}