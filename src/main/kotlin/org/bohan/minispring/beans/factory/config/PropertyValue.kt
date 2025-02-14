package org.bohan.minispring.beans.factory.config

/**
 * 属性值的封装类，用于setter注入
 *
 * @author Bohan
 */
data class PropertyValue(
    val name: String,
    val value: Any,
    val type: Class<Any>
)
