package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.DisposableBean
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractAutowireCapableBeanFactory: AbstractBeanFactory() {

    private val logger = LoggerFactory.getLogger(AbstractAutowireCapableBeanFactory::class.java)
    private val disposableBeans = ConcurrentHashMap<String, DisposableBean>()

    @Throws(BeansException::class)
    protected fun createBeanInstance(beanDefinition: BeanDefinition): Any {
        val beanClass = beanDefinition.getBeanClass() ?: throw BeansException("Bean class is not set for bean definition")

        return try {
            if (beanDefinition.hasConstructorArgumentValues()) autowireConstructor(beanDefinition) else
                beanClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw BeansException("Error creating bean instance for $beanClass", e)
        }
    }

    @Throws(BeansException::class)
    protected fun autowireConstructor(beanDefinition: BeanDefinition): Any {
        val beanClass = beanDefinition.getBeanClass()
        val argumentValues = beanDefinition.getConstructorArgumentValues()

        try {
            val constructors = beanClass.constructors

            constructors.forEach { constructor ->
                if (constructor.parameterCount == argumentValues.size) {
                    val paramTypes = constructor.parameterTypes
                    val args = arrayOfNulls<Any>(argumentValues.size)

                    argumentValues.indices.forEach { i ->
                        val argumentValue = argumentValues[i]
                        var value = argumentValue.value
                        val requiredType = paramTypes[i]

                        if (value is String && requiredType != String::class.java) {
                            // 如果值是字符串但需要的类型不是字符串，尝试获取引用的bean
                            val refBeanName = value.toString()
                            if (this is DefaultListableBeanFactory) {
                                value = this.getSingleton(refBeanName, true)
                                if (value == null) {
                                    value = getBean(refBeanName)
                                }
                            } else {
                                value = getBean(refBeanName)
                            }
                        }

                        args[i] = value
                    }

                    return constructor.newInstance(args)
                }
            }

            throw BeansException("Could not find matching constructor for $beanClass")
        } catch (e: Exception) {
            throw BeansException("Error autowiring constructor for $beanClass", e)
        }
    }

    @Throws(BeansException::class)
    fun populateBean(beanName: String, bean: Any, beanDefinition: BeanDefinition) {
        val propertyValues = beanDefinition.getPropertyValues()
        propertyValues?.getPropertyValues()?.forEach { propertyValue ->
            val propertyName = propertyValue.name
            var value = propertyValue.value
            val type = propertyValue.type

            try {
                if (value is String && type != String::class.java) {
                    // 如果值是字符串但需要的类型不是字符串，尝试获取引用的bean
                    val refBeanName = value.toString()
                    if (this is DefaultListableBeanFactory) {
                        value = this.getSingleton(refBeanName, true)
                        if (value == null) {
                            value = getBean(refBeanName)
                        }
                    } else {
                        value = getBean(refBeanName)
                    }
                }

                val methodName = "set${propertyName.substring(0, 1).uppercase()}${propertyName.substring(1)}"
                val setter = bean.javaClass.getMethod(methodName, type)
                // 设置方法可访问
                setter.isAccessible = true
                setter.invoke(bean, value)
            } catch (e: Exception) {
                throw BeansException("Error getting property '$propertyName' for bean '$beanName'", e)
            }
        }
    }

}