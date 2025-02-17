package org.bohan.minispring.beans.converter

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

class DefaultConverterTest {

    private lateinit var converter: DefaultTypeConverter

    @BeforeEach
    fun setup() {
        converter = DefaultTypeConverter()
    }

    @Test
    fun testPrimitiveTypes() {
        // 测试整数类型转换
        assertEquals(123, converter.convertIfNecessary("123", Int::class.java))
        assertEquals(123, converter.convertIfNecessary("123", Integer::class.java))
        // 测试长整数类型转换
        assertEquals(123L, converter.convertIfNecessary("123", Long::class.java))
        // 测试浮点数类型转换
        assertEquals(123.45, converter.convertIfNecessary("123.45", Double::class.java))
        assertEquals(123.45f, converter.convertIfNecessary("123.45", Float::class.java))
        // 测试布尔类型转换
        assertTrue(converter.convertIfNecessary("true", Boolean::class.java) !!)
        // 测试字符类型转换
        assertEquals('A', converter.convertIfNecessary("A", Char::class.java))
    }

    @Test
    fun convertBigNumbers() {
        // 测试BigDecimal转换
        val bigDecimal = converter.convertIfNecessary("123.45", BigDecimal::class.java)
        assertEquals(bigDecimal, BigDecimal("123.45"))

        val bigInteger = converter.convertIfNecessary("123", BigInteger::class.java)
        assertEquals(bigInteger, BigInteger("123"))
    }

    @Test
    fun testConvertDate() {
        val date = converter.convertIfNecessary("2024-02-17 02:01:37", Date::class.java)
        assertNotNull(date)
    }

    @Test
    fun testConvertWithNullValue() {
        assertNull(converter.convertIfNecessary(null, String::class.java))
    }

    @Test
    fun testConvertWithInvalidValue() {
        // 测试无效值转换
        assertThrows<TypeMismatchException> {
            converter.convertIfNecessary("abc", Int::class.java)
        }

        assertThrows<TypeMismatchException> {
            converter.convertIfNecessary("3366", Date::class.java)
        }
    }

    @Test
    fun testRegisterCustomConverter() {
        converter.registerConverter(CustomValue::class.java) { s -> CustomValue(s) }

        val result = converter.convertIfNecessary("123", CustomValue::class.java)
        assertEquals("123", result?.value)
    }

    /**
     * 用于测试的自定义类型
     */
    class CustomValue(
        val value: String
    )

}