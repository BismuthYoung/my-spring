package org.bohan.minispring.beans

/**
 * Beans 异常类
 *
 * @author Bohan
 */
open class BeansException : RuntimeException {

    /**
     * 创建一个空的BeansException
     */
    constructor(): super()

    /**
     * 创建一个带有错误信息的BeansException
     *
     * @param message 错误信息
     */
    constructor(message: String): super(message)

    /**
     * 创建一个带有错误信息和原因的BeansException
     *
     * @param message 错误信息
     * @param cause 原因
     */
    constructor(message: String, cause: Throwable) : super(message, cause)


    /**
     * 创建一个带有原因的BeansException
     *
     * @param cause 原因
     */
    constructor(cause: Throwable) : super(cause)

}