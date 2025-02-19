package org.bohan.minispring.utils

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.config.PropertyValue

/**
 * Bean操作的工具类
 */
object BeanUtils {

    @JvmStatic
    @Throws(BeansException::class)
    fun setProperty(bean: Any, propertyValue: PropertyValue, beanFactory: BeanFactory) {
        val propertyName = propertyValue.name
        var value = propertyValue.value
        val type = propertyValue.type

        try {
            // 如果值是字符串，且类型不是String，尝试从BeanFactory获取引用的bean
            if (value is String && type != String::class.java) {
                val beanName = value.toString()
                value = beanFactory.getBean(beanName, type)
            }
            val methodName = "set${propertyName.substring(0, 1).uppercase()}${propertyName.substring(1)}"
            val setter = bean.javaClass.getMethod(methodName, type)
            // 设置方法可访问
            setter.isAccessible = true
            setter.invoke(bean, value)
        } catch (e: Exception) {
            throw BeansException("Error setting property '$propertyName' to bean", e)
        }
    }

}