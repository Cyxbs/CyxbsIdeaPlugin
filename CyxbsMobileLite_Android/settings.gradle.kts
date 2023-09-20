pluginManagement {
  includeBuild("build-logic")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "CyxbsMobileLite_Android"

// cyxbs-applications
include(":cyxbs-applications:cyxbs-lite")

// cyxbs-components
include(":cyxbs-components:account")

// cyxbs-functions
include(":cyxbs-functions:debug")

// cyxbs-pages
include(":cyxbs-pages:exam")
include(":cyxbs-pages:exam:api-exam")