package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.config.ConfigurableBeanFactory

/**
 * 提供自动装配能力的bean工厂接口
 *
 * @author Bohan
 */
interface AutowireCapableBeanFactory: BeanFactory {

    companion object {
        /**
         * 不进行自动装配
         */
        const val AUTOWIRE_NO = 0

        /**
         * 通过名称自动装配
         */
        const val AUTOWIRE_BY_NAME = 1

        /**
         * 通过类型自动装配
         */
        const val AUTOWIRE_BY_TYPE = 2

        /**
         * 通过构造函数自动装配
         */
        const val AUTOWIRE_CONSTRUCTOR = 3
    }

    /**
     * 创建一个新的bean实例
     *
     * @param beanClass bean的类型
     * @return 新创建的bean实例
     * @throws BeansException 如果创建失败
     */
    fun <T> createBean(beanClass: Class<T>): T

    /**
     * 自动装配指定的bean
     *
     * @param existingBean 已存在的bean实例
     * @throws BeansException 如果自动装配失败
     */
    @Throws(BeansException::class)
    fun autowireBean(existingBean: Any)

    /**
     * 配置给定的bean实例
     * 应用bean后置处理器、初始化方法等
     *
     * @param existingBean 已存在的bean实例
     * @return 配置后的bean实例
     * @throws BeansException 如果配置失败
     */
    fun configureBean(existingBean: Any, beanName: String): Any

    /**
     * 解析指定bean的依赖
     *
     * @param descriptor bean的描述符
     * @param beanName bean的名称
     * @return 解析后的值
     * @throws BeansException 如果解析失败
     */
    fun resolveDependency(descriptor: Class<*>, beanName: String): Any

    /**
     * 获取bean工厂
     *
     * @return bean工厂
     */
    fun getBeanFactory(): ConfigurableBeanFactory

}