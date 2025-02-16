package org.bohan.minispring.beans.factory.config

/**
 * Bean定义接口，描述一个bean的配置信息
 *
 * @author Bohan
 */
interface BeanDefinition {

    companion object {
        const val SCOPE_SINGLETON = "singleton"
        const val SCOPE_PROTOTYPE = "prototype"
    }

    /**
     * 获取Bean的Class对象
     *
     * @return Bean的Class对象
     */
    fun getBeanClass(): Class<*>

    /**
     * 获取Bean的作用域
     *
     * @return Bean的作用域，默认为singleton
     */
    fun getScope(): String

    /**
     * 设置Bean的作用域
     *
     * @param scope Bean的作用域
     */
    fun setScope(scope: String)

    /**
     * 判断是否是单例
     *
     * @return 如果是单例返回true，否则返回false
     */
    fun isSingleton(): Boolean

    /**
     * 判断是否是原型
     *
     * @return 如果是原型返回true，否则返回false
     */
    fun isPrototype(): Boolean

    /**
     * 获取初始化方法名
     *
     * @return 初始化方法名
     */
    fun getInitMethodName(): String?

    /**
     * 设置初始化方法名
     *
     * @param initMethodName 初始化方法名
     */
    fun setInitMethodName(initMethodName: String)

    /**
     * 获取销毁方法名
     *
     * @return 销毁方法名
     */
    fun getDestroyMethodName(): String?

    /**
     * 设置销毁方法名
     *
     * @param destroyMethodName 销毁方法名
     */
    fun setDestroyMethodName(destroyMethodName: String)

}