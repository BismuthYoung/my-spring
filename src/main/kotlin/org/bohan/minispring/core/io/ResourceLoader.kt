package org.bohan.minispring.core.io

/**
 * 资源加载器接口
 * 定义了资源加载的规范
 *
 * @author Bohan
 */
interface ResourceLoader {

    /** 类路径URL前缀 */
    companion object {
        const val CLASSPATH_URL_PREFIX = "classpath:"
    }

    /**
     * 获取资源
     *
     * @param location 资源位置
     * @return 资源对象
     */
    fun getResource(location: String): Resource

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    fun getClassLoader(): ClassLoader

}