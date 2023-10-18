package com.cyxbs.idea.module.utils

import com.android.tools.idea.observable.InvalidationListener
import com.android.tools.idea.observable.ObservableValue
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.util.Disposer

/**
 * .
 *
 * @author 985892345
 * 2023/10/6 19:41
 */

/**
 * AS 的可观察对象转换为 idea 的可观察对象
 */
fun <T> ObservableValue<T>.toIdea(): ObservableProperty<T> {
  return object : ObservableProperty<T> {
    override fun afterChange(listener: (T) -> Unit) {
      this@toIdea.addListener { listener.invoke(get()) }
    }

    override fun afterChange(listener: (T) -> Unit, parentDisposable: Disposable) {
      val l = InvalidationListener { listener.invoke(get()) }
      this@toIdea.addListener(l)
      Disposer.register(parentDisposable) {
        this@toIdea.removeListener(l)
      }
    }

    override fun get(): T = this@toIdea.get()
  }
}

