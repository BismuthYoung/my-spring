package org.bohan.minispring.core.io

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class FileSystemResourceTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var testFile: File
    private lateinit var resource: FileSystemResource

    @BeforeEach
    fun setUp() {
        // 创建测试文件
        testFile = tempDir.resolve("test.txt").toFile()
        Files.write(testFile.toPath(), "Hello, World!".toByteArray(StandardCharsets.UTF_8))
        resource = FileSystemResource(testFile)
    }

    @Test
    fun testGetInputStream() {
        // 测试读取文件内容
        resource.getInputStream().use { `is` ->
            val content = `is`?.readAllBytes()?.toString(StandardCharsets.UTF_8)
            assertEquals("Hello, World!", content)
        }
    }

    @Test
    fun testExists() {
        // 测试文件存在性检查
        assertTrue(resource.exists())

        // 测试不存在的文件
        val nonExistingResource = FileSystemResource(tempDir.resolve("non-existing.txt").toString())
        assertFalse(nonExistingResource.exists())
    }

    @Test
    fun testGetDescription() {
        // 测试资源描述
        val description = resource.getDescription()
        assertTrue(description.startsWith("File ["))
        assertTrue(description.endsWith("test.txt]"))
    }

    @Test
    fun testGetFilename() {
        // 测试获取文件名
        assertEquals("test.txt", resource.getFileName())
    }

    @Test
    fun testIsReadable() {
        // 测试文件可读性
        assertTrue(resource.isReadable())

        // 创建一个新的只读文件来测试
        try {
            val readOnlyFile = tempDir.resolve("readonly.txt").toFile()
            Files.write(readOnlyFile.toPath(), "Read Only Content".toByteArray(StandardCharsets.UTF_8))
            readOnlyFile.setReadOnly()  // 使用setReadOnly()而不是setReadable(false)

            val readOnlyResource = FileSystemResource(readOnlyFile)
            assertTrue(readOnlyResource.exists())
            assertTrue(readOnlyResource.isReadable())  // 在Windows中，只读文件仍然是可读的

            // 清理
            readOnlyFile.setWritable(true)
            Files.delete(readOnlyFile.toPath())
        } catch (e: IOException) {
            fail("Failed to create or manipulate read-only file: ${e.message}")
        }
    }

    @Test
    fun testLastModified() {
        // 测试最后修改时间
        val lastModified = resource.lastModified()
        assertTrue(lastModified > 0)

        // 测试不存在的文件
        val nonExistingResource = FileSystemResource(tempDir.resolve("non-existing.txt").toString())
        assertThrows<IOException> { nonExistingResource.lastModified() }
    }

    @Test
    fun testGetAbsolutePath() {
        // 测试获取绝对路径
        val absolutePath = resource.getAbsolutePath()
        assertTrue(absolutePath.endsWith("test.txt"))
        assertTrue(File(absolutePath).exists())
    }

    @Test
    fun testGetFile() {
        // 测试获取File对象
        val file = resource.getFile()
        assertEquals(testFile, file)
        assertTrue(file.exists())
    }

    @Test
    fun testCreateRelative() {
        // 测试创建相对路径资源
        val relativeResource = resource.createRelative("relative.txt")
        assertEquals("relative.txt", relativeResource.getFileName())
        assertFalse(relativeResource.exists())
    }

    @Test
    fun testNonExistentFile() {
        // 测试不存在文件的异常处理
        val nonExistingResource = FileSystemResource(tempDir.resolve("non-existing.txt").toString())
        assertThrows<IOException> { nonExistingResource.getInputStream() }
    }
}
