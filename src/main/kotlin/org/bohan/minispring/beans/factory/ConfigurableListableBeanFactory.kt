package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.config.ConfigurableBeanFactory

interface ConfigurableListableBeanFactory: ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * 获取bean定义
     *
     * @param beanName bean名称
     * @return bean定义
     * @throws BeansException 如果找不到bean定义
     */
/*    @Throws(BeansException::class)
    fun getBeanDefinition(beanName: String): Any*/

    /**
     * 预实例化所有单例bean
     *
     * @throws BeansException 如果预实例化过程中发生错误
     */
    @Throws(BeansException::class)
    fun preInstantiateSingletons()

    /**
     * 确保所有非延迟加载的单例bean都被实例化
     *
     * @throws BeansException 如果实例化过程中发生错误
     */
    @Throws(BeansException::class)
    fun ensureAllSingletonsInstantiate()

}