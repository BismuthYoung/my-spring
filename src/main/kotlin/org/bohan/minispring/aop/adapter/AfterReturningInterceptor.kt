package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.AfterReturningAdvice
import org.bohan.minispring.aop.MethodInterceptor
import org.bohan.minispring.aop.MethodInvocation

/**
 * 方法返回后通知拦截器
 * 将AfterReturningAdvice转换为MethodInterceptor
 *
 * @author Bohan
 */
class AfterReturningInterceptor(
    private val advice: AfterReturningAdvice
): MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        val returnValue = invocation.proceed()

        advice.afterReturning(returnValue, invocation.getMethod(), invocation.getArguments(), invocation.getThis())
        return returnValue
    }
}