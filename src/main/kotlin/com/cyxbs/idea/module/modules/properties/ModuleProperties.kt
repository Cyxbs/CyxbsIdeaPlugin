package com.cyxbs.idea.module.modules.properties

import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * .
 *
 * @author 985892345
 * 2023/9/26 09:05
 */
object ModuleProperties {

  const val FILE_NAME = "module.properties"

  private val mModuleProperties = HashMap<File, Properties>()

  fun get(file: File): Properties? {
    if (mModuleProperties.contains(file)) return mModuleProperties.getValue(file)
    val propertiesFile = file.resolve(FILE_NAME)
    return if (propertiesFile.exists()) {
      val properties = Properties()
      try {
        properties.load(propertiesFile.inputStream().reader())
        mModuleProperties[file] = properties
        properties
      } catch (e: Exception) {
        null
      }
    } else {
      return file.parentFile?.let { get(it) }
    }
  }
}

fun ModuleProperties.getVisible(file: File): Boolean =
  get(file)?.getProperty("idea.plugin.module.builder.visible") != "false"

fun ModuleProperties.getDescription(file: File): List<String> =
  get(file)?.getProperty("idea.plugin.module.builder.description")?.split("\n") ?: emptyList()

fun ftModuleProperties(
  description: String,
) = """
  idea.plugin.module.builder.description=${description.ifEmpty { "nothing" }}
""".trimIndent()