package org.bohan.minispring.aop

import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class CglibAopProxy(
    private val advised: AdvisedSupport
): AopProxy {
    override fun getProxy(): Any {
        return getProxy(null)
    }

    override fun getProxy(classLoader: ClassLoader?): Any {
        val rootClass = advised.targetSource?.getTargetClass() ?: throw IllegalArgumentException("目标类不能为空")

        val enhancer = Enhancer()
        if (classLoader != null) {
            enhancer.classLoader = classLoader
        }
        enhancer.setSuperclass(rootClass)
        enhancer.setCallback(DynamicAdvisedInterceptor(advised))

        return enhancer.create()
    }

    companion object {
        /**
         * CGLIB方法拦截器
         * 实现方法的拦截和增强
         */
        class DynamicAdvisedInterceptor(
            private val advised: AdvisedSupport
        ): MethodInterceptor {
            override fun intercept(obj: Any?, method: Method, args: Array<Any>, proxy: MethodProxy?): Any {
                val target = advised.targetSource?.getTarget() ?: throw NullPointerException("target source is null")

                // 检查方法是否匹配切点表达式
                if (advised.methodMatcher != null
                    && ! advised.methodMatcher!!.matches(method, target.javaClass)) {
                    return method.invoke(target, args)
                }

                // 创建方法调用对象
                val invocation = ReflectiveMethodInvocation(target, method, args)

                // 执行拦截器链
                val interceptor = advised.methodInterceptor ?: throw NullPointerException("interceptor is null")
                return interceptor.invoke(invocation)
            }

        }
    }

}