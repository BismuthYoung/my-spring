package org.bohan.minispring.context.support

import org.bohan.minispring.beans.factory.BeanFactory
import org.bohan.minispring.beans.factory.ConfigurableListableBeanFactory
import org.bohan.minispring.context.ApplicationContext
import org.bohan.minispring.core.io.DefaultResourceLoader
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractApplicationContext(
    private val parent: ApplicationContext?
): DefaultResourceLoader(), ApplicationContext {

    private val logger = LoggerFactory.getLogger(AbstractApplicationContext::class.java)

    private var id: String? = null
    private var displayName: String? = null
    private val startupDate = System.currentTimeMillis()
    private val active = AtomicBoolean()
    private val closed = AtomicBoolean()

    constructor(): this(null)

    override fun getId(): String {
        return this.id ?: throw IllegalStateException("ID is not set")
    }

    override fun getDisplayName(): String {
        return this.displayName ?: throw IllegalStateException("Display name is not set")
    }

    override fun getStartUpDate(): Long {
        return this.startupDate
    }

    override fun getParent(): ApplicationContext? {
        return this.parent
    }

    /**
     * 刷新应用上下文
     * 这是一个模板方法，定义了上下文刷新的整体流程
     */
    @Throws(Exception::class)
    open fun refresh() {
        synchronized(this) {
            // 准备刷新上下文
            prepareRefresh()

            // 获取bean工厂
            val beanFactory = obtainFreshBeanFactory()

            // 准备bean工厂
            prepareBeanFactory(beanFactory)

            try {
                // 允许在上下文子类中对bean工厂进行后处理
                postProcessBeanFactory(beanFactory)

                // 调用BeanFactoryPostProcessor
                invokeBeanFactoryPostProcessors(beanFactory)

                // 注册BeanPostProcessor
                registerBeanPostProcessors(beanFactory)

                // 初始化消息源
                initMessageSource()

                // 初始化事件多播器
                initApplicationEventMulticaster()

                // 初始化其他特殊bean
                onRefresh()

                // 注册监听器
                registerListeners()

                // 完成bean工厂的初始化
                finishBeanFactoryInitialization(beanFactory)

                // 完成刷新
                finishRefresh()
            } catch (ex: Exception) {
                logger.error("Context refresh failed", ex)
                throw ex
            }
        }
    }

    protected fun prepareRefresh() {
        this.active.set(true)
        this.closed.set(false)
        logger.info("Refreshing ${getDisplayName()}")
    }

    protected abstract fun obtainFreshBeanFactory(): ConfigurableListableBeanFactory

    protected fun prepareBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // 设置类加载器
        beanFactory.setBeanClassLoader(getClassLoader())
    }

    protected open fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // 默认实现为空，留给子类扩展
    }

    protected fun invokeBeanFactoryPostProcessors(beanFactory: ConfigurableListableBeanFactory) {
        // 执行BeanFactoryPostProcessor
    }

    protected fun registerBeanPostProcessors(beanFactory: ConfigurableListableBeanFactory) {
        // 注册BeanPostProcessor
    }

    protected fun initMessageSource() {
        // 初始化消息源
    }

    protected fun initApplicationEventMulticaster() {
        // 初始化事件多播器
    }

    protected open fun onRefresh() {
        // 留给子类实现特殊bean的初始化
    }

    protected fun registerListeners() {
        // 注册监听器
    }

    protected fun finishBeanFactoryInitialization(beanFactory: ConfigurableListableBeanFactory) {
        // 初始化所有剩余的单例bean
    }

    protected fun finishRefresh() {
        // 完成刷新，发布上下文刷新事件
    }

    /**
     * 设置上下文ID
     */
    fun setId(id: String) {
        this.id = id
    }

    /**
     * 设置显示名称
     */
    fun setDisplayName(displayName: String) {
        this.displayName = displayName
    }

    override fun getBean(name: String): Any {
        return getBeanFactory().getBean(name)
    }

    override fun <T> getBean(name: String, requiredType: Class<T>): T {
        return getBeanFactory().getBean(name, requiredType)
    }

    override fun containsBean(name: String): Boolean {
        return getBeanFactory().containsBean(name)
    }

    override fun isSingleton(name: String): Boolean {
        return getBeanFactory().isSingleton(name)
    }

    override fun isPrototype(name: String): Boolean {
        return getBeanFactory().isPrototype(name)
    }

    override fun getType(name: String): Class<*>? {
        return getBeanFactory().getType(name)
    }

    /**
     * 获取内部的bean工厂
     */
    protected abstract fun getBeanFactory(): BeanFactory

}