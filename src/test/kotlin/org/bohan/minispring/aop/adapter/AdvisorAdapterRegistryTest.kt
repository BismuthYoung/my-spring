package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.AfterReturningAdvice
import org.bohan.minispring.aop.MethodBeforeAdvice
import org.bohan.minispring.aop.MethodInvocation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Method

/**
 * 通知适配器注册表测试类
 *
 * @author Bohan
 */
class AdvisorAdapterRegistryTest {

    private lateinit var registry: AdvisorAdapterRegistry

    @BeforeEach
    fun setup() {
        registry = DefaultAdvisorAdapterRegistry()
    }

    @Test
    @Throws(Exception::class)
    fun testMethodBeforeAdvice() {
        val beforeAdvice = TestMethodBeforeAdvise()
        val interceptors = registry.getInterceptors(beforeAdvice)

        assertEquals(1, interceptors.size)
        assertTrue(interceptors.first() is MethodBeforeAdviceInterceptor)

        val method = String::class.java.getDeclaredMethod("toString")
        val target = "test"
        interceptors.first().invoke(TestMethodInvocation(target, method))

        assertTrue(beforeAdvice.isExecuted())
    }

    @Test
    @Throws(Exception::class)
    fun testMethodAfterReturningAdvice() {
        val afterAdvice = TestAfterReturningAdvice()
        val interceptors = registry.getInterceptors(afterAdvice)

        assertEquals(1, interceptors.size)
        assertTrue(interceptors.first() is AfterReturningInterceptor)

        val method = String::class.java.getDeclaredMethod("toString")
        val target = "test"
        interceptors.first().invoke(TestMethodInvocation(target, method))

        assertTrue(afterAdvice.isExecuted())
    }

    class TestMethodBeforeAdvise: MethodBeforeAdvice {
        private var executed = false

        override fun before(method: Method, args: Array<Any>, target: Any) {
            executed = true
        }

        fun isExecuted(): Boolean {
            return executed
        }
    }

    class TestAfterReturningAdvice: AfterReturningAdvice {
        private var executed = false

        override fun afterReturning(returnValue: Any?, method: Method, args: Array<Any>, target: Any) {
            executed = true
        }

        fun isExecuted(): Boolean {
            return executed
        }
    }

    class TestMethodInvocation(
        private val target: Any,
        private val method: Method
    ): MethodInvocation {
        override fun getMethod(): Method {
            return method
        }

        override fun getThis(): Any {
            return target
        }

        override fun getArguments(): Array<Any> {
            return emptyArray()
        }

        override fun proceed(): Any? {
            return method.invoke(target)
        }

    }

}