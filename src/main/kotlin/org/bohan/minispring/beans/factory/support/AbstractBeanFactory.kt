package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.AutowireCapableBeanFactory
import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.ObjectFactory
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.config.BeanPostProcessor
import org.bohan.minispring.beans.factory.config.ConfigurableBeanFactory
import org.slf4j.LoggerFactory
import java.beans.Beans
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractBeanFactory: SimpleAliasRegistry(), ConfigurableBeanFactory, AutowireCapableBeanFactory {

    private val logger = LoggerFactory.getLogger(AbstractBeanFactory::class.java)

    private val beanPostProcessors = mutableListOf<BeanPostProcessor>()

    private var beanClassLoader = Thread.currentThread().contextClassLoader

    private var parentBeanFactory: ConfigurableBeanFactory? = null

    private val singletonObjects = ConcurrentHashMap<String, Any>(256)
    /** 存储单例 Bean 的容器 - 二级缓存 - 存储早期的单例 Bean 对象（未完全初始化）*/
    private val earlySingletonObjects = ConcurrentHashMap<String, Any>(16)
    /** 存储单例 Bean 的容器 - 三级缓存 - 存储单例 Bean 的工厂对象 */
    private val singletonFactories = ConcurrentHashMap<String, ObjectFactory<*>>(16)
    /** 当前 Bean 名称的集合 */
    private val singletonCurrentlyInCreation: MutableSet<String> = Collections.newSetFromMap(ConcurrentHashMap(16))

    /**
     * 在单例创建之前调用
     */
    protected fun beforeSingletonCreation(beanName: String) {
        if (! this.singletonCurrentlyInCreation.add(beanName)) {
            throw BeansException("Bean with '$beanName' is currently in creation")
        }
    }

    /**
     * 在单例创建之后调用
     */
    protected fun afterSingletonCreation(beanName: String) {
        if (! singletonCurrentlyInCreation.remove(beanName)) {
            throw BeansException("Singleton '$beanName' isn't currently in creation")
        }
    }

    /**
     * 添加单例工厂
     */
    protected fun addSingletonFactory(beanName: String, singletonFactory: ObjectFactory<*>) {
        synchronized(this.singletonObjects) {
            if (! singletonFactories.containsKey(beanName)) {
                this.singletonFactories[beanName] = singletonFactory
                this.earlySingletonObjects.remove(beanName)
            }
        }
    }

    override fun registerSingleton(beanName: String, singletonObject: Any) {
        synchronized(this.singletonObjects) {
            this.singletonObjects[beanName] = singletonObject
            this.singletonFactories.remove(beanName)
            this.earlySingletonObjects.remove(beanName)
            logger.debug("Registered singleton bean named '{}'", beanName)
        }
    }

    override fun getSingleton(beanName: String): Any? {
        // 首先从单例缓存中获取
        var singletonObject = singletonObjects[beanName]
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            // 从早期单例缓存中获取
            singletonObject = this.earlySingletonObjects[beanName]
            if (singletonObject == null) {
                val singletonFactory = this.singletonFactories[beanName]
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject()
                    this.earlySingletonObjects[beanName] = singletonFactory
                    this.singletonFactories.remove(beanName)
                }
            }
        }

        return singletonObject
    }

    override fun getBean(name: String): Any {
        return doGetBean(name, null)
    }

    override fun <T> getBean(name: String, requiredType: Class<T>): T {
        return doGetBean(name, requiredType)
    }

    override fun setBeanClassLoader(beanClassLoader: ClassLoader) {
        this.beanClassLoader = beanClassLoader
    }

    override fun getBeanClassLoader(): ClassLoader? {
        return this.beanClassLoader
    }

    override fun setParentBeanFactory(parentBeanFactory: ConfigurableBeanFactory) {
        if (this.parentBeanFactory != null) {
            throw BeansException("Already has a parent BeanFactory")
        }

        this.parentBeanFactory = parentBeanFactory
    }

    override fun getParentBeanFactory(): BeanFactory? {
        return this.parentBeanFactory
    }

    override fun addBeanPostProcessor(beanPostProcessor: BeanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor)
        this.beanPostProcessors.add(beanPostProcessor)
        logger.debug("Added bean post processor: {}", beanPostProcessor)
    }

    override fun getBeanPostProcessor(): List<BeanPostProcessor> {
        return this.beanPostProcessors
    }

    /**
     * 添加单例对象
     */
    fun addSingleton(beanName: String, singletonObject: Any) {
        registerSingleton(beanName, singletonObject)
    }

    /**
     * 检查 Bean 是否正在创建中
     *
     * @param beanName bean 的名称
     */
    protected open fun isSingletonCurrentlyInCreation(beanName: String): Boolean {
        return beanName in singletonCurrentlyInCreation
    }

    /**
     * 获取早期bean引用
     */
    protected fun getEarlyBeanReference(beanName: String, beanDefinition: BeanDefinition, bean: Any): Any {
        return bean
    }

    /**
     * 转换bean名称
     * 处理别名等情况
     */
    protected open fun transformedBeanName(name: String): String {
        return canonicalName(name)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun <T> doGetBean(name: String, requiredType: Class<T>?): T {
        val beanName = transformedBeanName(name)
        var bean: Any?

        // 获取bean定义
        val beanDefinition = getBeanDefinition(beanName) ?: throw BeansException("No bean named '$beanName' is defined")

        // 作用域处理
        if (beanDefinition.isSingleton()) {
            // 对于单例bean，尝试从缓存获取
            bean = getSingleton(beanName)
            if (bean == null) {
                bean = createBean(beanName, beanDefinition)
                addSingleton(beanName, bean)
            }
        } else if (beanDefinition.isPrototype()) {
            // 对于原型bean，每次都创建新实例
            bean = createBean(beanName, beanDefinition)
        } else {
            throw BeansException("Unsupported scope '${beanDefinition.getScope()}' for bean '$beanName'")
        }

        // 类型检查
        if (requiredType != null && ! requiredType.isInstance(bean)) {
            throw BeansException("Bean named '$beanName' is excepted to be type" +
                    "'$requiredType' but was actually of type ${bean.javaClass}")
        }

        return bean as T
    }

    @Throws(BeansException::class)
    protected abstract fun getBeanDefinition(beanName: String): BeanDefinition

    protected abstract fun createBean(beanName: String, beanDefinition: BeanDefinition): Any

//    @Throws(BeansException::class)
//    protected abstract fun getBeanDefinition(beanName: String): BeanDefinition?

    @Throws(BeansException::class)
    protected abstract fun doGetBean(beanName: String): Any

    @Throws(BeansException::class)
    protected abstract fun applyBeanPostProcessorsBeforeInitialization(existingBean: Any, beanName: String): Any

    @Throws(BeansException::class)
    protected abstract fun applyBeanPostProcessorAfterInitialization(existingBean: Any, beanName: String): Any

}