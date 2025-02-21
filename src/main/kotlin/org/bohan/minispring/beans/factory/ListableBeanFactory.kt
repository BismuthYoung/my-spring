package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException

/**
 * 可列表化的bean工厂接口，提供了枚举bean的功能
 *
 * @author Bohan
 */
interface ListableBeanFactory: BeanFactory {

    /**
     * 判断是否包含指定名称的bean定义
     *
     * @param beanName bean名称
     * @return 如果包含返回true，否则返回false
     */
//    fun containsBeanDefinition(beanName: String): Boolean

    /**
     * 获取bean定义的数量
     *
     * @return bean定义的数量
     */
    fun getBeanDefinitionCount(): Int

    /**
     * 获取所有bean定义的名称
     *
     * @return bean定义名称数组
     */
//    fun getBeanDefinitionNames(): Array<String>

    /**
     * 根据类型获取bean的名称
     *
     * @param type bean类型
     * @return bean名称数组
     */
    fun getBeanNamesForType(type: Class<*>): Array<String>

    /**
     * 根据类型获取bean实例
     *
     * @param type bean类型
     * @return bean实例的Map，key为bean名称，value为bean实例
     * @throws BeansException 如果获取bean失败
     */
    @Throws(BeansException::class)
    fun <T> getBeansOfType(type: Class<T>): Map<String, T>

    /**
     * 获取带有指定注解的bean
     *
     * @param annotationType 注解类型
     * @return bean实例的Map，key为bean名称，value为bean实例
     * @throws BeansException 如果获取bean失败
     */
    @Throws(BeansException::class)
    fun <T: Annotation> getBeansWithAnnotation(annotationType: T): Map<String, Any>

    /**
     * 获取指定bean上的注解
     *
     * @param beanName bean名称
     * @param annotationType 注解类型
     * @return 注解实例，如果不存在返回null
     */
    @Throws(BeansException::class)
    fun <A: Annotation> findAnnotationOnBean(beanName: String, annotationType: Class<A>): A

}