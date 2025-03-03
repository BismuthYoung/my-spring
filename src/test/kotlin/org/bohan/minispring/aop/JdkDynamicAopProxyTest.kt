package org.bohan.minispring.aop

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Method

class JdkDynamicAopProxyTest {

    interface UserService {
        fun getUserName(id: String): String
    }

    class UserServiceImpl: UserService {
        override fun getUserName(id: String): String {
            return "User: $id"
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
            return method.name == "getUserName"
        }
    }

    @Test
    fun testJdkDynamicAopProxy() {
        // 创建目标对象
        val userService = UserServiceImpl()

        // 创建AOP配置
        val advisedSupport = AdvisedSupport()
        advisedSupport.targetSource = TargetSource(userService)
        advisedSupport.methodInterceptor = LoggingMethodInterceptor()
        advisedSupport.methodMatcher = SimpleMethodMather()

        // 创建代理对象
        val proxy = JdkDynamicAopProxy(advisedSupport)
        val proxyService = proxy.getProxy() as UserService

        // 测试方法调用
        val result = proxyService.getUserName("1")
        assertEquals("User: 1", result)
    }
}