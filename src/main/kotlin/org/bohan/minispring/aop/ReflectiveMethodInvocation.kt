package org.bohan.minispring.aop

import org.bohan.minispring.aop.adapter.MethodBeforeAdviceInterceptor
import java.lang.reflect.Method

/**
 * 反射方法调用实现
 * 实现方法调用链的执行
 *
 * @author Bohan
 */
open class ReflectiveMethodInvocation(
    private val target: Any,      // 目标对象
    private val method: Method,   // 方法
    private val arguments: Array<Any>, // 参数
    protected val interceptors: List<MethodInterceptor>,
    protected var currentInterceptorIndex: Int = -1
) : MethodInvocation {

    override fun getMethod(): Method = method

    override fun getThis(): Any = target

    override fun getArguments(): Array<Any> = arguments

    @Throws(Throwable::class)
    override fun proceed(): Any? {
        // 如果所有拦截器都已经调用完，则调用目标方法
        if (currentInterceptorIndex >= interceptors.size - 1) {
            return method.invoke(target, *arguments)
        }

        // 获取下一个拦截器
        val interceptor = interceptors[++ currentInterceptorIndex]

        try {
            // 调用拦截器
            return interceptor.invoke(this)
        } catch (e: Exception) {
            // 如果发生异常，确保所有前置通知都已执行
            if (interceptor is MethodBeforeAdviceInterceptor) {
                currentInterceptorIndex ++
                if (currentInterceptorIndex < interceptors.size &&
                    interceptors[currentInterceptorIndex] is MethodBeforeAdviceInterceptor) {
                    return proceed()
                }
            }

            throw e
        }
    }
}