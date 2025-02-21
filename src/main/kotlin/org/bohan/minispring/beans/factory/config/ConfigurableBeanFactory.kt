package org.bohan.minispring.beans.factory.config

import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.HierarchicalBeanFactory

interface ConfigurableBeanFactory: HierarchicalBeanFactory, SingletonBeanRegistry {

    companion object {
        /**
         * 单例作用域标识符
         */
        const val SCOPE_SINGLETON = "singleton"

        /**
         * 原型作用域标识符
         */
        const val SCOPE_PROTOTYPE = "prototype"
    }

    /**
     * 设置父bean工厂
     *
     * @param parentBeanFactory 父bean工厂
     */
    fun setParentBeanFactory(parentBeanFactory: ConfigurableBeanFactory)

    /**
     * 设置类加载器
     *
     * @param beanClassLoader 类加载器
     */
    fun setBeanClassLoader(beanClassLoader: ClassLoader)

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    fun getBeanClassLoader(): ClassLoader?

    /**
     * 添加bean后置处理器
     *
     * @param beanPostProcessor bean后置处理器
     */
    fun addBeanPostProcessor(beanPostProcessor: BeanPostProcessor)

    /**
     * 获取所有bean后置处理器
     *
     * @return bean后置处理器列表
     */
    fun getBeanPostProcessor(): List<BeanPostProcessor>

    /**
     * 获取bean后置处理器的数量
     *
     * @return bean后置处理器的数量
     */
    fun getBeanPostProcessorCount(): Int

    /**
     * 注册依赖的bean
     *
     * @param beanName 当前bean的名称
     * @param dependentBeanName 依赖bean的名称
     */
    fun registerDependentBean(beanName: String, dependentBeanName: String)

    /**
     * 获取依赖当前bean的bean名称
     *
     * @param beanName 当前bean的名称
     * @return 依赖的bean名称数组
     */
    fun getDependentBeans(beanName: String): Array<String>

    /**
     * 获取当前bean依赖的bean名称
     *
     * @param beanName 当前bean的名称
     * @return 被依赖的bean名称数组
     */
    fun getDependenciesForBean(beanName: String): Array<String>

    /**
     * 销毁所有单例bean
     */
    fun destroySingletons()

}