package org.bohan.minispring.aop

/**
 * AOP代理接口
 * 定义获取代理对象的方法
 *
 * @author Bohan
 */
interface AopProxy {

    /**
     * 获取代理对象
     *
     * @return 代理对象
     */
    fun getProxy(): Any

    /**
     * 使用指定的类加载器获取代理对象
     *
     * @param classLoader 类加载器
     * @return 代理对象
     */
    fun getProxy(classLoader: ClassLoader?): Any

}