package com.cyxbs.idea.module.creator.modules

import com.cyxbs.idea.module.creator.modules.data.*
import com.cyxbs.idea.module.creator.modules.properties.ModuleProperties
import com.cyxbs.idea.module.creator.modules.properties.getDescription
import com.cyxbs.idea.module.creator.modules.properties.getVisible
import com.cyxbs.idea.module.creator.utils.checkCyxbsMobileLite
import com.intellij.openapi.project.Project
import org.jetbrains.concurrency.runAsync
import java.io.File

object ModulesDataSource {

  private val mApplicationModules = mutableListOf<ApplicationModule>()
  private val mComponentModules = mutableListOf<ComponentModule>()
  private val mFunctionModules = mutableListOf<FunctionModule>()
  private val mPageModules = mutableListOf<PageModule>()

  fun copyApplicationModules(): List<ApplicationModule> = synchronized(mApplicationModules) {
    mApplicationModules.toList()
  }

  fun copyComponentModules(): List<ComponentModule> = synchronized(mComponentModules) {
    mComponentModules.toList()
  }

  fun copyFunctionModules(): List<FunctionModule> = synchronized(mFunctionModules) {
    mFunctionModules.toList()
  }

  fun copyPageModules(): List<PageModule> = synchronized(mPageModules) {
    mPageModules.toList()
  }

  /**
   * 读取依赖信息
   */
  fun loadData(project: Project?) {
    val basePath = project?.basePath ?: return
    if (!checkCyxbsMobileLite(basePath)) {
      return
    }
    runAsync {
      val rootFile = File(basePath)
      readApplicationModules(rootFile.resolve(CyxbsGroup.Applications.groupName))
      readComponentModules(rootFile.resolve(CyxbsGroup.Components.groupName))
      readFunctionModules(rootFile.resolve(CyxbsGroup.Functions.groupName))
      readPageModules(rootFile.resolve(CyxbsGroup.Pages.groupName))
    }
  }

  private fun readApplicationModules(applicationsFile: File) {
    synchronized(mApplicationModules) {
      mApplicationModules.clear()
      visitCyxbsGroup(applicationsFile) {
        val module = ApplicationModule(it.name, it, ModuleProperties.getDescription(it))
        mApplicationModules.add(module)
      }
    }
  }

  private fun readComponentModules(componentsFile: File) {
    synchronized(mComponentModules) {
      mComponentModules.clear()
      visitCyxbsGroup(componentsFile) {
        val module = ComponentModule(it.name, it, ModuleProperties.getDescription(it))
        mComponentModules.add(module)
      }
    }
  }

  private fun readFunctionModules(functionsFile: File) {
    synchronized(mFunctionModules) {
      mFunctionModules.clear()
      visitCyxbsGroup(functionsFile) {
        val module = FunctionModule(it.name, it, ModuleProperties.getDescription(it))
        mFunctionModules.add(module)
      }
    }
  }

  private fun readPageModules(pagesFile: File) {
    synchronized(mPageModules) {
      mPageModules.clear()
      visitCyxbsGroup(pagesFile) {
        val module = PageModule(it.name, it, ModuleProperties.getDescription(it))
        mPageModules.add(module)
      }
    }
  }

  private fun visitCyxbsGroup(cyxbsGroupFile: File, visitor: (parent: File) -> Unit) {
    cyxbsGroupFile.listFiles { file ->
      file.resolve("build.gradle.kts").exists()
          && ModuleProperties.getVisible(file)
    }?.forEach {
      visitor.invoke(it)
    }
  }
}