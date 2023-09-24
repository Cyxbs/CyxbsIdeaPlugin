package com.cyxbs.idea.module.creator.wizard.group

import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.cyxbs.idea.module.creator.libraries.LibraryDataSource
import com.cyxbs.idea.module.creator.wizard.cyxbs.applications.ApplicationsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.components.ComponentsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.functions.FunctionsWizardStep
import com.cyxbs.idea.module.creator.wizard.cyxbs.pages.PagesWizardStep
import com.cyxbs.idea.module.creator.wizard.dependencies.CyxbsDependWizardStep
import com.cyxbs.idea.module.creator.wizard.dependencies.data.CheckableType
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeData
import com.cyxbs.idea.module.creator.wizard.dependencies.data.TreeNodeType
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.ui.validation.validationErrorFor
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import java.util.Properties
import javax.swing.JPanel

object GroupManager {

  val GroupCheck = validationErrorFor<() -> String, String> { step, input ->
    when (step.invoke()) {
      "applications" -> {
        when {
          "cyxbs-".contains(input) -> null
          !input.startsWith("cyxbs-") -> "cyxbs-applications 子模块必须以 cyxbs- 开头"
          !input.matches(Regex("^cyxbs-[a-z].*")) -> "cyxbs- 后以小写字母开头"
          !input.matches(Regex("^cyxbs-[a-z][a-z0-9]*$")) -> "cyxbs- 后只能包含小写字母和数字"
          input.length < 3 -> "模块名长度不能小于三个字符"
          else -> null
        }
      }

      else -> {
        when {
          !input.matches(Regex("^[a-z].*")) -> "模块名只能以小写字母开头"
          !input.matches(Regex("^[a-z][a-z0-9]*$")) -> "模块名只能包含小写字母和数字"
          input.length < 3 -> "模块名长度不能小于三个字符"
          else -> null
        }
      }
    }
  }

  val RepeatModuleCheck = validationErrorFor<Project?, String> { t1, t2 ->
    val rootProjectPath = t1?.basePath ?: return@validationErrorFor null
    val rootProjectFile = File(rootProjectPath)
    val isValid = rootProjectFile.listFiles { file ->
      file.name == "cyxbs-applications"
          || file.name == "cyxbs-components"
          || file.name == "cyxbs-functions"
          || file.name == "cyxbs-pages"
    }!!.all {
      !it.resolve(t2).exists()
    }
    if (isValid) null else {
      "模块名不允许重复"
    }
  }

  private var mCyxbsDependWizardStep: CyxbsDependWizardStep? = null

  private val mApplicationsDependWizardStep = JPanel(GridBagLayout()).apply {
    add(panel {
      row {
        text("applications 类型模块会自动依赖所有子模块")
          .horizontalAlign(HorizontalAlign.CENTER)
      }
      row {
        text("如果想做特殊设置，请在 build-logic 中单独设置")
          .horizontalAlign(HorizontalAlign.CENTER)
      }
      group("最后") {
        row {
          text("创建后请在 build-logic 中实现单独的 ApplicationConfig")
            .horizontalAlign(HorizontalAlign.CENTER)
        }
      }
    }, GridBagConstraints().apply {
      fill = GridBagConstraints.CENTER
    })
  }

  fun createWizardSteps(
    wizardContext: WizardContext,
    modulesProvider: ModulesProvider
  ): Array<ModuleWizardStep> {
    println(
      ".(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
          "createWizardSteps"
    )
    val wizardStep = CyxbsDependWizardStep(wizardContext)
    mCyxbsDependWizardStep = wizardStep
    return arrayOf(wizardStep)
  }

