package org.bohan.minispring.beans.converter

import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class DefaultTypeConverter: TypeConverter {

    private val logger = LoggerFactory.getLogger(DefaultTypeConverter::class.java)

    companion object {
        /** 日期格式 */
        private const val DEFAULT_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    /** 类型转换函数映射 */
    private val converters: MutableMap<Class<*>, (String) -> Any?> = HashMap()

    init {
        // 注册基本类型转换器
        converters[Integer::class.java] = { s -> s.toInt() }
        converters[Int::class.java] = { s -> s.toInt() }
        converters[Long::class.java] = { s -> s.toLong() }
//        converters[Long::class.javaPrimitiveType] = { s -> s.toLong() }
        converters[Double::class.java] = { s -> s.toDouble() }
//        converters[Double::class.javaPrimitiveType] = { s -> s.toDouble() }
        converters[Float::class.java] = { s -> s.toFloat() }
//        converters[Float::class.javaPrimitiveType] = { s -> s.toFloat() }
        converters[Boolean::class.java] = { s -> s.toBoolean() }
//        converters[Boolean::class.javaPrimitiveType] = { s -> s.toBoolean() }
        converters[Short::class.java] = { s -> s.toShort() }
//        converters[Short::class.javaPrimitiveType] = { s -> s.toShort() }
        converters[Byte::class.java] = { s -> s.toByte() }
//        converters[Byte::class.javaPrimitiveType] = { s -> s.toByte() }
        converters[Char::class.java] = { s -> s[0] }
//        converters[Char::class.javaPrimitiveType] = { s -> s[0] }

        // 注册其他常用类型转换器
        converters[BigDecimal::class.java] = { s -> BigDecimal(s) }
        converters[BigInteger::class.java] = { s -> BigInteger(s) }
        converters[String::class.java] = { s -> s }
        converters[Date::class.java] = { s -> parseDate(s) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> convertIfNecessary(value: Any?, requiredType: Class<T>): T? {
        // 如果值为空，则返回空
        if (value == null) {
            return null
        }
        // 如果值已经是目标类型，直接返回
        if (requiredType.isInstance(value)) {
            return value as T
        }
        // 如果值是字符串，尝试转换
        if (value is String) {
            try {
                // 获取转换函数
                val converter = converters[requiredType]
                if (converter != null) {
                    val result = converter(value)
                    return result as T
                }
            } catch (e: Exception) {
                throw TypeMismatchException(value, requiredType, e)
            }
        }

        throw TypeMismatchException(value, requiredType)
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    private fun parseDate(dataStr: String): Date {
        try {
            val dateFormatter = SimpleDateFormat(DEFAULT_DATA_FORMAT)
            return dateFormatter.parse(dataStr)
        } catch (e: ParseException) {
            throw IllegalArgumentException("Fail to parse date: $dataStr", e)
        }
    }

    /**
     * 注册自定义类型转换器
     *
     * @param type 目标类型
     * @param converter 转换函数
     * @param <T> 目标类型的泛型参数
     */
    fun <T> registerConverter(type: Class<T>, converter: (String) -> T) {
        converters[type] = converter
        logger.debug("Registered converter for type '{}'", type.name);
    }

}