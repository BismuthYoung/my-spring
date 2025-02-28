package org.bohan.minispring.context

interface MessageSourceResolvable {

    /**
     * 获取消息代码
     * 支持多个代码按优先级排序
     *
     * @return 消息代码数组
     */
    fun getCodes(): Array<String>?

    /**
     * 获取用于替换消息中占位符的参数
     *
     * @return 参数数组,如果没有参数返回null
     */
    fun getArguments(): Array<Any>?

    /**
     * 获取默认消息
     * 当所有消息代码都无法解析时使用
     *
     * @return 默认消息,如果没有默认消息返回null
     */
    fun getDefaultMessage(): String?

}