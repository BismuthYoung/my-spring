package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException

interface InitializingBean {

    /**
     * 在bean的所有属性设置完成后调用
     */
    @Throws(BeansException::class)
    fun afterPropertySet()

}