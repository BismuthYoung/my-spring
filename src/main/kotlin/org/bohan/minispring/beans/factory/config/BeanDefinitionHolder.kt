package org.bohan.minispring.beans.factory.config

/**
 * Bean定义的包装类，用于保存Bean定义及其构造参数和属性信息
 *
 * @author Bohan
 */
class BeanDefinitionHolder(
    private val beanDefinition: BeanDefinition,
    private val beanName: String,
) {

    private var aliases: Array<String>? = null

    constructor(beanDefinition: BeanDefinition, beanName: String, aliases: Array<String>) : this(beanDefinition, beanName) {
        this.aliases = aliases
    }

    /**
     * 获取 BeanDefinition
     */
    fun getBeanDefinition(): BeanDefinition = this.beanDefinition

    /**
     * 获取 Bean 名称
     */
    fun getBeanName(): String = this.beanName


}