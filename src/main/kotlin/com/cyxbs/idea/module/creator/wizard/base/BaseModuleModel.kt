package com.cyxbs.idea.module.creator.wizard.base

import com.android.sdklib.SdkVersionInfo
import com.android.tools.adtui.device.FormFactor
import com.android.tools.idea.npw.model.ExistingProjectModelData
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleModel
import com.android.tools.idea.npw.platform.AndroidVersionsInfo
import com.android.tools.idea.observable.core.OptionalValueProperty
import com.google.wireless.android.sdk.stats.AndroidStudioEvent
import com.intellij.openapi.project.Project

/**
 * .
 *
 * @author 985892345
 * 2023/10/7 11:23
 */
abstract class BaseModuleModel(
  project: Project,
  moduleParent: String,
  projectSyncInvoker: ProjectSyncInvoker
) : ModuleModel(
  name = "",
  commandName = "New Cyxbs Module",
  isLibrary = true,
  projectModelData = ExistingProjectModelData(project, projectSyncInvoker),
  moduleParent = moduleParent,
  wizardContext = AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext.NEW_MODULE
) {

  override val loggingEvent: AndroidStudioEvent.TemplateRenderer
    get() = AndroidStudioEvent.TemplateRenderer.ANDROID_MODULE

  // 直接照抄的 NewLibraryModuleModel
  override val androidSdkInfo = OptionalValueProperty(
    AndroidVersionsInfo().apply { loadLocalVersions() }
      .getKnownTargetVersions(FormFactor.MOBILE, SdkVersionInfo.LOWEST_ACTIVE_API)
      .first() // we don't care which one do we use, we just have to pass something, it is not going to be used
  )
}