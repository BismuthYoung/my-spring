package org.bohan.minispring.core.io

import org.bohan.minispring.utils.ClassUtils
import org.bohan.minispring.utils.StringUtils
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL

/**
 * 类路径资源实现类
 * 用于加载类路径下的资源文件
 *
 * @author Bohan
 */
class ClassPathResource: Resource {

    private val logger = LoggerFactory.getLogger(ClassPathResource::class.java)
    private val path: String
    private val classLoader: ClassLoader?
    private val clazz: Class<*>?

    /**
     * 创建一个类路径资源
     *
     * @param path 资源路径
     */
    constructor(path: String) : this(path, null)

    /**
     * 创建一个类路径资源
     *
     * @param path 资源路径
     * @param classLoader 类加载器
     */
    constructor(path: String, classLoader: ClassLoader?) {
        this.path = StringUtils.cleanPath(path) !!
        this.classLoader = classLoader ?: ClassUtils.getDefaultClassLoader()
        this.clazz = null
    }

    /**
     * 创建一个类路径资源
     *
     * @param path 资源路径
     * @param clazz 所属类
     */
    constructor(path: String, clazz: Class<*>) {
        this.path = StringUtils.cleanPath(path) !!
        this.clazz = clazz
        this.classLoader = null
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        val `is`: InputStream = when {
            clazz != null -> {
                var pathToUse = path
                if (!pathToUse.startsWith("/")) {
                    pathToUse = "/$pathToUse"
                }
                clazz.getResourceAsStream(pathToUse)
            }
            classLoader != null -> {
                classLoader.getResourceAsStream(path)
            }
            else -> {
                ClassLoader.getSystemResourceAsStream(path)
            }
        } ?: throw FileNotFoundException("${getDescription()} cannot be opened because it does not exist")

        logger.debug("Opened InputStream for {}", getDescription())
        return `is`
    }


    override fun exists(): Boolean {
        return getUrl() != null
    }

    override fun getDescription(): String {
        val builder = StringBuilder("class path resource [")
        if (clazz != null) {
            builder
                .append(clazz.name)
                .append("/")
        }
        builder
            .append(path)
            .append("]")

        return builder.toString()
    }

    override fun getFileName(): String? {
        return StringUtils.getFilename(path)
    }

    override fun isReadable(): Boolean {
        return exists()
    }

    override fun lastModified(): Long {
        val url = getUrl() ?: throw FileNotFoundException("${getDescription()} cannot be opened because it does not exist")
        try {
            return url.openConnection().lastModified
        } catch (e: IOException) {
            logger.debug("Could not get last-modified timestamp for {}: {}", getDescription(), e.message)
            throw e
        }
    }

    /**
     * 获得类路径的 URL
     */
    private fun getUrl(): URL? {
        return if (clazz != null) {
            clazz.getResource(path)
        } else if (classLoader != null) {
            classLoader.getResource(path)
        } else {
            ClassLoader.getSystemResource(path)
        }
    }

}