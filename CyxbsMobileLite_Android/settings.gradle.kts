import java.io.File

pluginManagement {
  includeBuild("build-logic")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "CyxbsMobileLite_Android"



///////////  自动 include 模块  ///////////

// 需要删除模块时写这里面，将不再进行 include，直接写模块名即可
val excludeList: List<String> = listOf(
)

fun includeModule(topName: String, file: File) {
  if (!file.resolve("settings.gradle.kts").exists()) {
    if (file.resolve("build.gradle.kts").exists()) {
      var path = ":${file.name}"
      var parentFile = file.parentFile
      do {
        path = ":${parentFile.name}$path"
        parentFile = parentFile.parentFile
      } while (parentFile.name == topName)
      include(path)
    }
  }
  // 递归寻找所有子模块
  file.listFiles()?.filter {
    it.name != "src" // 去掉 src 文件夹
        && !it.resolve("settings.gradle.kts").exists() // 去掉独立的项目模块，比如 build-logic
        && !excludeList.contains(it.name) // 去掉被忽略的模块
  }?.forEach {
    includeModule(topName, it)
  }
}

includeModule("cyxbs-applications", rootDir.resolve("cyxbs-applications"))
includeModule("cyxbs-components", rootDir.resolve("cyxbs-components"))
includeModule("cyxbs-functions", rootDir.resolve("cyxbs-functions"))
includeModule("cyxbs-pages", rootDir.resolve("cyxbs-pages"))
/**
 * 如果你使用 AS 自带的模块模版，他会自动添加 include()，请删除掉，因为上面会自动读取
 * 请注意:
 * - 对于普通的模块请使用配套的 idea 插件: CyxbsModuleBuilder
 * - 如果是比较特殊的模块，请单独 include()
 */