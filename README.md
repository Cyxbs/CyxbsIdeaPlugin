# CyxbsIdeaPlugin
[掌上重邮极速版](https://github.com/Cyxbs/CyxbsMobileLite_Android) 配套的 idea 插件

## 功能如下
- 创建模块
  - 支持添加依赖

## 如何参与
如果你想参与该项目，你需要了解以下内容:
- Swing
- [idea 插件官方文档](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- 官方项目
  - NewModuleBuilder
  - JavaFxModuleBuilder (plugins 需添加 javaFX)
  - MavenArchetypeNewProjectWizard (plugins 需添加 maven)
  - AndroidModuleBuilder (plugins 需添加 android)
- 其他学习资料
  - [掘金: Android Studio IDE 插件开发](https://juejin.cn/post/7020033392422944804)
  - [Mirai idea 插件](https://github.com/mamoe/mirai/tree/dev/mirai-console/tools/intellij-plugin)


## 如何调试
- 执行 gradle 中 intellij 分组的 runIde
- 打开该项目目录下的 [CyxbsMobileLite_Android](CyxbsMobileLite_Android) (这是源项目的模版，方便调试)

## 如何使用
- 执行 gradle 中 intellij 分组的 buildPlugin
- 然后把 build 下面 libs 中生成的 `CyxbsIdeaPlugin-x.x.jar` 放在掌上重邮极速版项目目录下
- 插件中已做旧版强制升级处理
- 在 设置 - plugins 里面点击齿轮，选择 `Install Plugin from Disk` 安装目录下的 jar 即可完成升级操作