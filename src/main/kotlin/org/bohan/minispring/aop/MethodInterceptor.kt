package org.bohan.minispring.aop

import kotlin.Throws

/**
 * 方法拦截器
 * 用于在目标方法执行前后进行增强
 *
 * @author Bohan
 */
interface MethodInterceptor {

    /**
     * 执行方法拦截
     * 在这个方法中实现对目标方法的增强
     *
     * @param invocation 方法调用
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Throws(Throwable::class)
    fun invoke(invocation: MethodInvocation): Any

}