package com.cyxbs.idea.module.creator.file

import com.android.SdkConstants
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.cyxbs.idea.module.modules.data.CyxbsGroup
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
  ) {
    val childModuleDir = moduleData.rootDir.resolve(cyxbsGroup.groupName)
      .resolve(parentModuleName)
      .resolve(childModuleName)
    recipeExecutor.apply {
      generateCodeDir(childModuleDir, cyxbsGroup)
      generateBuildGradle(childModuleDir)
      generateAndroidManifest(childModuleDir)
    }
  }

  private fun RecipeExecutor.generateCodeDir(
    childModuleDir: File,
    cyxbsGroup: CyxbsGroup,
  ) {
    createDirectory(childModuleDir
      .resolve("src")
      .resolve("main")
      .resolve("java")
      .resolve("com")
      .resolve("cyxbs")
      .resolve(cyxbsGroup.step)
      .resolve(childModuleDir.name.replace("-", File.separator)))
  }

  private fun RecipeExecutor.generateBuildGradle(
    childModuleDir: File,
  ) {
    save(
      ftBuildGradle(listOf("module-manager"), emptyList(), emptyList()),
      childModuleDir.resolve(SdkConstants.FN_BUILD_GRADLE_KTS))
  }

  private fun RecipeExecutor.generateAndroidManifest(apiModuleDir: File) {
    save(ftAndroidManifest(), apiModuleDir.resolve("src")
      .resolve("main")
      .resolve(SdkConstants.ANDROID_MANIFEST_XML))
  }
}