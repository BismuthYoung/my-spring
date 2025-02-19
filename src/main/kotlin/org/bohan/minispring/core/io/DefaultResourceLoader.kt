package org.bohan.minispring.core.io

import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URL

/**
 * ResourceLoader接口的默认实现, 可以加载类路径资源和URL资源
 */
class DefaultResourceLoader: ResourceLoader {

    private val logger = LoggerFactory.getLogger(DefaultResourceLoader::class.java)

    private var classLoader: ClassLoader

    constructor() {
        this.classLoader = this.javaClass.classLoader
    }

    constructor(classLoader: ClassLoader?) {
        this.classLoader = classLoader ?: this.javaClass.classLoader
    }

    override fun getResource(location: String): Resource {
        if (location.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
            // 类路径资源
            return ClassPathResource(location.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length), getClassLoader())
        }

        return try {
            // 尝试作为URL
            val url = URL(location)
            UrlResource(url)
        } catch (e: MalformedURLException) {
            try {
                // 作为文件系统路径
                FileSystemResource(location)
            } catch (e: FileNotFoundException) {
                throw e
            }
        }
    }

    override fun getClassLoader(): ClassLoader {
        return this.classLoader
    }

}