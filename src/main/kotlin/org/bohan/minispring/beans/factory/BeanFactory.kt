package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException

/**
 * Bean工厂接口，定义IoC容器的基本功能
 *
 * @author Bohan
 */
interface BeanFactory {

    /**
     * 根据bean的名称获取bean实例
     *
     * @param name bean的名称
     * @return bean实例
     * @throws BeansException 如果获取bean失败，则抛出异常
     */
    @Throws(BeansException::class)
    fun getBean(name: String): Any

    /**
     * 根据bean的名称和类型获取bean实例
     *
     * @param name bean的名称
     * @param requiredType bean的类型
     * @return bean实例
     * @throws BeansException 如果获取bean失败，则抛出异常
     */
    @Throws(BeansException::class)
    fun <T> getBean(name: String, requiredType: Class<T>): T

    /**
     * 根据bean的类型获取bean实例
     *
     * @param requiredType bean的类型
     * @return bean实例
     * @throws BeansException 如果获取bean失败，则抛出异常
     */
    @Throws(BeansException::class)
    fun <T> getBean(requiredType: Class<T>): T

    /**
     * 判断是否包含指定名称的bean
     *
     * @param name bean的名称
     * @return 如果包含返回true，否则返回false
     */
    fun containsBean(name: String): Boolean

    /**
     * 判断指定名称的bean是否为单例
     *
     * @param name bean的名称
     * @return 如果是单例返回true，否则返回false
     * @throws BeansException 如果获取bean失败，则抛出异常
     */
    @Throws(BeansException::class)
    fun isSingleton(name: String): Boolean

    /**
     * 判断指定名称的bean是否为原型
     *
     * @param name bean的名称
     * @return 如果是原型返回true，否则返回false
     * @throws BeansException 如果获取bean失败，则抛出异常
     */
    @Throws(BeansException::class)
    fun isPrototype(name: String): Boolean

}