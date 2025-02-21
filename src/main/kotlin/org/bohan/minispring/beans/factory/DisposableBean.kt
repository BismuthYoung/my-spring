package org.bohan.minispring.beans.factory

import org.bohan.minispring.beans.BeansException

/**
 * 定义bean销毁时的行为
 *
 * @author Bohan
 */
interface DisposableBean {

    /**
     * 销毁时调用
     */
    @Throws(BeansException::class)
    fun destroy()

}