package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.ObjectFactory
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.config.BeanDefinitionHolder
import org.slf4j.LoggerFactory
import java.beans.PropertyDescriptor
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * BeanFactory接口的默认实现
 * 基于列表的bean工厂实现，支持单例bean的注册、别名机制、依赖注入和循环依赖处理
 *
 * @author Bohan
 */
open class DefaultListableBeanFactory: SimpleAliasRegistry(), BeanFactory {

    private val logger = LoggerFactory.getLogger(DefaultListableBeanFactory::class.java)
    /** 存储单例 Bean 的容器 - 一级缓存 - 存储完全初始化好的单例 Bean */
    private val singletonObjects = ConcurrentHashMap<String, Any>(256)
    /** 存储单例 Bean 的容器 - 二级缓存 - 存储早期的单例 Bean 对象（未完全初始化）*/
    private val earlySingletonObjects = ConcurrentHashMap<String, Any>(16)
    /** 存储单例 Bean 的容器 - 三级缓存 - 存储单例 Bean 的工厂对象 */
    private val singletonFactories = ConcurrentHashMap<String, ObjectFactory<*>>(16)
    /** 当前 Bean 名称的集合 */
    private val singletonCurrentlyInCreation: MutableSet<String> = Collections.newSetFromMap(ConcurrentHashMap(16))
    /** 存储 Bean 定义的容器 */
    private val beanDefinitionMap = ConcurrentHashMap<String, BeanDefinitionHolder>(256)

    override fun getBean(name: String): Any {
        val beanName = canonicalName(name)
        val beanDefinitionHolder = getBeanDefinitionHolder(name)
        val beanDefinition = beanDefinitionHolder.getBeanDefinition()

        if (beanDefinition.isSingleton()) {
            // 首先检查一级缓存
            var singleton = singletonObjects[beanName]
            if (singleton != null) {
                return singleton
            }

            // 检查是否存在循环依赖
            singleton = getSingletonEarly(name)
            if (singleton != null) {
                return singleton
            }

            // 标记 bean 正在创建中
            beforeSingletonCreation(beanName)
            try {
                // 创建 bean 实例
                singleton = createBean(beanName, beanDefinitionHolder)
                // 将完全初始化的 Bean 放入一级缓存
                addSingleton(beanName, singleton)
                return singleton
            } finally {
                // 移除创建中的标记
                afterSingletonCreation(beanName)
            }
        } else {
            // 如果是原型bean，每次都创建新实例
            val prototype = createBean(name, beanDefinitionHolder)
            logger.debug("Instantiated prototype bean '{}'", beanName)
            return prototype
        }
    }

    override fun <T> getBean(name: String, requiredType: Class<T>): T {
        val bean = getBean(name)
        if (! requiredType.isInstance(bean)) {
            throw BeansException(
                "Bean named '" + name + "' is expected to be of type '" + requiredType.name +
                        "' but was actually of type '" + bean.javaClass.name + "'"
            )
        }

        return requiredType.cast(bean)
    }

    override fun <T> getBean(requiredType: Class<T>): T {
        beanDefinitionMap.entries.forEach { e ->
            if (requiredType.isAssignableFrom(e.value.getBeanDefinition().getBeanClass())) {
                return requiredType.cast(getBean(e.key))
            }
        }

        throw BeansException("No qualifying bean of type '" + requiredType.getName() + "' available")
    }

    override fun containsBean(name: String): Boolean {
        val beanName = canonicalName(name)
        return beanDefinitionMap.containsKey(beanName)
    }

    override fun isSingleton(name: String): Boolean {
        val beanName = canonicalName(name)
        return getBeanDefinitionHolder(beanName).getBeanDefinition().isSingleton()
    }

    override fun isPrototype(name: String): Boolean {
        val beanName = canonicalName(name)
        return getBeanDefinitionHolder(beanName).getBeanDefinition().isPrototype()
    }

    /**
     * 获取bean定义持有者
     *
     * @param name bean的名称
     * @return bean定义持有者
     * @throws BeansException 如果找不到bean定义
     */
    @Throws(BeansException::class)
    fun getBeanDefinitionHolder(name: String): BeanDefinitionHolder {
        val beanName = canonicalName(name)
        return beanDefinitionMap[beanName]
            ?: throw BeansException("No bean named '$name' is defined")
    }

