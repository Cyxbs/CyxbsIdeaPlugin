package com.cyxbs.idea.module.creator.modules.data

import java.io.File

/**
 * .
 *
 * @author 985892345
 * 2023/9/24 20:09
 */
sealed interface CyxbsModule {
  val name: String
  val file: File
}

data class ApiModule(
  override val name: String,
  override val file: File,
  val parent: CommonModule,
) : CyxbsModule

sealed interface CommonModule : CyxbsModule

class ApplicationModule(
  override val name: String,
  override val file: File,
) : CommonModule

class ComponentModule(
  override val name: String,
  override val file: File,
) : CommonModule

class FunctionModule(
  override val name: String,
  override val file: File,
) : CommonModule {
  val api: ApiModule? by lazy {
    val apiFile = file.resolve("api-${file.name}")
    if (apiFile.exists()) {
      ApiModule(apiFile.name, apiFile, this)
    } else null
  }
}

class PageModule(
  override val name: String,
  override val file: File,
) : CommonModule {
  val api: ApiModule? by lazy {
    val apiFile = file.resolve("api-${file.name}")
    if (apiFile.exists()) {
      ApiModule(apiFile.name, apiFile, this)
    } else null
  }
}


enum class CyxbsGroup(val groupName: String) {
  Applications("cyxbs-applications"),
  Components("cyxbs-components"),
  Functions("cyxbs-functions"),
  Pages("cyxbs-pages"),
}
