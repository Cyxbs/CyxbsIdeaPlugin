package com.cyxbs.idea.module.creator.file

import com.android.SdkConstants
import com.android.tools.idea.npw.module.recipes.emptyPluginsBlock
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.cyxbs.idea.module.modules.data.CyxbsGroup
import java.io.File

/**
 * api 模块构建器
 *
 * @author 985892345
 * 2023/10/6 09:29
 */
object ApiModuleFileBuilder {

  fun generate(
    moduleData: ModuleTemplateData,
    recipeExecutor: RecipeExecutor,
    apiModuleName: String, // 包含 api- 前缀
    cyxbsGroup: CyxbsGroup,
  ) {
    val apiModuleDir = moduleData.rootDir.resolve(cyxbsGroup.groupName)
      .resolve(apiModuleName.substringAfter("api-"))
      .resolve(apiModuleName)
    recipeExecutor.apply {
      generateCodeDir(apiModuleDir, cyxbsGroup, apiModuleName)
      generateBuildGradle(apiModuleDir)
      generateAndroidManifest(apiModuleDir)
    }
  }

  private fun RecipeExecutor.generateCodeDir(
    apiModuleDir: File,
    cyxbsGroup: CyxbsGroup,
    apiModuleName: String,
  ) {
    createDirectory(apiModuleDir
      .resolve("src")
      .resolve("main")
      .resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(cyxbsGroup.step)
      .resolve(apiModuleName.replace("-", File.separator)))
  }

  private fun RecipeExecutor.generateBuildGradle(
    apiModuleDir: File,
  ) {
    save(
      ftBuildGradle(listOf("module-manager"), null, null),
      apiModuleDir.resolve(SdkConstants.FN_BUILD_GRADLE_KTS))
  }

  private fun RecipeExecutor.generateAndroidManifest(apiModuleDir: File) {
    save(ftAndroidManifest(), apiModuleDir.resolve("src")
      .resolve("main")
      .resolve(SdkConstants.ANDROID_MANIFEST_XML))
  }
}