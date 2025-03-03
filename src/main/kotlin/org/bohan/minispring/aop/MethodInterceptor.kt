package org.bohan.minispring.aop

import kotlin.Throws

/**
 * 方法拦截器
 * 用于在目标方法执行前后进行增强
 *
 * @author Bohan
 */
interface MethodInterceptor: Advice {

    /**
     * 拦截方法调用
     *
     * @param invocation 方法调用
     * @return 方法返回值
     * @throws Throwable 执行异常
     */
    @Throws(Throwable::class)
    fun invoke(invocation: MethodInvocation): Any?

}