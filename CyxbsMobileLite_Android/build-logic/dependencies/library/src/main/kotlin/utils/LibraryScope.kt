import org.gradle.api.Project

// 不添加包名，这样 build.gradle.kts 就不用导包

/**
 * ```
 * // 在你的模块里面
 * dependLibrary {
 *   dependCoroutines()
 *   dependGlide()
 *   dependNetwork()
 *   // 其他依赖输入 depend 看 idea 智能提示
 * }
 * ```
 *
 * @author 985892345
 * 2023/9/15 22:34
 */
fun Project.dependLibrary(action: LibraryScope.() -> Unit) {
  LibraryScope(project).action()
}

class LibraryScope(project: Project): Project by project