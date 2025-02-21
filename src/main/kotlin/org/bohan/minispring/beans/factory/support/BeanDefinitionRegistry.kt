package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.config.BeanDefinition

/**
 * Bean定义注册表接口
 * 定义了注册和获取bean定义的基本操作
 *
 * @author Bohan
 */
interface BeanDefinitionRegistry {

    /**
     * 注册一个新的bean定义
     *
     * @param beanName bean名称
     * @param beanDefinition bean定义
     * @throws BeansException 如果bean定义无效或已存在同名的bean定义
     */
    @Throws(BeansException::class)
    fun registerBeanDefinition(beanName: String, beanDefinition: BeanDefinition)

    /**
     * 移除一个bean定义
     *
     * @param beanName bean名称
     * @throws BeansException 如果找不到指定名称的bean定义
     */
    @Throws(BeansException::class)
    fun removeBeanDefinition(beanName: String)

    /**
     * 获取bean定义
     *
     * @param beanName bean名称
     * @return bean定义
     * @throws BeansException 如果找不到指定名称的bean定义
     */
    @Throws(BeansException::class)
    fun getBeanDefinition(beanName: String): BeanDefinition

    /**
     * 判断是否包含指定名称的bean定义
     *
     * @param beanName bean名称
     * @return 如果包含返回true，否则返回false
     */
    fun containsBeanDefinition(beanName: String): Boolean

    /**
     * 获取所有bean定义的名称
     *
     * @return bean定义名称数组
     */
    fun getBeanDefinitionNames(): Array<String>

    /**
     * 获取bean定义的数量
     *
     * @return bean定义的数量
     */
    fun getBeanDefinitionCount(): Int

}