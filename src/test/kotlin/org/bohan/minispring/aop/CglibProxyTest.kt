package org.bohan.minispring.aop

/**
 * Cglib代理测试类
 * 测试Cglib代理的功能
 *
 * @author Bohan
 */
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

class CglibProxyTest {

    private lateinit var userService: UserService
    private lateinit var loggingAdvice: LoggingBeforeAdvice
    private lateinit var auditAdvice: AuditAfterAdvice

    @BeforeEach
    fun setUp() {
        userService = UserService()
        loggingAdvice = LoggingBeforeAdvice()
        auditAdvice = AuditAfterAdvice()
    }

    @Test
    fun testCglibProxy() {
        // 创建代理工厂
        val proxyFactory = ProxyFactory(userService)
        proxyFactory.setProxyTargetClass(true) // 强制使用Cglib代理

        // 添加通知
        proxyFactory.addAdvice(loggingAdvice)
        proxyFactory.addAdvice(auditAdvice)

        // 获取代理对象
        val proxy = proxyFactory.getProxy() as UserService

        // 执行业务方法
        val result = proxy.findUser("test")

        // 验证结果
        assertEquals("User: test", result)
    }

    // 测试类（不实现任何接口）
    open class UserService {
        constructor()

        fun findUser(username: String): String {
            return "User: $username"
        }
    }

    // 测试通知类
    class LoggingBeforeAdvice : MethodBeforeAdvice {
        override fun before(method: Method, args: Array<Any>, target: Any) {
            println("logging")
        }
    }

    class AuditAfterAdvice : AfterReturningAdvice {
        override fun afterReturning(returnValue: Any?, method: Method, args: Array<Any>, target: Any) {
            println("audit")
        }
    }
}
