package com.cyxbs.idea.module.creator.file

import com.android.SdkConstants
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.cyxbs.idea.module.modules.properties.ModuleProperties
import com.cyxbs.idea.module.modules.properties.ftModuleProperties
import com.cyxbs.idea.module.utils.capitalized
import java.io.File

/**
 * 普通模块构建器
 *
 * @author 985892345
 * 2023/10/6 09:22
 */
object CommonModuleFileBuilder {

  fun generate(
    moduleData: ModuleTemplateData,
    recipeExecutor: RecipeExecutor,
    moduleName: String,
    cyxbsGroup: CyxbsGroup,
    isSingleModule: Boolean,
    dependModules: List<String>?,
    dependLibraries: List<String>?,
    description: String,
  ) {
    val moduleDir = moduleData.rootDir.resolve(cyxbsGroup.groupName)
      .resolve(moduleName)
    recipeExecutor.apply {
      generateCodeDir(moduleDir, cyxbsGroup, moduleName)
      generateBuildGradle(moduleDir, isSingleModule, dependModules, dependLibraries)
      generateAndroidManifest(moduleDir)
      generateSingleModuleEntry(moduleDir, moduleName, cyxbsGroup, isSingleModule)
      generateModuleProperties(moduleDir, description)
    }
  }

  private fun RecipeExecutor.generateCodeDir(
    moduleDir: File,
    cyxbsGroup: CyxbsGroup,
    moduleName: String,
  ) {
    val dir = moduleDir
      .resolve("src")
      .resolve("main")
      .resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(cyxbsGroup.step)
      .resolve(moduleName)
    createDirectory(dir)
    val resDir = moduleDir
      .resolve("src")
      .resolve("main")
      .resolve("res")
    createDirectory(resDir)
  }

  private fun RecipeExecutor.generateBuildGradle(
    moduleDir: File,
    isSingleModule: Boolean,
    dependModules: List<String>?,
    dependLibraries: List<String>?,
  ) {
    val plugins = if (isSingleModule) {
      listOf("module-single")
    } else listOf("module-manager")
    save(
      ftBuildGradle(plugins, dependModules, dependLibraries),
      moduleDir.resolve(SdkConstants.FN_BUILD_GRADLE_KTS))
    open(moduleDir.resolve(SdkConstants.FN_BUILD_GRADLE_KTS))
  }

  private fun RecipeExecutor.generateAndroidManifest(moduleDir: File,) {
    save(ftAndroidManifest(), moduleDir.resolve("src")
      .resolve("main")
      .resolve(SdkConstants.ANDROID_MANIFEST_XML))
  }

  private fun RecipeExecutor.generateSingleModuleEntry(
    moduleDir: File,
    moduleName: String,
    cyxbsGroup: CyxbsGroup,
    isSingleModule: Boolean,
  ) {
    if (!isSingleModule) return
    val singlePackageName = "com.cyxbs.${cyxbsGroup.step}.${moduleName}.single"
    save(
      ftSingleModuleEntry(singlePackageName, moduleName),
      moduleDir.resolve("src")
        .resolve("main")
        .resolve("single")
        .resolve(singlePackageName.replace(".", File.separator))
        .resolve("${moduleName.capitalized()}SingleModuleEntry.kt"))
  }

  private fun RecipeExecutor.generateModuleProperties(
    moduleDir: File,
    description: String,
  ) {
    save(ftModuleProperties(
      description
    ), moduleDir.resolve(ModuleProperties.FILE_NAME))
  }
}