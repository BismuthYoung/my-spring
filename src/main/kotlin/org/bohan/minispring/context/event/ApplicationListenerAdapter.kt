package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationContext
import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class ApplicationListenerAdapter(
    private val target: Any,
    private val method: Method,
    private val eventType: Class<out ApplicationEvent>
): ApplicationListener<ApplicationEvent> {

    private val logger = LoggerFactory.getLogger(ApplicationListenerAdapter::class.java)

    init {
        method.isAccessible = true
    }

    override fun <E> onApplicationEvent(event: E) {
        if (eventType.isInstance(event)) {
            try {
                method.invoke(target, event)
            } catch (ex: Exception) {
                logger.error("Failed to invoke event listener method: $method", ex)
            }
        }
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    fun getEventType(): Class<out ApplicationEvent> = this.eventType

    /**
     * 获取目标对象
     *
     * @return 目标对象
     */
    fun getTarget(): Any = this.target

    /**
     * 获取处理方法
     *
     * @return 处理方法
     */
    fun getMethod(): Method = this.method

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApplicationListenerAdapter) return false
        return this.target == other.target && this.method == other.method
    }

    override fun hashCode(): Int {
        return 31 * target.hashCode() + method.hashCode()
    }

    override fun toString(): String {
        return "ApplicationListenerAdapter: target = [$target], method = [$method]"
    }
}