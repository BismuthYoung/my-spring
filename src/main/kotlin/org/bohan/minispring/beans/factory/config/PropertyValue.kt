package org.bohan.minispring.beans.factory.config

/**
 * 属性值的封装类，用于setter注入
 *
 * @author Bohan
 * @property value 该成员变量的值。若为 bean 则输入 Bean 的名称，若非 Bean 则输入具体值
 * @property type 该成员变量的类文件
 * @property name 该成员变量的名称
 */
data class PropertyValue(
    val name: String,
    val value: Any?,
    val type: Class<*>
) {
    constructor(name: String, value: Any): this(name, value, value.javaClass)
}