    /**
     * 创建bean实例
     *
     * @param name bean的名称
     * @param beanDefinitionHolder bean定义的持有者
     * @return bean实例
     * @throws BeansException 如果创建bean失败，抛出异常
     */
    protected fun createBean(name: String, beanDefinitionHolder: BeanDefinitionHolder): Any {
        val beanDefinition = beanDefinitionHolder.getBeanDefinition()
        val beanClass = beanDefinition.getBeanClass()
        var bean: Any

        try {
            // 处理构造器注入
            val constructorArgs = beanDefinitionHolder.getConstructorArgumentValues()
            if (constructorArgs.isNotEmpty()) {
                val parameterTypes = arrayOfNulls<Class<*>>(constructorArgs.size)
                val parameterValues = arrayOfNulls<Any>(constructorArgs.size)

                for (i in constructorArgs.indices) {
                    val argumentValue = constructorArgs[i]
                    parameterTypes[i] = argumentValue.type
                    parameterValues[i] = getBean(argumentValue.value.toString())
                }

                val constructor = beanClass.getDeclaredConstructor(* parameterTypes)
                bean = constructor.newInstance(* parameterValues)
            } else {
                bean = beanClass.getDeclaredConstructor().newInstance()
                logger.debug("Created bean '{}' using default constructor", name)
            }

            // 如果是单例且正在创建中（可能存在循环依赖），将工厂放入三级缓存
            if (beanDefinition.isSingleton() && isSingletonCurrentlyInCreation(name)) {
                singletonFactories[name] = object: ObjectFactory<Any> {
                    @Throws(BeansException::class)
                    override fun getObject(): Any {
                        return bean
                    }
                }
                logger.debug("Added factory for singleton bean '{}' to third-level cache", name)
            }

            // 处理 setter 注入
            val propertiesValue = beanDefinitionHolder.getPropertyValues()
            if (propertiesValue.isNotEmpty()) {
                propertiesValue.forEach { propertyValue ->
                    val propertyName = propertyValue.name
                    var value = propertyValue.value

                    // 如果属性值是引用其他bean，则获取对应的bean实例
                    if (value is String && containsBean(value.toString())) {
                        value = getBean(value.toString())
                    }

                    // 使用PropertyDescriptor进行属性注入
                    val pd = PropertyDescriptor(propertyName, beanClass)
                    val writeMethod = pd.writeMethod
                    writeMethod?.invoke(bean, value)
                    logger.debug("Injected property '{}' of bean '{}'", propertyName, name)
                }
            }

            // 调用初始化方法
            val initMethodName = beanDefinition.getInitMethodName()
            if (! initMethodName.isNullOrBlank()) {
                val initMethod = beanClass.getMethod(initMethodName)
                initMethod.invoke(bean)
                logger.debug("Invoked init-method '{}' of bean '{}'", initMethodName, name);
            }
        } catch (e: Exception) {
            throw BeansException("Error creating bean with name '$name'", e)
        }

        return bean
    }

    /**
     * 注册一个bean定义
     *
     * @param name bean的名称
     * @param beanDefinition bean的定义
     */
    fun registerBeanDefinition(name: String, beanDefinition: BeanDefinition) {
        val holder = BeanDefinitionHolder(beanDefinition, name)
        beanDefinitionMap[name] = holder
        logger.debug("Registered bean definition for bean named '{}'", name)
    }

    /**
     * 注册一个单例bean
     *
     * @param name bean的名称
     * @param singletonObject bean实例
     */
    fun registerSingleton(name: String, singletonObject: Any) {
        val beanName = canonicalName(name)
        singletonObjects[beanName] = singletonObject
        logger.debug("Registered singleton bean named '{}'", beanName)
    }

    /**
     * 销毁所有单例bean
     */
    fun destroySingletons() {
        singletonObjects.entries.forEach { entry ->
            val beanName = entry.key
            val bean = entry.value
            val beanDefinitionHolder = beanDefinitionMap[beanName]

            if (beanDefinitionHolder != null) {
                val beanDefinition = beanDefinitionHolder.getBeanDefinition()
                val destroyMethodName = beanDefinition.getDestroyMethodName()
                if (! destroyMethodName.isNullOrBlank()) {
                    try {
                        val destroyMethod = bean.javaClass.getMethod(destroyMethodName)
                        destroyMethod.invoke(bean)
                        logger.debug("Invoked destroy-method '{}' of bean '{}'", destroyMethodName, beanName);
                    } catch (e: Exception) {
                        logger.error("Error invoking destroy-method '{}' of bean '{}'", destroyMethodName, beanName, e)
                    }
                }
            }
        }

        singletonObjects.clear()
        logger.debug("Destroyed all singleton beans")
    }

    /**
     * 获取早期的单例bean（用于处理循环依赖）
     * 其中，早期的单例 Bean 即二级缓存中的 Bean。
     */
    protected fun getSingletonEarly(beanName: String): Any? {
        // 检查二级缓存
        var singleton = earlySingletonObjects[beanName]
        // Bean 正在创建中，需要加锁进行二次检查
        if (singleton == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized(singletonObjects) {
                // 再次检查二级缓存
                singleton = earlySingletonObjects[beanName]
                if (singleton == null) {
                    // 检查三级缓存
                    val factory = singletonFactories[beanName]
                    if (factory != null) {
                        // 从工厂获取对象并放入二级缓存
                        singleton = factory.getObject()
                        if (singleton != null) {
                            earlySingletonObjects[beanName] = singleton !!
                        } else {
                            throw BeansException("Bean '$beanName' from ObjectFactory is null")
                        }
                        singletonFactories.remove(beanName)
                        logger.debug("Created early reference for singleton bean '{}'", beanName)
                    }
                }
            }
        }

        return singleton
    }

    /**
     * 将 Bean 标记为正在创建中
     *
     * @param beanName bean 的名称
     */
    protected fun beforeSingletonCreation(beanName: String) {
        if (! singletonCurrentlyInCreation.add(beanName)) {
            throw BeansException("Circular reference detected during bean creation for singleton '$beanName'")
        }
    }

    /**
     * 移除 Bean 的正在创建标记
     *
     * @param beanName bean 的名称
     */
    protected fun afterSingletonCreation(beanName: String) {
        if (! singletonCurrentlyInCreation.remove(beanName)) {
            throw BeansException("Singleton '$beanName' isn't currently in creation")
        }
    }

    /**
     * 检查 Bean 是否正在创建中
     *
     * @param beanName bean 的名称
     */
    protected fun isSingletonCurrentlyInCreation(beanName: String): Boolean {
        return beanName in singletonCurrentlyInCreation
    }

    /**
     * 将完全初始化的 Bean 添加到一级缓存
     *
     * @param beanName bean 的名称
     * @param singleton 创建好的单例对象
     */
    protected fun addSingleton(beanName: String, singleton: Any) {
        synchronized(singletonObjects) {
            singletonObjects[beanName] = singleton
            earlySingletonObjects.remove(beanName)
            singletonFactories.remove(beanName)
            logger.debug("Added singleton bean '{}' to primary cache", beanName)
        }
    }

}