package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.config.BeanDefinitionHolder
import org.slf4j.LoggerFactory
import java.beans.PropertyDescriptor
import java.util.concurrent.ConcurrentHashMap

open class DefaultListableBeanFactory: SimpleAliasRegistry(), BeanFactory {

    private val logger = LoggerFactory.getLogger(DefaultListableBeanFactory::class.java)
    /** 存储单例 Bean 的容器 */
    private val singletonObjects = ConcurrentHashMap<String, Any>(256)
    /** 存储 Bean 定义的容器 */
    private val beanDefinitionMap = ConcurrentHashMap<String, BeanDefinitionHolder>(256)

    override fun getBean(name: String): Any {
        val beanName = canonicalName(name)
        val beanDefinitionHolder = getBeanDefinitionHolder(name)
        val beanDefinition = beanDefinitionHolder.getBeanDefinition()

        if (beanDefinition.isSingleton()) {
            var singleton = singletonObjects[beanName]
            if (singleton != null) {
                return singleton
            }

            singleton = createBean(name, beanDefinitionHolder)
            singletonObjects[beanName] = singleton
            logger.debug("Instantiated singleton bean '{}'", beanName)
            return singleton
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
                val parameterTypes = arrayOfNulls<Class<Any>>(constructorArgs.size)
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

}