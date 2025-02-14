package org.bohan.minispring.beans.factory.config

/**
 * 构造器参数值的封装类
 *
 * @author Bohan
 */
data class ConstructorArgumentValue(
    val name: String,
    val value: Any,
    val type: Class<Any>
)
