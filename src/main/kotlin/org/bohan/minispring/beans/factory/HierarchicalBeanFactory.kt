package org.bohan.minispring.beans.factory

/**
 * 分层的bean工厂接口, 定义了bean工厂的父子关系
 *
 * @author Bohan
 */
interface HierarchicalBeanFactory: BeanFactory {

    /**
     * 获取父bean工厂
     *
     * @return 父bean工厂，如果没有返回null
     */
    fun getParentBeanFactory(): BeanFactory?

    /**
     * 当前bean工厂是否包含指定名称的bean
     * 不会检查父bean工厂
     *
     * @param name bean名称
     * @return 如果包含返回true，否则返回false
     */
    fun containsLocalBean(name: String): Boolean

}