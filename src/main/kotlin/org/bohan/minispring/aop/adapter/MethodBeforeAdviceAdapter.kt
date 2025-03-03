package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.Advice
import org.bohan.minispring.aop.MethodBeforeAdvice
import org.bohan.minispring.aop.MethodInterceptor

class MethodBeforeAdviceAdapter: AdvisorAdapter {
    override fun supportsAdvice(advice: Advice): Boolean {
        return advice is MethodBeforeAdvice
    }

    override fun getInterceptor(advice: Advice): MethodInterceptor {
        return MethodBeforeAdviceInterceptor(advice as MethodBeforeAdvice)
    }
}