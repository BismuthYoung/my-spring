package org.bohan.minispring.aop

import java.lang.reflect.Method

open class ReflectiveMethodInvocation(
    private val target: Any,      // 目标对象
    private val method: Method,   // 方法
    private val arguments: Array<Any> // 参数
) : MethodInvocation {

    override fun getMethod(): Method = method

    override fun getThis(): Any = target

    override fun getArguments(): Array<Any> = arguments

    @Throws(Throwable::class)
    override fun proceed(): Any {
        return method.invoke(target, *arguments)
    }
}