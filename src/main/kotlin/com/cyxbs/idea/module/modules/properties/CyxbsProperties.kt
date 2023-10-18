package com.cyxbs.idea.module.modules.properties

import com.intellij.openapi.project.Project
import java.io.File
import java.util.Properties

/**
 * .
 *
 * @author 985892345
 * 2023/10/15 20:12
 */
object CyxbsProperties {

  const val FILE_NAME = "ideaPlugin.properties"

  val properties = Properties()

  fun init(project: Project) {
    val basePath = project.basePath ?: return
    val file = File(basePath).resolve(FILE_NAME)
    try {
      properties.load(file.inputStream().reader())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  val pagesDefaultModules: List<String>
    get() = properties.getProperty("pages.default.modules")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()

  val pagesDefaultLibraries: List<String>
    get() = properties.getProperty("pages.default.libraries")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()

  val functionsDefaultModules: List<String>
    get() = properties.getProperty("functions.default.modules")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()

  val functionsDefaultLibraries: List<String>
    get() = properties.getProperty("functions.default.libraries")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()

  val singleModuleDefaultModules: List<String>
    get() = properties.getProperty("single.module.default.modules")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()

  val singleModuleDefaultLibraries: List<String>
    get() = properties.getProperty("single.module.default.libraries")?.split(" ")?.filter {
      it.isNotBlank()
    } ?: emptyList()
}

