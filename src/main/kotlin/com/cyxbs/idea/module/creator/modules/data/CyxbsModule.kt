package com.cyxbs.idea.module.creator.modules.data

import com.cyxbs.idea.module.creator.modules.ModuleProperties
import com.cyxbs.idea.module.creator.modules.getDescription
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
  val description: List<String>
}

data class ApiModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
  val parent: CommonModule,
) : CyxbsModule

sealed interface CommonModule : CyxbsModule {
  val api: ApiModule?
}

class ApplicationModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule?
    get() = null
}

class ComponentModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule?
    get() = null
}

class FunctionModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule? by lazy { getApiModule() }
}

class PageModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule? by lazy { getApiModule() }
}

private fun CommonModule.getApiModule(): ApiModule? {
  val apiFile = file.resolve("api-${file.name}")
  return if (apiFile.exists()) {
    ApiModule(apiFile.name, apiFile, ModuleProperties.getDescription(apiFile), this)
  } else null
}


enum class CyxbsGroup(val groupName: String, val step: String) {
  Applications("cyxbs-applications", "applications"),
  Components("cyxbs-components", "components"),
  Functions("cyxbs-functions", "functions"),
  Pages("cyxbs-pages", "pages"),
}
