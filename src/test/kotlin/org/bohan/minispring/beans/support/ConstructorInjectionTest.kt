package org.bohan.minispring.beans.support

import org.bohan.minispring.beans.factory.config.ConstructorArgumentValue
import org.bohan.minispring.beans.factory.support.DefaultListableBeanFactory
import org.bohan.minispring.beans.factory.support.GenericBeanDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * 构造器注入测试类
 *
 * @author Bohan
 */
class ConstructorInjectionTest {

    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeEach
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun testConstructorInjection() {
        // 注册依赖的Bean
        val serviceBeanDefinition = GenericBeanDefinition(SimpleService::class.java)
        beanFactory.registerBeanDefinition("serviceBean", serviceBeanDefinition)

        // 注册需要构造器注入的Bean
        val controllerBeanDefinition = GenericBeanDefinition(SimpleController::class.java)
        beanFactory.registerBeanDefinition("controllerBean", controllerBeanDefinition)

        // 添加构造器参数
        val controllerBeanDefinitionHolder =
            beanFactory.getBeanDefinitionHolder("controllerBean")
        controllerBeanDefinitionHolder.addConstructorArgumentValue(
            ConstructorArgumentValue("serviceBean", SimpleService::class.java, "service")
        )

        // 获取并验证Bean
        val controllerBean = beanFactory.getBean("controllerBean", SimpleController::class.java)
        assertNotNull(controllerBean)
        assertNotNull(controllerBean.getService())
        assertEquals("Hello from service!", controllerBean.getService().sayHello())
    }

    /**
     * 用于测试的 Service 类
     */
    class SimpleService {
        fun sayHello(): String {
            return "Hello from service!"
        }
    }

    /**
     * 用于测试的 Controller 类
     */
    class SimpleController(
        private val simpleService: SimpleService
    ) {
        fun getService(): SimpleService {
            return simpleService
        }
    }

}