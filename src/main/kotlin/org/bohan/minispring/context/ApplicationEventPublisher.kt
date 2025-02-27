package org.bohan.minispring.context

/**
 * 事件发布器接口, 封装了事件发布功能
 *
 * @author Bohan
 */
interface ApplicationEventPublisher {

    /**
     * 发布应用事件
     *
     * @param event 要发布的事件
     */
    fun publishEvent(event: ApplicationEvent)

    /**
     * 发布任意对象作为事件
     * 如果对象不是ApplicationEvent，会将其包装为PayloadApplicationEvent
     *
     * @param event 要发布的事件对象
     */
    fun publishEvent(event: Any)

}