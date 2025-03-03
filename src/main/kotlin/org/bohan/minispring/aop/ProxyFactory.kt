package org.bohan.minispring.aop

import org.bohan.minispring.aop.adapter.AdvisorAdapterRegistry
import org.bohan.minispring.aop.adapter.DefaultAdvisorAdapterRegistry

/**
 * AOP代理工厂
 * 用于创建代理对象，支持JDK动态代理和Cglib代理
 *
 * @author kama
 * @version 1.0.0
 */
class ProxyFactory(target: Any) {

    private val advised: AdvisedSupport = AdvisedSupport().apply {
        targetSource = TargetSource(target)
    }
    private val advisorAdapterRegistry: AdvisorAdapterRegistry = DefaultAdvisorAdapterRegistry()

    /**
     * 添加通知
     *
     * @param advice 通知
     */
    fun addAdvice(advice: Advice) {
        val interceptor = this.advisorAdapterRegistry.wrap(advice)
        this.advised.addMethodInterceptor(interceptor)
    }

    /**
     * 设置是否强制使用Cglib代理
     *
     * @param proxyTargetClass 是否强制使用Cglib代理
     */
    fun setProxyTargetClass(proxyTargetClass: Boolean) {
        this.advised.isProxyTargetClass = proxyTargetClass
    }

    /**
     * 获取代理对象
     *
     * @return 代理对象
     */
    fun getProxy(): Any {
        return getProxy(null)
    }

    /**
     * 获取代理对象
     *
     * @param classLoader 类加载器
     * @return 代理对象
     */
    fun getProxy(classLoader: ClassLoader?): Any {
        if (this.advised.targetSource != null) {
            return if (this.advised.isProxyTargetClass || !isInterfaceProxyable(this.advised.targetSource!!.getTarget())) {
                createCglibProxy(classLoader)
            } else {
                createJdkDynamicProxy(classLoader)
            }
        }

        throw NullPointerException("target source cannot be null")
    }

    /**
     * 判断是否可以使用接口代理
     *
     * @param target 目标对象
     * @return 是否可以使用接口代理
     */
    private fun isInterfaceProxyable(target: Any): Boolean {
        return target.javaClass.interfaces.isNotEmpty()
    }

    /**
     * 创建JDK动态代理
     *
     * @param classLoader 类加载器
     * @return 代理对象
     */
    private fun createJdkDynamicProxy(classLoader: ClassLoader?): Any {
        val proxy = JdkDynamicAopProxy(this.advised)
        return proxy.getProxy(classLoader ?: this::class.java.classLoader)
    }

    /**
     * 创建Cglib代理
     *
     * @param classLoader 类加载器
     * @return 代理对象
     */
    private fun createCglibProxy(classLoader: ClassLoader?): Any {
        val proxy = CglibAopProxy(this.advised)
        return proxy.getProxy(classLoader)
    }
}
