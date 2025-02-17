package org.bohan.minispring.core.io

import org.bohan.minispring.utils.Assert
import org.bohan.minispring.utils.StringUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * 文件系统资源实现类
 * 用于加载文件系统中的资源文件
 *
 * @author Bohan
 */
class FileSystemResource(
    private val path: String
): Resource {

    private val logger = LoggerFactory.getLogger(FileSystemResource::class.java)
    private val file: File

    init {
        Assert.notNull(path, "Path must not be null")
        val cleanedPath = StringUtils.cleanPath(path)
        this.file = File(cleanedPath)
    }

    constructor(file: File): this(file.path) {
        Assert.notNull(file, "File must not be null")
    }

    override fun getInputStream(): InputStream? {
        try {
            val `is` = Files.newInputStream(file.toPath())
            logger.debug("Opened InputStream for {}", getDescription())
            return `is`
        } catch (e: IOException) {
            throw FileNotFoundException("${getDescription()} cannot be found since file doesn't exist")
        }
    }

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun getDescription(): String {
        return "File [$path]"
    }

    override fun getFileName(): String? {
        return file.name
    }

    override fun isReadable(): Boolean {
        return file.canRead()
    }

    override fun lastModified(): Long {
        val lastModified = file.lastModified()
        if (lastModified == 0L && ! file.exists()) {
            throw FileNotFoundException("${getDescription()} cannot be resolved in the file system for resolving its last-modified timestamp")
        }

        return lastModified
    }

    /**
     * 获取文件的绝对路径
     *
     * @return 绝对路径
     */
    fun getAbsolutePath(): String {
        return file.absolutePath
    }

    /**
     * 创建相对于此资源的新资源
     *
     * @param relativePath 相对路径
     * @return 新的文件系统资源
     */
    fun createRelative(relativePath: String): FileSystemResource {
        val pathToUse = StringUtils.cleanPath(path)
        val parent = pathToUse?.let { Paths.get(it).parent }

        return if (parent != null) {
            FileSystemResource(parent.resolve(relativePath).toString())
        } else {
            FileSystemResource(relativePath)
        }
    }
}