  fun getStepMap(groupName: String, parent: NewProjectWizardStep): Map<String, NewProjectWizardStep> {
    val map = LinkedHashMap<String, NewProjectWizardStep>()
    // 如果是单独点击的 cyxbs-pages、cyxbs-functions 等就只显示对应的模块选项
    when (groupName) {
      "cyxbs-pages" -> map["pages"] = PagesWizardStep(parent)
      "cyxbs-functions" -> map["functions"] = FunctionsWizardStep(parent)
      "cyxbs-components" -> map["components"] = ComponentsWizardStep(parent)
      "cyxbs-applications" -> map["applications"] = ApplicationsWizardStep(parent)
      else -> {
        map["pages"] = PagesWizardStep(parent)
        map["functions"] = FunctionsWizardStep(parent)
        map["components"] = ComponentsWizardStep(parent)
        map["applications"] = ApplicationsWizardStep(parent)
      }
    }
    return map
  }

  fun updateDependWizardStep(step: String) {
    if (step == "applications") {
      mCyxbsDependWizardStep?.setSpecificContentPanel(mApplicationsDependWizardStep)
      return
    }
    val libraries = LibraryDataSource.getData()
//    val modules = ModulesDataSource.getData()
//    when (step) {
//      "pages" -> mCyxbsDependWizardStep?.update(libraries.values, modules.values)
//      "functions" -> mCyxbsDependWizardStep?.update(libraries.values, modules.values)
//      "components" -> {
//        mCyxbsDependWizardStep?.update(
//          listOf(libraries.getValue("cyxbs-components")),
//          listOf(modules.getValue("cyxbs-components"))
//        )
//      }
//    }
  }

  fun readData(rootProjectPath: String, result: LinkedHashMap<String, TreeNodeData>) {
    val moduleDirs = File(rootProjectPath).listFiles { dir ->
      when (dir.name) {
        "cyxbs-components", "cyxbs-functions", "cyxbs-pages" -> true
        else -> false
      }
    } ?: return
    moduleDirs.sort()
    moduleDirs.mapNotNull { group ->
      group.listFiles { module ->
        getModuleProperties(module)?.getProperty("idea.plugin.module.builder.depend") != "false"
            && module.resolve("build.gradle.kts").exists()
      }?.mapNotNull { module ->
        when (group.name) {
          "cyxbs-components" -> module
          "cyxbs-functions" -> module.resolve("api-${module.name}").let { if (it.exists()) it else module }
          "cyxbs-pages" -> module.resolve("api-${module.name}").let { if (it.exists()) it else null }
          else -> null
        }
      }?.sorted()?.let { group to it }
    }.forEach { pair ->
      result[pair.first.name] = TreeNodeData(pair.first.name, "", CheckableType.Catalog, TreeNodeType.ParentNode(
        pair.second.map {
          TreeNodeData(it.name,
            getModuleProperties(it)?.getProperty("idea.plugin.module.builder.description") ?: "",
            CheckableType.Checkbox(), TreeNodeType.LeafNode)
        }
      ))
    }
  }


  private val mModuleProperties = HashMap<File, Properties>()

  fun getModuleProperties(file: File): Properties? {
    if (mModuleProperties.contains(file)) return mModuleProperties.getValue(file)
    if (file.resolve("build.gradle.kts").exists()) {
      val propertiesFile = file.resolve("module.properties")
      if (propertiesFile.exists()) {
        val properties = Properties()
        return try {
          properties.load(propertiesFile.inputStream().reader())
          mModuleProperties[file] = properties
          properties
        } catch (e: Exception) {
          null
        }
      }
    }
    return null
  }

  var stepName: String? = null
  var moduleName: String? = null

  fun getModuleFile(project: Project?): File? {
    project ?: return null
    val stepName = stepName ?: return null
    val moduleName = moduleName ?: return null
    val basePath = project.basePath ?: return null
    if (!checkCyxbsMobileLite(basePath)) return null
    val projectFile = File(basePath)
    return when (stepName) {
      "applications" -> projectFile.resolve("cyxbs-applications").resolve(moduleName)
      "components" -> projectFile.resolve("cyxbs-components").resolve(moduleName)
      "functions" -> projectFile.resolve("cyxbs-functions").resolve(moduleName)
      "pages" -> projectFile.resolve("cyxbs-pages").resolve(moduleName)
      else -> null
    }
  }
}