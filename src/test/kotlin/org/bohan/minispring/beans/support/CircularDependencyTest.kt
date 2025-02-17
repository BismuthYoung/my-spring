package org.bohan.minispring.beans.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.config.PropertyValue
import org.bohan.minispring.beans.factory.support.DefaultListableBeanFactory
import org.bohan.minispring.beans.factory.support.GenericBeanDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class CircularDependencyTest {

    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeEach
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun testCircularDependencyWithSetter() {
        // 注册CircularA的定义
        val beanDefinitionA = GenericBeanDefinition(CircularA::class.java)
        beanFactory.registerBeanDefinition("circularA", beanDefinitionA)

        // 注册CircularB的定义
        val beanDefinitionB = GenericBeanDefinition(CircularB::class.java)
        beanFactory.registerBeanDefinition("circularB", beanDefinitionB)

        // 设置CircularA依赖CircularB
        val beanDefinitionAHolder = beanFactory.getBeanDefinitionHolder("circularA")
        beanDefinitionAHolder.addPropertyValue(
            PropertyValue("circularB", "circularB", CircularB::class.java)
        )

        // 设置CircularB依赖CircularA
        val beanDefinitionBHolder = beanFactory.getBeanDefinitionHolder("circularB")
        beanDefinitionBHolder.addPropertyValue(
            PropertyValue("circularA", "circularA", CircularA::class.java)
        )

        // 获取并验证Bean
        val circularB = beanFactory.getBean("circularB", CircularB::class.java)
        val circularA = beanFactory.getBean("circularA", CircularA::class.java)

        assertNotNull(circularA)
        assertNotNull(circularB)
        assertSame(circularB, circularA.getCircularB())
        assertSame(circularA, circularB.getCircularA())
    }

    @Test
    fun testCircularDependencyWithConstructor() {
        // 注册 circularC 的定义
        val beanDefinitionC = GenericBeanDefinition(CircularC::class.java)
        beanFactory.registerBeanDefinition("circularC", beanDefinitionC)

        // 注册 circularD 的定义
        val beanDefinitionD = GenericBeanDefinition(CircularD::class.java)
        beanFactory.registerBeanDefinition("circularD", beanDefinitionD)

        // 设置构造器依赖
        assertThrows<BeansException> {
            beanFactory.getBean("circularC")
        }
    }

    /**
     * 用于测试 Setter 注入依赖的类 A
     */
    class CircularA {

        private var circularB: CircularB? = null

        fun setCircularB(circularB: CircularB) {
            this.circularB = circularB
        }

        fun getCircularB(): CircularB? {
            return circularB
        }

    }

    /**
     * 用于测试 Setter 注入依赖的类 B
     */
    class CircularB {

        private var circularA: CircularA? = null

        fun setCircularA(circularA: CircularA) {
            this.circularA = circularA
        }

        fun getCircularA(): CircularA? {
            return circularA
        }

    }

    /**
     * 用于测试构造器注入依赖的类 C
     */
    data class CircularC(
        private val circularD: CircularD
    )

    /**
     * 用于测试构造器注入依赖的类 D
     */
    class CircularD(
        private val circularC: CircularC
    )
}