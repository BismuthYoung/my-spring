package org.bohan.minispring.core.io

import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.jvm.Throws

/**
 * URL资源实现类
 * 支持访问任何可以通过URL访问的资源，包括HTTP、HTTPS、FTP等
 *
 * @author Bohan
 */
class UrlResource(
    private val url: URL
): Resource {

    private val logger = LoggerFactory.getLogger(UrlResource::class.java)

    /**
     * 通过URL字符串创建资源
     *
     * @param url URL字符串
     * @throws MalformedURLException 如果URL格式不正确
     */
    @Throws(MalformedURLException::class)
    constructor(url: String) : this(URL(url))

    override fun getInputStream(): InputStream? {
        val connection = url.openConnection()
        try {
            return connection.getInputStream()
        } catch (e: IOException) {
            if (connection is HttpURLConnection) {
                connection.disconnect()
            }
            throw e
        }
    }

    override fun exists(): Boolean {
        try {
            val connection = url.openConnection()
            // 对于 HTTP 链接，使用状态码来判断资源是否存在
            if (connection is HttpURLConnection) {
                connection.requestMethod = "HEAD"
                val code = connection.responseCode
                connection.disconnect()
                return (code in 200..300)
            }
            // 对于非HTTP URL，尝试获取输入流
            try {
                connection.getInputStream().use { return true }
            } catch (e: Exception) {
                return false
            }
        } catch (e: IOException) {
            logger.debug("Failed to check existence of {}: {}", this.url, e.message)
            return false
        }
    }

    override fun getDescription(): String {
        return "URL [$url]"
    }

    override fun getFileName(): String {
        val path = url.path
        return path.substring(path.lastIndexOf("/") + 1)
    }

    override fun isReadable(): Boolean {
        return true
    }

    override fun lastModified(): Long {
        val connection = url.openConnection()
        try {
            return connection.lastModified
        } finally {
            if (connection is HttpURLConnection) {
                connection.disconnect()
            }
        }
    }

    /**
     * 创建相对于此URL的新资源
     *
     * @param relativePath 相对路径
     * @return 新的URL资源
     * @throws MalformedURLException 如果无法创建新的URL
     */
    @Throws(MalformedURLException::class)
    fun createRelative(relativePath: String): UrlResource {
        return UrlResource(URL(url, relativePath))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UrlResource) return false
        return url == other.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

}