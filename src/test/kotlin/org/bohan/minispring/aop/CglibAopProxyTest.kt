package org.bohan.minispring.aop

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Method
import kotlin.Throws


class CglibAopProxyTest {

    open class TestService {
        var name: String = "default"

        constructor()

        constructor(name: String) { // 允许带参构造
            this.name = name
        }

        fun sayHello(): String {
            return "Hello, $name"
        }
    }

    class LoggingMethodInterceptor: MethodInterceptor {
        override fun invoke(invocation: MethodInvocation): Any {
            println("Before method: ${invocation.getMethod()}")
            val result = invocation.proceed()
            println("After method: ${invocation.getMethod()}")

            return result
        }
    }

    class SimpleMethodMather: MethodMatcher {
        override fun matches(method: Method, targetClass: Class<*>): Boolean {
            return method.name == "sayHello"
        }
    }

    @Test
    @Throws(Exception::class)
    fun testCglibProxy() {
        // 创建目标对象
        val target = TestService()

        // 创建AOP配置
        val advisedSupport = AdvisedSupport()
        advisedSupport.targetSource = TargetSource(target)
        advisedSupport.methodInterceptor = JdkDynamicAopProxyTest.LoggingMethodInterceptor()
        advisedSupport.methodMatcher = JdkDynamicAopProxyTest.SimpleMethodMather()

        // 创建代理对象
        val proxy = CglibAopProxy(advisedSupport)
        val proxyObject = proxy.getProxy() as TestService
        proxyObject.name = "Bohan"

        // 调用代理方法
        val result = proxyObject.sayHello()
        assertEquals("Hello, Bohan", result)
    }

}