package org.bohan.minispring.context.support

import org.bohan.minispring.context.MessageSource
import org.bohan.minispring.context.MessageSourceResolvable
import org.bohan.minispring.context.NoSuchMessageException
import org.slf4j.LoggerFactory
import java.text.MessageFormat
import java.util.*

/**
 * MessageSource接口的抽象实现
 * 提供了消息格式化和缓存的基础功能
 *
 * @author Bohan
 */
abstract class AbstractMessageSource: MessageSource {

    private val logger = LoggerFactory.getLogger(AbstractMessageSource::class.java)

    private var parentMessageSource: MessageSource? = null

    private var useCodeAsDefaultMessage = false

    override fun getMessage(code: String, args: Array<Any>?, defaultMessage: String, locale: Locale): String {
        val msg = getMessageInternal(code, args, locale)
        if (msg != null) {
            return msg
        }

        if (defaultMessage == null && useCodeAsDefaultMessage) {
            return code
        }

        return defaultMessage
    }

    override fun getMessage(code: String, args: Array<Any>?, locale: Locale): String {
        val msg = getMessageInternal(code, args, locale)
        if (msg != null) {
            return msg
        }
        if (useCodeAsDefaultMessage) {
            return code
        }

        throw NoSuchMessageException(code, locale)
    }

    @Throws(NoSuchMessageException::class)
    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
        val codes = resolvable.getCodes() ?: arrayOf()

        for (code in codes) {
            val msg = getMessageInternal(code, resolvable.getArguments(), locale)
            if (msg != null) {
                return msg
            }
        }

        if (useCodeAsDefaultMessage && codes.isNotEmpty()) {
            return codes[0]
        }

        resolvable.getDefaultMessage()?.let {
            return it
        }

        if (codes.isNotEmpty()) {
            throw NoSuchMessageException(codes[0], locale)
        }

        throw NoSuchMessageException("No message", locale)
    }


    /**
     * 获取消息的内部方法
     * 由子类实现具体的消息解析逻辑
     */
    protected abstract fun resolveMessage(code: String, locale: Locale): String?

    /**
     * 获取消息并进行参数格式化
     */
    protected fun getMessageInternal(code: String, args: Array<Any>?, locale: Locale): String? {
        logger.debug("消息码为：{}，参数为 {}，所属区域为：{}", code, args, locale.country)
        var message = resolveMessage(code, locale)
        if (message == null && parentMessageSource != null) {
            message = parentMessageSource!!.getMessage(code, args, locale)
        }
        if (message != null && ! args.isNullOrEmpty()) {
            return formatMessage(message, args, locale)
        }

        return message
    }

    /**
     * 使用MessageFormat格式化消息
     */
    protected fun formatMessage(msg: String?, args: Array<Any>?, locale: Locale): String? {
        if (msg.isNullOrEmpty() || args.isNullOrEmpty()) {
            return msg
        }
        val trimmedMsg = msg.trim()
        val messageFormat = MessageFormat(trimmedMsg, locale)
        return messageFormat.format(args)
    }

    /**
     * 设置父消息源
     */
    fun setParentMessageSource(parent: MessageSource) {
        this.parentMessageSource = parent
    }

    /**
     * 获取父消息源
     */
    fun getParentMessageSource(): MessageSource? {
        return this.parentMessageSource
    }

    /**
     * 设置是否使用消息代码作为默认消息
     */
    fun setUseCodeAsDefaultMessage(useCodeAsDefaultMessage: Boolean) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage
    }

    /**
     * 是否使用消息代码作为默认消息
     */
    fun isUseCodeAsDefaultMessage(): Boolean {
        return this.useCodeAsDefaultMessage
    }

}