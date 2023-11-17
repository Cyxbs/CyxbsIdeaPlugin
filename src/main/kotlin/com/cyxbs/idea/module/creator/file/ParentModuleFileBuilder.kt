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
 * 父模块构建器
 *
 * @author 985892345
 * 2023/10/6 09:22
 */
object ParentModuleFileBuilder {

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
      generateCodeDir(moduleDir, isSingleModule, cyxbsGroup, moduleName)
      generateBuildGradle(moduleDir, isSingleModule, dependModules, dependLibraries)
      generateAndroidManifest(moduleDir)
      generateSingleModuleEntry(moduleDir, moduleName, cyxbsGroup, isSingleModule)
      generateModuleProperties(moduleDir, description)
    }
  }

  private fun RecipeExecutor.generateCodeDir(
    moduleDir: File,
    isSingleModule: Boolean,
    cyxbsGroup: CyxbsGroup,
    moduleName: String,
  ) {
    val mainDir = moduleDir
      .resolve("src")
      .resolve("main")
    val codeDir = mainDir
      .resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(cyxbsGroup.step)
      .resolve(moduleName)
    createDirectory(codeDir)
    if (cyxbsGroup == CyxbsGroup.Pages) {
      val resDir = mainDir.resolve("res")
      createDirectory(resDir.resolve("drawable"))
      createDirectory(resDir.resolve("drawable-xxhdpi"))
      createDirectory(resDir.resolve("layout"))
      save(ftResources(), resDir.resolve("values").resolve("strings.xml"))
      save(ftResources(), resDir.resolve("values").resolve("colors.xml"))
      save(ftResources(), resDir.resolve("values-night").resolve("colors.xml"))
    }
    if (isSingleModule) {
      val singleResDir = mainDir.resolve("single-res")
      createDirectory(singleResDir.resolve("layout"))
    }
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