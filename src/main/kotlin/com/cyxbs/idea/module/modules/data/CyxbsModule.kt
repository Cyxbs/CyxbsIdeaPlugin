package com.cyxbs.idea.module.modules.data

import com.cyxbs.idea.module.modules.properties.ModuleProperties
import com.cyxbs.idea.module.modules.properties.getDescription
import com.cyxbs.idea.module.modules.properties.getVisible
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

data class ChildModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
  val parent: CommonModule,
) : CyxbsModule

sealed interface CommonModule : CyxbsModule {
  val api: ApiModule?
  val children: List<ChildModule>
}

class ApplicationModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule?
    get() = null
  override val children: List<ChildModule>
    get() = emptyList()
}

class ComponentModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule?
    get() = null
  override val children: List<ChildModule> by lazy { getChildModules() }
}

class FunctionModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule? by lazy { getApiModule() }
  override val children: List<ChildModule> by lazy { getChildModules() }
}

class PageModule(
  override val name: String,
  override val file: File,
  override val description: List<String>,
) : CommonModule {
  override val api: ApiModule? by lazy { getApiModule() }
  override val children: List<ChildModule> by lazy { getChildModules() }
}

private fun CommonModule.getApiModule(): ApiModule? {
  val apiFile = file.resolve("api-${file.name}")
  return if (apiFile.exists() && ModuleProperties.getVisible(file)) {
    ApiModule(apiFile.name, apiFile, ModuleProperties.getDescription(apiFile), this)
  } else null
}

private fun CommonModule.getChildModules(): List<ChildModule> {
  return file.listFiles()?.mapNotNull {
    if (!it.name.startsWith("api-") && it.resolve("build.gradle.kts").exists()
      && ModuleProperties.getVisible(it)) {
      ChildModule(it.name, it, ModuleProperties.getDescription(it), this)
    } else null
  } ?: emptyList()
}


enum class CyxbsGroup(val groupName: String, val step: String) {
  Applications("cyxbs-applications", "applications"),
  Components("cyxbs-components", "components"),
  Functions("cyxbs-functions", "functions"),
  Pages("cyxbs-pages", "pages"),
}
