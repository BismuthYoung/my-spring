package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.factory.DisposableBean

class DisposableBeanAdapter(
    private val bean: Any,
    private val beanName: String,
    private val destroyMethodName: String?,
    private val isDisposableBean: Boolean = bean is DisposableBean
): DisposableBean {
    override fun destroy() {
        // 1. 如果bean实现了DisposableBean接口
        if (isDisposableBean) {
            (bean as DisposableBean).destroy()
        }

        // 2. 如果配置了自定义的销毁方法
        if (! destroyMethodName.isNullOrBlank() &&
            ! (isDisposableBean && "destroy" == destroyMethodName)) {
            val destroyMethodName = bean.javaClass.getMethod(destroyMethodName)
            destroyMethodName.invoke(bean)
        }
    }
}