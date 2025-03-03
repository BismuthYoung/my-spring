package org.bohan.minispring.aop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

/**
 * AOP代理测试类
 * 测试AOP在实际业务场景中的应用
 *
 * @author Bohan
 */
class AopProxyTest {

    private lateinit var executionOrder: MutableList<String>
    private lateinit var userService: UserService
    private lateinit var beforeAdvice: TestMethodBeforeAdvice
    private lateinit var afterReturningAdvice: TestAfterReturningAdvice

    @BeforeEach
    fun setup() {
        this.executionOrder = mutableListOf()
        this.userService = UserServiceImpl()
        this.beforeAdvice = TestMethodBeforeAdvice()
        this.afterReturningAdvice = TestAfterReturningAdvice()
    }

    @Test
    fun testAopProxyWithAdvices() {
        // 创建代理工厂
        val proxyFactory = ProxyFactory(userService)

        // 添加通知
        proxyFactory.addAdvice(beforeAdvice)
        proxyFactory.addAdvice(afterReturningAdvice)

        // 获取代理对象
        val proxy = proxyFactory.getProxy() as UserService

        // 执行业务方法
        val result = proxy.findUser("test")

        // 验证结果

        // 验证结果
        assertEquals("User: test", result)
        assertEquals(3, executionOrder.size)
        assertEquals("before", executionOrder[0])
        assertEquals("findUser", executionOrder[1])
        assertEquals("afterReturning", executionOrder[2])
    }

    /**
     * 测试接口
     */
    interface UserService {
        fun findUser(userName: String): String
    }

    /**
     * 测试实现类
     */
    inner class UserServiceImpl: AopProxyTest.UserService {
        override fun findUser(userName: String): String {
            executionOrder.add("findUser")
            return "User: $userName"
        }
    }

    /**
     * 测试前置通知
     */
    inner class TestMethodBeforeAdvice: MethodBeforeAdvice {
        override fun before(method: Method, args: Array<Any>, target: Any) {
            executionOrder.add("before")
        }
    }

    /**
     * 测试后置通知
     */
    inner class TestAfterReturningAdvice: AfterReturningAdvice {
        override fun afterReturning(returnValue: Any?, method: Method, args: Array<Any>, target: Any) {
            executionOrder.add("afterReturning")
        }
    }

}