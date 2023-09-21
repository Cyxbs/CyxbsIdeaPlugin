pluginManagement {
  includeBuild(".")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mavenCentral 快照仓库
    maven("https://jitpack.io")
//    jcenter() // 部分依赖需要
    mavenLocal() // maven 默认的本地依赖位置：用户名/.m2/repository 中
  }
}
rootProject.name = "build-logic"

// dependencies
//include(":dependencies:android")
//include(":dependencies:others")
include(":dependencies:library")

// module
include(":module")

// plugin