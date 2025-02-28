package org.bohan.minispring.context

import org.bohan.minispring.beans.BeansException
import java.util.*

/**
 * MessageSource接口的抽象实现
 * 提供了消息格式化和缓存的基础功能
 *
 * @author Bohan
 */
class NoSuchMessageException : BeansException {

    constructor(code: String) : super("No message found under code '$code'")

    constructor(code: String, locale: Locale) : super("No message found under code '$code' for locale '$locale'")

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}