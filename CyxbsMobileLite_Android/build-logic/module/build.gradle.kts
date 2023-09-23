plugins {
  `kotlin-dsl`
}

dependencies {
  api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
  api(project(":dependencies:library"))
}