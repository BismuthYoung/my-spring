package org.bohan.minispring.aop

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * JDK动态代理实现
 * 基于JDK动态代理实现AOP代理
 *
 * @author Bohan
 */
class JdkDynamicAopProxy(
    private val advised: AdvisedSupport
): AopProxy, InvocationHandler {
    override fun getProxy(): Any {
        return getProxy(javaClass.classLoader)
    }

    override fun getProxy(classLoader: ClassLoader?): Any {
        val targetClass = advised.targetSource?.getTargetClass()
            ?: throw IllegalArgumentException("target class cannot be null")

        return Proxy.newProxyInstance(classLoader, targetClass.interfaces, this)
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        val target = advised.targetSource?.getTarget() ?: throw NullPointerException("target source is null")

        // 检查方法是否匹配切点表达式
        if (advised.methodMatcher != null
            && ! advised.methodMatcher!!.matches(method, target.javaClass)) {
            return method.invoke(target, args)
        }

        // 创建拦截器链
        val interceptors = advised.interceptors

        // 创建方法调用对象
        val invocation = ReflectiveMethodInvocation(target, method, args, interceptors.toList())

        // 执行方法调用链
        return invocation.proceed()
    }
}