package org.bohan.minispring.utils

import java.lang.reflect.Modifier

/**
 * 类工具类
 *
 * @author Bohan
 */
object ClassUtils {

    /** 默认类加载器数组 */
    private val EMPTY_CLASS_LOADER_ARRAY = arrayOfNulls<ClassLoader>(0)

    /**
     * 获取默认的类加载器
     * 优先使用当前线程的上下文类加载器，其次使用加载当前类的类加载器，最后使用系统类加载器
     *
     * @return 类加载器
     */
    @JvmStatic
    fun getDefaultClassLoader(): ClassLoader? {
        // 获取当前线程的上下文类加载器
        var cl: ClassLoader? = null
        try {
            cl = Thread.currentThread().contextClassLoader
        } catch (ex: Throwable) {
            // 无法访问线程上下文类加载器，忽略异常
        }
        if (cl == null) {
            // 使用加载当前类的类加载器
            cl = ClassUtils::class.java.classLoader
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader()
                } catch (ex: Throwable) {
                    // 无法访问系统类加载器，忽略异常
                }
            }
        }

        return cl
    }

    /**
     * 获取类的包名
     *
     * @param clazz 类
     * @return 包名
     */
    @JvmStatic
    fun getPackageName(clazz: Class<*>?): String {
        Assert.notNull(clazz, "Class name must not be null")
        return clazz!!.packageName
    }

    /**
     * 获取类名的包名部分
     *
     * @param fullQualifiedClassName 完全限定的类名
     * @return 包名
     */
    @JvmStatic
    fun getPackageName(fullQualifiedClassName: String?): String {
        Assert.notNull(fullQualifiedClassName, "Class name must not be null")
        val lastIndex = fullQualifiedClassName!!.lastIndexOf('.')
        return if (lastIndex != -1) fullQualifiedClassName.subSequence(0, lastIndex).toString()
            else ""
    }

    /**
     * 判断类是否是内部类
     *
     * @param clazz 类
     * @return 如果是内部类返回true，否则返回false
     */
    fun isInnerClass(clazz: Class<*>?): Boolean {
        return (clazz != null && clazz.isMemberClass && ! isStaticClass(clazz))
    }

    /**
     * 判断类是否是静态类
     *
     * @param clazz 类
     * @return 如果是静态类返回true，否则返回false
     */
    fun isStaticClass(clazz: Class<*>?): Boolean {
        return (clazz != null && clazz.modifiers == Modifier.STATIC)
    }

}