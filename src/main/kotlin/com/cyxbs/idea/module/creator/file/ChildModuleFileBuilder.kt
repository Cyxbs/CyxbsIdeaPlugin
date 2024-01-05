package com.cyxbs.idea.module.creator.file

import com.android.SdkConstants
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import com.cyxbs.idea.module.utils.capitalized
import java.io.File

/**
 * 子模块构建器
 *
 * @author 985892345
 * 2023/10/6 09:29
 */
object ChildModuleFileBuilder {

  fun generate(
    moduleData: ModuleTemplateData,
    recipeExecutor: RecipeExecutor,
    parentModuleName: String,
    childModuleName: String,
    cyxbsGroup: CyxbsGroup,
    isSingleModule: Boolean,
    dependModules: List<String>?,
    dependLibraries: List<String>?,
  ) {
    val childModuleDir = moduleData.rootDir.resolve(cyxbsGroup.groupName)
      .resolve(parentModuleName)
      .resolve(childModuleName)
    recipeExecutor.apply {
      generateCodeDir(childModuleDir, isSingleModule, cyxbsGroup)
      generateBuildGradle(childModuleDir, dependModules, dependLibraries)
      generateAndroidManifest(childModuleDir)
      generateSingleModuleEntry(childModuleDir, cyxbsGroup, isSingleModule)
    }
  }

  private fun RecipeExecutor.generateCodeDir(
    childModuleDir: File,
    isSingleModule: Boolean,
    cyxbsGroup: CyxbsGroup,
  ) {
    val mainDir = childModuleDir
      .resolve("src")
      .resolve("main")
    createDirectory(mainDir
      .resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(cyxbsGroup.step)
      .resolve(childModuleDir.name.replace("-", File.separator)))
    if (!childModuleDir.name.startsWith("api-") && cyxbsGroup == CyxbsGroup.Pages) {
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
    childModuleDir: File,
    dependModules: List<String>?,
    dependLibraries: List<String>?,
  ) {
    save(
      ftBuildGradle(listOf("module-manager"), dependModules, dependLibraries),
      childModuleDir.resolve(SdkConstants.FN_BUILD_GRADLE_KTS))
  }

  private fun RecipeExecutor.generateAndroidManifest(apiModuleDir: File) {
    save(ftAndroidManifest(), apiModuleDir.resolve("src")
      .resolve("main")
      .resolve(SdkConstants.ANDROID_MANIFEST_XML))
  }

  private fun RecipeExecutor.generateSingleModuleEntry(
    moduleDir: File,
    cyxbsGroup: CyxbsGroup,
    isSingleModule: Boolean,
  ) {
    if (!isSingleModule) return
    val singlePackageName = "com.cyxbs.${cyxbsGroup.step}.${moduleDir.name.replace("-", ".")}.single"
    val classNamePrefix = moduleDir.name.split("-")
      .toMutableList()
      .apply {
        add(first()) // 把第一个父模块名字放到最后
        removeFirst()
      }.joinToString("") { it.capitalized() }
    save(
      ftSingleModuleEntry(singlePackageName, classNamePrefix),
      moduleDir.resolve("src")
        .resolve("main")
        .resolve("single")
        .resolve(singlePackageName.replace(".", File.separator))
        .resolve("${classNamePrefix}SingleModuleEntry.kt"))
  }
}