package org.bohan.minispring.beans.factory.config

/**
 * Bean定义的包装类，用于保存Bean定义及其构造参数和属性信息
 *
 * @author Bohan
 */
class BeanDefinitionHolder(
    private val beanDefinition: BeanDefinition,
    private val beanName: String
) {

    private val constructorArgumentValues = mutableListOf<ConstructorArgumentValue>()
    private val propertyValues = mutableListOf<PropertyValue>()


    /**
     * 获取 BeanDefinition
     */
    fun getBeanDefinition(): BeanDefinition = this.beanDefinition

    /**
     * 获取 Bean 名称
     */
    fun getBeanName(): String = this.beanName

    /**
     * 添加构造函数参数值
     */
    fun addConstructorArgumentValue(argumentValue: ConstructorArgumentValue) {
        this.constructorArgumentValues.add(argumentValue)
    }

    /**
     * 获取构造函数参数值列表
     */
    fun getConstructorArgumentValues(): List<ConstructorArgumentValue> = ArrayList(this.constructorArgumentValues)

    /**
     * 添加属性值
     */
    fun addPropertyValue(propertyValue: PropertyValue) {
        this.propertyValues.add(propertyValue)
    }

    /**
     * 获取属性值列表
     */
    fun getPropertyValues(): List<PropertyValue> = ArrayList(this.propertyValues)

}