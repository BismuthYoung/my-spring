package org.bohan.minispring.core.io

import java.io.IOException
import java.io.InputStream

/**
 * 资源访问接口，定义资源的基本操作
 * 提供统一的资源访问方式，支持类路径、文件系统、URL等多种资源类型
 *
 * @author Bohan
 */
interface Resource {

    /**
     * 获取资源的输入流
     *
     * @return 资源的输入流
     * @throws IOException 如果无法获取输入流
     */
    @Throws(IOException::class)
    fun getInputStream(): InputStream?

    /**
     * 判断资源是否存在
     *
     * @return 如果资源存在返回true，否则返回false
     */
    fun exists(): Boolean

    /**
     * 获取资源的描述信息
     * 通常用于在异常信息和日志中标识资源
     *
     * @return 资源的描述信息
     */
    fun getDescription(): String?

    /**
     * 获取资源的文件名
     * 如果资源没有文件名，返回null
     *
     * @return 资源的文件名
     */
    fun getFileName(): String?

    /**
     * 判断资源是否可读
     *
     * @return 如果资源可读返回true，否则返回false
     */
    fun isReadable(): Boolean

    /**
     * 获取资源的最后修改时间
     *
     * @return 资源的最后修改时间（毫秒）
     * @throws IOException 如果无法获取最后修改时间
     */
    @Throws(IOException::class)
    fun lastModified(): Long

}