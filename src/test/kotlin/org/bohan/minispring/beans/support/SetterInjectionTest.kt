package org.bohan.minispring.beans.support

import org.bohan.minispring.beans.factory.config.PropertyValue
import org.bohan.minispring.beans.factory.support.DefaultListableBeanFactory
import org.bohan.minispring.beans.factory.support.GenericBeanDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * setter 方法注入测试类
 */
class SetterInjectionTest {

    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeEach
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun testSetterInjection() {
        // 注册依赖的 Bean
        val serviceBeanDefinition = GenericBeanDefinition(SimpleService::class.java)
        beanFactory.registerBeanDefinition("serviceBean", serviceBeanDefinition)

        // 注册需要 setter 注入的 Bean
        val controllerBeanDefinition = GenericBeanDefinition(SimpleController::class.java)
        beanFactory.registerBeanDefinition("controllerBean", controllerBeanDefinition)

        // 添加属性值
        val controllerBeanDefinitionHolder =
            beanFactory.getBeanDefinitionHolder("controllerBean")
        controllerBeanDefinitionHolder.addPropertyValue(
            PropertyValue("service", "serviceBean", SimpleService::class.java)
        )

        // 获取并验证Bean
        val controllerBean = beanFactory.getBean("controllerBean", SimpleController::class.java)
        assertNotNull(controllerBean)
        assertNotNull(controllerBean.service)
        assertEquals("Hello from service!", controllerBean.service?.sayHello())
    }

    @Test
    fun testSetterInjectionWithPrimitiveValue() {
        // 注册需要setter注入的Bean
        val serviceBeanDefinition = GenericBeanDefinition(SimpleService::class.java)
        beanFactory.registerBeanDefinition("serviceBean", serviceBeanDefinition)

        // 注册需要 setter 注入的 Bean
        val controllerBeanDefinition = GenericBeanDefinition(SimpleController::class.java)
        beanFactory.registerBeanDefinition("controllerBean", controllerBeanDefinition)

        // 添加属性值
        val controllerBeanDefinitionHolder =
            beanFactory.getBeanDefinitionHolder("controllerBean")
        controllerBeanDefinitionHolder.addPropertyValue(
            PropertyValue("name", "testName", String::class.java)
        )

        // 获取并验证Bean
        val controllerBean = beanFactory.getBean("controllerBean", SimpleController::class.java)
        assertNotNull(controllerBean)
        assertNotNull(controllerBean.name)
        assertEquals("testName", controllerBean.name)
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
     * 用于测试的 controller 类
     */
    class SimpleController {
        var service: SimpleService? = null
        var name: String? = null
    }

}