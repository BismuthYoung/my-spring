package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.factory.config.BeanDefinition

class GenericBeanDefinition(
    private val beanClass: Class<Any>
): BeanDefinition {

    private var scope = BeanDefinition.SCOPE_SINGLETON
    private var initMethod: String? = null
    private var destroyMethod: String? = null

    override fun getBeanClass(): Class<Any> {
        return beanClass
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
}