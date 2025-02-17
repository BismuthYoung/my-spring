package org.bohan.minispring.utils

/**
 * 断言工具类，用于参数校验
 *
 * @author Bohan
 */
object Assert {

    /**
     * 断言对象不为null
     *
     * @param object 要检查的对象
     * @param message 异常消息
     * @throws IllegalArgumentException 如果对象为null
     */
    @JvmStatic
    fun notNull(`object`: Any?, message: String) {
        if (`object` == null) {
            throw IllegalArgumentException(message)
        }
    }

    /**
     * 断言字符串不为空
     *
     * @param text 要检查的字符串
     * @param message 异常消息
     * @throws IllegalArgumentException 如果字符串为null或空
     */
    @JvmStatic

    fun hasText(text: String?, message: String) {
        if (text.isNullOrBlank()) {
            throw IllegalArgumentException(message)
        }
    }

    /**
     * 断言表达式为true
     *
     * @param expression 要检查的表达式
     * @param message 异常消息
     * @throws IllegalArgumentException 如果表达式为false
     */
    @JvmStatic
    fun isTrue(expression: Boolean, message: String) {
        if (! expression) {
            throw IllegalArgumentException(message)
        }
    }

}