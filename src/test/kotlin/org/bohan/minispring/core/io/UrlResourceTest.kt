package org.bohan.minispring.core.io

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class UrlResourceTest {

    private val logger = LoggerFactory.getLogger(UrlResourceTest::class.java)

    @TempDir
    lateinit var tempDir: Path

    private lateinit var testFile: File
    private lateinit var fileUrlResource: UrlResource
    private val resources = ArrayList<Closeable?>()

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        // 创建测试文件
        testFile = tempDir.resolve("test.txt").toFile()
        Files.write(testFile.toPath(), "Hello, World!".toByteArray(StandardCharsets.UTF_8))
        fileUrlResource = UrlResource(testFile.toURI().toURL())
    }

    /**
     * 关闭所有资源，并删除资源
     */
    @AfterEach
    fun tearDown() {
        resources.forEach { resource ->
            try {
                resource?.close()
                logger.debug("Successfully closed resource: {}", resource)
            } catch (e: IOException) {
                logger.warn("Failed to close resource: {}", resource, e)
            }
        }

        resources.clear()

        // 强制执行 gc 和 finalize
        System.gc()
        System.runFinalization()

        try {
            // 等待一小段时间让文件句柄释放
            Thread.sleep(100)

            // 删除文件
            if (testFile.exists()) {
                val deleted = Files.deleteIfExists(testFile.toPath())
                if (deleted) {
                    logger.debug("Successfully deleted test file: {}", testFile)
                } else {
                    logger.warn("Failed to delete test file: {}", testFile)
                }
            }
        } catch (e: Exception) {
            logger.error("Error during cleanup", e)
        }
    }

    /**
     * 工具方法，跟踪资源
     */
    private fun <T: Closeable> track(resource: T?): T? {
        resources.add(resource)
        logger.debug("Tracking new resource: {}", resource)
        return resource
    }

    @Test
    fun testGetInputStream() {
        val `is` = track(fileUrlResource.getInputStream())
        `is`?.use { inputStream ->
            val content = inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
            assertEquals("Hello, World!", content)
        } ?: logger.warn("Cannot get input stream of file")

    }

    @Test
    @Throws(IOException::class)
    fun testExists() {
        assertTrue(fileUrlResource.exists())

        val nonExistingFile = tempDir.resolve("non-existing.txt")
        val nonExistingResource = UrlResource(nonExistingFile.toUri().toURL())
        assertFalse(nonExistingResource.exists())
    }

    @Test
    fun testDescription() {
        val description = fileUrlResource.getDescription()
        assertTrue(description.startsWith("URL ["))
        assertTrue(description.endsWith("test.txt]"))
    }

    @Test
    fun testGetFilename() {
        // 测试获取文件名
        assertEquals("test.txt", fileUrlResource.getFileName())
    }

    @Test
    fun testIsReadable() {
        // URL资源总是被认为是可读的
        assertTrue(fileUrlResource.isReadable())
    }

    @Test
    fun testLastModified() {
        // 测试最后修改时间
        val lastModified = fileUrlResource.lastModified()
        assertTrue(lastModified > 0)
        assertEquals(testFile.lastModified(), lastModified)
    }

    @Test
    fun testCreateRelative() {
        // 测试创建相对路径资源
        val relativeResource = fileUrlResource.createRelative("relative.txt")
        assertNotNull(relativeResource)
        assertEquals("relative.txt", relativeResource.getFileName())
    }

    @Test
    fun testEqualsAndHashCode() {
        // 测试equals和hashCode方法
        val resource1 = UrlResource(testFile.toURI().toURL())
        val resource2 = UrlResource(testFile.toURI().toURL())

        val otherFile = tempDir.resolve("other.txt").toFile()
        val resource3 = UrlResource(otherFile.toURI().toURL())

        // 测试相等性
        assertEquals(resource1, resource2)
        assertNotEquals(resource1, resource3)

        // 测试哈希码
        assertEquals(resource1.hashCode(), resource2.hashCode())
        assertNotEquals(resource1.hashCode(), resource3.hashCode())
    }

    @Test
    fun testMalformedUrl() {
        // 测试错误的URL格式
        assertThrows<MalformedURLException> { UrlResource("invalid:url") }
    }

}