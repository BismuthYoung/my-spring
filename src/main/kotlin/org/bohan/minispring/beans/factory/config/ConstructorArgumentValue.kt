package org.bohan.minispring.beans.factory.config

/**
 * 构造器参数值的封装类
 *
 * @author Bohan
 * @property value 该成员变量的值。若为 bean 则输入 Bean 的名称，若非 Bean 则输入具体值
 * @property type 该成员变量的类文件
 * @property name 该成员变量的名称
 */
data class ConstructorArgumentValue(
    val value: Any,
    val type: Class<*>,
    val name: String
)
