package org.bohan.minispring.beans.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.support.DefaultListableBeanFactory
import org.bohan.minispring.beans.factory.support.GenericBeanDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows


/**
 * DefaultListableBeanFactory的测试类
 *
 * @author Bohan
 */
class DefaultListableBeanFactoryTest {

    private lateinit var beanFactory: DefaultListableBeanFactory

    @BeforeEach
    fun setup() {
        beanFactory = DefaultListableBeanFactory()
    }

    @Test
    fun testRegisterAndGetBean() {
        // 注册TestBean的定义
        val beanDefinition = GenericBeanDefinition(TestBean::class.java)
        beanFactory.registerBeanDefinition("testBean", beanDefinition)

        // 测试getBean(String)
        val bean1 = beanFactory.getBean("testBean")
        assertNotNull(bean1)
        assertTrue(bean1 is TestBean)

        // 测试getBean(String, Class)
        val bean2 = beanFactory.getBean("testBean", TestBean::class.java)
        assertNotNull(bean2)

        // 测试getBean(Class)
        val bean3 = beanFactory.getBean(TestBean::class.java)
        assertNotNull(bean3)

        // 验证单例行为
        assertSame(bean2, bean3)
    }

    @Test
    fun testBeanNotFound() {
        assertThrows<BeansException> {
            beanFactory.getBean("noExistingBean")
        }
    }

    @Test
    fun testContainsBean() {
        val beanDefinition = GenericBeanDefinition(TestBean::class.java)
        beanFactory.registerBeanDefinition("testBean", beanDefinition)
        assertTrue(beanFactory.containsBean("testBean"))
    }

    @Test
    fun testAliasRegistry() {
        // 注册TestBean的定义
        val beanDefinition = GenericBeanDefinition(TestBean::class.java)
        beanFactory.registerBeanDefinition("testBean", beanDefinition)

        // 注册别名
        beanFactory.registerAlias("testBean", "alias1")
        beanFactory.registerAlias("testBean", "alias2")

        // 测试别名是否正确注册
        assertTrue(beanFactory.isAlias("alias1"))
        assertTrue(beanFactory.isAlias("alias2"))
        assertFalse(beanFactory.isAlias("alias"))

        // 测试通过别名获取bean
        val bean1 = beanFactory.getBean("alias1")
        val bean2 = beanFactory.getBean("alias2")
        val originalBean = beanFactory.getBean("testBean")

        // 验证通过不同名称获取的是同一个bean
        assertSame(bean1, originalBean)
        assertSame(bean2, originalBean)

        // 测试获取别名
        val aliases = beanFactory.getAliases("testBean")
        assertEquals(2, aliases.size)
        assertTrue(containsAll(aliases, "alias1", "alias2"))

        // 测试移除别名
        beanFactory.removeAlias("alias1")
        assertTrue(beanFactory.isAlias("alias2"))
        assertFalse(beanFactory.isAlias("alias1"))
    }

    @Test
    fun testScopeAndLifeCycle() {
        // 创建带有初始化和销毁方法的bean定义
        val beanDefinition = GenericBeanDefinition(LifecycleBean::class.java)
        beanDefinition.setInitMethodName("init")
        beanDefinition.setDestroyMethodName("destroy")
        beanFactory.registerBeanDefinition("lifeCycleBean", beanDefinition)

        // 获取bean并验证初始化方法被调用
        val bean = beanFactory.getBean("lifeCycleBean", LifecycleBean::class.java)
        assertTrue(bean.isInitialized())

        // 测试单例作用域
        assertTrue(beanFactory.isSingleton("lifeCycleBean"))
        assertFalse(beanFactory.isPrototype("lifeCycleBean"))

        // 修改作用域为prototype
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE)
        assertFalse(beanFactory.isSingleton("lifeCycleBean"))
        assertTrue(beanFactory.isPrototype("lifeCycleBean"))

        // 获取两个原型bean实例并验证它们不同
        val bean1 = beanFactory.getBean("lifeCycleBean")
        val bean2 = beanFactory.getBean("lifeCycleBean")
        assertNotSame(bean1, bean2)

        // 销毁所有单例bean
        beanFactory.destroySingletons()
        assertTrue(bean.isDestroyed())
    }

    @Test
    fun testCircularAlias() {
        assertThrows<BeansException>() {
            beanFactory.registerAlias("testBean", "alias1")
            beanFactory.registerAlias("alias1", "alias2")
            beanFactory.registerAlias("alias2", "testBean")
        }
    }

    @Test
    fun testAliasOverriding() {
        // 注册TestBean的定义
        val beanDefinition = GenericBeanDefinition(TestBean::class.java)
        beanFactory.registerBeanDefinition("testBean", beanDefinition)

        // 注册别名
        beanFactory.registerAlias("testBean", "alias")

        // 尝试为不同的bean注册相同的别名
        val anotherBeanDefinition = GenericBeanDefinition(TestBean::class.java)
        beanFactory.registerBeanDefinition("anotherBean", anotherBeanDefinition)

        assertThrows<BeansException> {
            beanFactory.registerAlias("anotherBean", "alias")
        }
    }

    private fun containsAll(array: Array<String>, vararg values: String): Boolean {
        return values.all { value -> array.contains(value) }
    }

    /**
     * 用于测试的简单Bean类
     */
    class TestBean {
        var name: String? = null
    }

    /**
     * 用于测试生命周期方法的Bean类
     */
    class LifecycleBean {
        private var initialized = false
        private var destroyed = false

        fun init() {
            initialized = true
        }

        fun destroy() {
            destroyed = true
        }

        fun isInitialized() = initialized
        fun isDestroyed() = destroyed
    }

}