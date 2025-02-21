package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.PropertyValues
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.config.ConstructorArgumentValue
import org.bohan.minispring.beans.factory.config.PropertyValue

class GenericBeanDefinition(
    private var beanClass: Class<*>,
): BeanDefinition {

    private var scope = BeanDefinition.SCOPE_SINGLETON
    private var initMethod: String? = null
    private var destroyMethod: String? = null
    private val constructorArgumentValues = mutableListOf<ConstructorArgumentValue>()
    private var propertyValues: PropertyValues? = null

    init {
        propertyValues = PropertyValues()
    }


    override fun getBeanClass(): Class<*> {
        return beanClass
    }

    override fun setBeanClass(beanClass: Class<*>) {
        this.beanClass = beanClass
    }

    override fun getScope(): String {
        return scope
    }

    override fun setScope(scope: String) {
        this.scope = scope
    }

    override fun isSingleton(): Boolean {
        return BeanDefinition.SCOPE_SINGLETON == scope
    }

    override fun isPrototype(): Boolean {
        return BeanDefinition.SCOPE_PROTOTYPE == scope
    }

    override fun getInitMethodName(): String? {
        return initMethod
    }

    override fun setInitMethodName(initMethodName: String) {
        initMethod = initMethodName
    }

    override fun getDestroyMethodName(): String? {
        return destroyMethod
    }

    override fun setDestroyMethodName(destroyMethodName: String) {
        destroyMethod = destroyMethodName
    }

    override fun getConstructorArgumentValues(): List<ConstructorArgumentValue> {
        return this.constructorArgumentValues
    }

    override fun addConstructorArgumentValue(constructorArgumentValue: ConstructorArgumentValue) {
        this.constructorArgumentValues.add(constructorArgumentValue)
    }

    override fun hasConstructorArgumentValues(): Boolean {
        return this.constructorArgumentValues.isNotEmpty()
    }

    override fun getPropertyValues(): PropertyValues? {
        return this.propertyValues
    }

    override fun setPropertyValues(propertyValues: PropertyValues) {
        this.propertyValues = propertyValues
    }

    override fun addPropertyValue(propertyValue: PropertyValue) {
        this.propertyValues?.addPropertyValue(propertyValue)
    }
}