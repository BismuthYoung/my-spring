package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.Advice
import org.bohan.minispring.aop.MethodInterceptor
import org.slf4j.LoggerFactory

class DefaultAdvisorAdapterRegistry: AdvisorAdapterRegistry {

    private val logger = LoggerFactory.getLogger(DefaultAdvisorAdapterRegistry::class.java)
    private val adapters = mutableListOf<AdvisorAdapter>()

    init {
        registerAdvisorAdapter(MethodBeforeAdviceAdapter())
        registerAdvisorAdapter(AfterReturningAdviceAdapter())
    }

    override fun registerAdvisorAdapter(adapter: AdvisorAdapter) {
        this.adapters.add(adapter)
    }

    override fun getInterceptors(advice: Advice): Array<MethodInterceptor> {
        val interceptors = mutableListOf<MethodInterceptor>()

        // 如果已经是 MethodInterceptor，直接添加
        if (advice is MethodInterceptor) {
            interceptors.add(advice)
        }

        // 遍历所有适配器，找到支持该通知的适配器
        for (adapter in adapters) {
            if (adapter.supportsAdvice(advice)) {
                interceptors.add(adapter.getInterceptor(advice))
            }
        }

        return interceptors.toTypedArray()
    }

    override fun wrap(advice: Advice): MethodInterceptor {
        if (advice is MethodInterceptor) {
            return advice
        }

        // 遍历所有适配器，找到支持该通知的适配器
        adapters.forEach { adapter ->
            if (adapter.supportsAdvice(advice)) {
                return adapter.getInterceptor(advice)
            }
        }

        throw IllegalArgumentException(
            "Advice type [" + advice.javaClass.name +
                    "] is not supported by any registered adapter"
        )
    }

}