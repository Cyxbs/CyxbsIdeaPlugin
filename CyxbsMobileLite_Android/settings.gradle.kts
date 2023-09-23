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

rootDir.listFiles { file ->
  when (file.name) {
    "cyxbs-applications",
    "cyxbs-components",
    "cyxbs-functions",
    "cyxbs-pages" -> true
    else -> false
  }
}!!.map {
  it.listFiles()!!.toList()
}.flatten().filter {
  // 以部分文件来过滤出那些时模块
  it.isDirectory
      && it.resolve("build.gradle.kts").exists()
      && !it.resolve("settings.gradle.kts").exists()
}.filter {
  // 排除不需要的模块
  !excludeList.contains(it.name)
      && !excludeList.contains(it.parentFile.name) // 如果父模块被忽略，则子模块同步忽略
}.map {
  // 映射为模块路径
  val parentFile = it.parentFile
  if (parentFile.parentFile == rootDir) {
    ":${parentFile.name}:${it.name}"
  } else {
    // 目前路径最多只有三级，第一级为最外层的模块分组，第二级为父模块，第三级为 api 子模块
    ":${parentFile.parentFile.name}:${parentFile.name}:${it.name}"
  }
}.forEach {
  include(it)
}
/**
 * 如果你使用 AS 自带的模块模版，他会自动添加 include()，请删除掉，因为上面会自动读取
 * 请注意:
 * - 对于普通的模块请使用配套的 idea 插件: CyxbsModuleBuilder
 * - 如果是比较特殊的模块，请单独 include()
 */