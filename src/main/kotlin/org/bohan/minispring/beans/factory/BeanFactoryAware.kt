package org.bohan.minispring.beans.factory

interface BeanFactoryAware: Aware {

    /**
     * 设置所属的BeanFactory
     *
     * @param beanFactory 所属的BeanFactory
     */
    fun setBeanFactory(beanFactory: BeanFactory)

}