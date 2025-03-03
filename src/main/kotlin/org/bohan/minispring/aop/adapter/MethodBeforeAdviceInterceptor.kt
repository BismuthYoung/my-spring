package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.MethodBeforeAdvice
import org.bohan.minispring.aop.MethodInterceptor
import org.bohan.minispring.aop.MethodInvocation

class MethodBeforeAdviceInterceptor(
    private val advice: MethodBeforeAdvice
): MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getArguments())

        return invocation.proceed()
    }
}