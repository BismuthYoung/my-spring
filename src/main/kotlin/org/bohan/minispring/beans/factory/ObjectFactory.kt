package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException

/**
 * 对象工厂接口，用于延迟创建对象
 * 主要用来解决循环依赖问题
 *
 * @author Bohan
 */
interface ObjectFactory<T> {

    /**
     * 获取对象实例
     *
     * @return 对象实例
     * @throws BeansException 如果创建对象失败
     */
    @Throws(BeansException::class)
    fun getObject(): T

}