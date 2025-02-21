package org.bohan.minispring.beans.factory

interface BeanNameAware: Aware {

    /**
     * 设置bean在容器中的名字
     *
     * @param name bean的名字
     */
    fun setBeanName(name: String)

}