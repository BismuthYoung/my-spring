package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.Advice
import org.bohan.minispring.aop.AfterReturningAdvice
import org.bohan.minispring.aop.MethodInterceptor

class AfterReturningAdviceAdapter: AdvisorAdapter {
    override fun supportsAdvice(advice: Advice): Boolean {
        return advice is AfterReturningAdvice
    }

    override fun getInterceptor(advice: Advice): MethodInterceptor {
        return AfterReturningInterceptor(advice as AfterReturningAdvice)
    }
}