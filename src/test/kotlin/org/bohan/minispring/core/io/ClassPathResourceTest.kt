package org.bohan.minispring.core.io

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.nio.charset.StandardCharsets

class ClassPathResourceTest {

    @Test
    fun testGetInputStream() {
        // 创建一个类路径资源
        val resource = ClassPathResource("test.txt")

        // 获取输入流并读取内容
        resource.getInputStream().use { `is` ->
            val bytes = `is`.readAllBytes()
            val content = String(bytes, StandardCharsets.UTF_8)
            assertEquals("Hello, World!", content.trim())
        }
    }

    @Test
    fun testGetInputStreamWithClassLoader() {
        // 使用指定的类加载器创建类路径资源
        val resource = ClassPathResource("test.txt", javaClass.classLoader)

        // 获取输入流并读取内容
        resource.getInputStream().use { `is` ->
            val bytes = `is`.readAllBytes()
            val content = String(bytes, StandardCharsets.UTF_8)
            assertEquals("Hello, World!", content.trim())
        }
    }

    @Test
    fun testGetInputStreamWithClass() {
        // 使用指定的类创建类路径资源
        val resource = ClassPathResource("test.txt", javaClass)

        // 获取输入流并读取内容
        resource.getInputStream().use { `is` ->
            val bytes = `is`.readAllBytes()
            val content = String(bytes, StandardCharsets.UTF_8)
            assertEquals("Hello, World!", content.trim())
        }
    }

    @Test
    fun testExists() {
        // 测试存在的资源
        val existingResource = ClassPathResource("test.txt")
        assertTrue(existingResource.exists())

        // 测试不存在的资源
        val nonExistingResource = ClassPathResource("non-existing.txt")
        assertFalse(nonExistingResource.exists())
    }

    @Test
    fun testGetFilename() {
        // 测试简单文件名
        val resource1 = ClassPathResource("test.txt")
        assertEquals("test.txt", resource1.getFileName())

        // 测试带路径的文件名
        val resource2 = ClassPathResource("path/to/test.txt")
        assertEquals("test.txt", resource2.getFileName())
    }

    @Test
    fun testGetDescription() {
        // 测试普通描述
        val resource1 = ClassPathResource("test.txt")
        assertEquals("class path resource [test.txt]", resource1.getDescription())

        // 测试带类的描述
        val resource2 = ClassPathResource("test.txt", javaClass)
        assertTrue(resource2.getDescription().contains(javaClass.name))
        assertTrue(resource2.getDescription().contains("test.txt"))
    }

    @Test
    fun testIsReadable() {
        // 测试可读资源
        val readableResource = ClassPathResource("test.txt")
        assertTrue(readableResource.isReadable())

        // 测试不可读资源
        val nonReadableResource = ClassPathResource("non-existing.txt")
        assertFalse(nonReadableResource.isReadable())
    }

    @Test
    fun testLastModified() {
        // 测试获取最后修改时间
        val resource = ClassPathResource("test.txt")
        assertDoesNotThrow {
            val lastModified = resource.lastModified()
            assertTrue(lastModified > 0)
        }

        // 测试不存在的资源
        val nonExistingResource = ClassPathResource("non-existing.txt")
        assertThrows<IOException> { nonExistingResource.lastModified() }
    }
}