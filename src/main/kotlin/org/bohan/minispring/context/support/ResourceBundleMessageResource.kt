package org.bohan.minispring.context.support

import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ResourceBundleMessageResource: AbstractMessageSource() {

    private val logger = LoggerFactory.getLogger(ResourceBundleMessageResource::class.java)
    private var bundleClassLoader: ClassLoader? = null
    private val cachedResourceBundles = ConcurrentHashMap<String, MutableMap<Locale, ResourceBundle>>()
    private lateinit var baseName: String

    fun setBasename(basename: String) {
        this.baseName = basename
    }

    fun setBundleClassLoader(classLoader: ClassLoader) {
        this.bundleClassLoader = classLoader
    }

    override fun resolveMessage(code: String, locale: Locale): String? {
        val bundle = getResourceBundle(locale)
        logger.debug("resourceBundle 获得的 bundle 为：{}", bundle)
        if (bundle != null) {
            try {
                return bundle.getString(code)
            } catch (e: MissingResourceException) {
                logger.debug("No message found with code '{}' in bundle '{}'", code, bundle.getBaseBundleName())
            }
        }

        return null
    }

    /**
     * 获取ResourceBundle,优先从缓存中获取
     */
    protected fun getResourceBundle(locale: Locale): ResourceBundle? {
        val bundleMap = this.cachedResourceBundles[this.baseName]
        if (bundleMap != null) {
            val bundle = bundleMap[locale]
            if (bundle != null) {
                return bundle
            }
        }

        try {
            val bundle = getBundle(locale)
            if (bundle != null) {
                val map = (cachedResourceBundles.putIfAbsent(baseName, ConcurrentHashMap())
                    ?: this.cachedResourceBundles[this.baseName])
                map?.set(locale, bundle)

                return bundle
            }
        } catch (e: MissingResourceException) {
            logger.debug("No bundle found for basename '{}'", this.baseName)
        }

        return null
    }

    /**
     * 加载ResourceBundle
     */
    protected fun getBundle(locale: Locale): ResourceBundle? {
        var classLoader = this.bundleClassLoader
        if (classLoader == null) {
            classLoader = Thread.currentThread().contextClassLoader
        }

        // 如果是默认Locale,优先使用默认资源文件
        if (locale == Locale.getDefault()) {
            try {
                return ResourceBundle.getBundle(this.baseName, Locale.ROOT, classLoader)
            } catch (e: MissingResourceException) {
                logger.debug("No default bundle found for basename '{}', falling back to system locale", this.baseName)
            }
        }

        return ResourceBundle.getBundle(this.baseName, locale, classLoader)
    }
}