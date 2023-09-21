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
// cyxbs-applications

// cyxbs-components
include(":cyxbs-components:account")
// cyxbs-components

// cyxbs-functions
include(":cyxbs-functions:debug")
// cyxbs-functions

// cyxbs-pages
//include(":cyxbs-pages:aaa")
//include(":cyxbs-pages:aaa:api-aaa")
include(":cyxbs-pages:exam")
include(":cyxbs-pages:exam:api-exam")
// cyxbs-pages