package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener
import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

class SimpleApplicationEventMulticaster: ApplicationEventMulticaster {

    private val logger = LoggerFactory.getLogger(SimpleApplicationEventMulticaster::class.java)

    private val listeners = mutableSetOf<ApplicationListener<*>>()

    private lateinit var taskExecutor: Executor

    override fun addApplicationListener(listener: ApplicationListener<*>) {
        synchronized(this.listeners) {
            this.listeners.add(listener)
            logger.debug("Added application listener: {}", listener)
        }
    }

    override fun removeApplicationListener(listener: ApplicationListener<*>) {
        synchronized(this.listeners) {
            this.listeners.remove(listener)
            logger.debug("Removed application listener: {}", listener)
        }
    }

    override fun removeAllListeners() {
        synchronized(this.listeners) {
            this.listeners.clear()
            logger.debug("Removed all application listeners")
        }
    }

    override fun multicastEvent(event: ApplicationEvent) {
        listeners.forEach { listener ->
            val executor = getExecutor()
            if (executor != null) {
                executor.execute {
                    invokeListener(listener, event)
                }
            } else {
                invokeListener(listener, event)
            }
        }
    }

    /**
     * 获取任务执行器
     *
     * @return 任务执行器
     */
    protected fun getExecutor(): Executor? {
        return this.taskExecutor
    }

    /**
     * 设置任务执行器
     *
     * @param taskExecutor 任务执行器
     */
    protected fun setExecutor(taskExecutor: Executor) {
        this.taskExecutor = taskExecutor
    }

    /**
     * 调用监听器处理事件
     */
    protected fun invokeListener(listener: ApplicationListener<out ApplicationEvent>, event: ApplicationEvent) {
        try {
            listener.onApplicationEvent(event)
        } catch (e: Exception) {
            logger.error("Error invoking ApplicationListener", e)
        }
    }

    /**
     * 获取适用于指定事件的所有监听器
     */
    protected fun getApplicationListeners(event: ApplicationEvent): Collection<ApplicationListener<*>> {
        val allListeners = mutableSetOf<ApplicationListener<*>>()

        synchronized(this.listeners) {
            this.listeners.forEach { listener ->
                if (supportsEvent(listener, event)) {
                    allListeners.add(listener)
                }
            }
        }

        return allListeners
    }

    /**
     * 判断监听器是否支持处理该事件
     */
    protected fun supportsEvent(listener: ApplicationListener<*>, event: ApplicationEvent): Boolean {
        val listenerClass = listener.javaClass

        // 首先检查类本身实现的接口
        if (supportsEventForInterfaces(listenerClass.genericInterfaces, event)) {
            return true
        }

        // 然后检查父类实现的接口
        var superclass: Class<*>? = listenerClass.superclass
        while (superclass != null && superclass != Any::class.java) {
            if (supportsEventForInterfaces(superclass.genericInterfaces, event)) {
                return true
            }
            superclass = superclass.superclass
        }

        return false
    }


    /**
     * 检查给定的接口类型是否支持指定的事件
     */
    private fun supportsEventForInterfaces(genericInterfaces: Array<Type>, event: ApplicationEvent): Boolean {
        for (genericInterface in genericInterfaces) {
            if (genericInterface is ParameterizedType) {
                val rawType = genericInterface.rawType

                // 检查原始类型是否是 ApplicationListener
                if (rawType == ApplicationListener::class.java) {
                    val typeArguments = genericInterface.actualTypeArguments
                    if (typeArguments.size == 1) {
                        val typeArgument = typeArguments[0]
                        if (typeArgument is Class<*>) {
                            // 检查泛型参数是否与事件类型匹配
                            val eventClass = typeArgument as Class<*>
                            if (eventClass.isInstance(event)) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

}