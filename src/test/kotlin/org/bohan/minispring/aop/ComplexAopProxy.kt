package org.bohan.minispring.aop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

/**
 * 复杂AOP代理测试类
 * 测试多个通知的组合和执行顺序
 *
 * @author Bohan
 */
class ComplexAopProxyTest {

    private lateinit var orderService: OrderService
    private lateinit var executionOrder: MutableList<String>
    private lateinit var loggingBeforeAdvice: LoggingBeforeAdvice
    private lateinit var validationBeforeAdvice: ValidationBeforeAdvice
    private lateinit var auditAfterAdvice: AuditAfterAdvice

    @BeforeEach
    fun setUp() {
        orderService = OrderServiceImpl()
        executionOrder = mutableListOf()
        loggingBeforeAdvice = LoggingBeforeAdvice(executionOrder)
        validationBeforeAdvice = ValidationBeforeAdvice(executionOrder)
        auditAfterAdvice = AuditAfterAdvice(executionOrder)
    }

    @Test
    fun testMultipleAdvices() {
        // 创建代理工厂
        val proxyFactory = ProxyFactory(orderService)

        // 添加多个通知
        proxyFactory.addAdvice(loggingBeforeAdvice)
        proxyFactory.addAdvice(validationBeforeAdvice)
        proxyFactory.addAdvice(auditAfterAdvice)

        // 获取代理对象
        val proxy = proxyFactory.getProxy() as OrderService

        // 执行业务方法
        val order = Order("123", 100.0)
        proxy.createOrder(order)

        // 验证执行顺序
        assertEquals(4, executionOrder.size)
        assertEquals("logging", executionOrder[0])
        assertEquals("validation", executionOrder[1])
        assertEquals("createOrder", executionOrder[2])
        assertEquals("audit", executionOrder[3])
    }

    @Test
    fun testAdviceWithException() {
        // 创建代理工厂
        val proxyFactory = ProxyFactory(orderService)

        // 添加多个通知
        proxyFactory.addAdvice(loggingBeforeAdvice)
        proxyFactory.addAdvice(validationBeforeAdvice)
        proxyFactory.addAdvice(auditAfterAdvice)

        // 获取代理对象
        val proxy = proxyFactory.getProxy() as OrderService

        // 执行业务方法，使用无效订单金额
        val invalidOrder = Order("123", -100.0)

        // 验证异常抛出
        assertThrows(IllegalArgumentException::class.java) {
            proxy.createOrder(invalidOrder)
        }

        // 验证执行顺序（只有前置通知被执行）
        assertEquals(2, executionOrder.size)
        assertEquals("logging", executionOrder[0])
        assertEquals("validation", executionOrder[1])
    }

    // 测试接口和类
    interface OrderService {
        fun createOrder(order: Order)
    }

    data class Order(val orderId: String, val amount: Double)

    inner class OrderServiceImpl : OrderService {
        override fun createOrder(order: Order) {
            executionOrder.add("createOrder")
        }
    }

    // 测试通知类
    class LoggingBeforeAdvice(private val executionOrder: MutableList<String>) : MethodBeforeAdvice {
        override fun before(method: Method, args: Array<Any>, target: Any) {
            executionOrder.add("logging")
        }
    }

    class ValidationBeforeAdvice(private val executionOrder: MutableList<String>) : MethodBeforeAdvice {
        override fun before(method: Method, args: Array<Any>, target: Any) {
            executionOrder.add("validation")
            val order = args[0] as Order
            if (order.amount <= 0) {
                throw IllegalArgumentException("Order amount must be positive")
            }
        }
    }

    class AuditAfterAdvice(private val executionOrder: MutableList<String>) : AfterReturningAdvice {
        override fun afterReturning(returnValue: Any?, method: Method, args: Array<Any>, target: Any) {
            executionOrder.add("audit")
            val order = args[0] as Order
            // 模拟审计日志记录
            println("Audit: Order ${order.orderId} created successfully")
        }
    }
}
