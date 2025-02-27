package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener

interface ApplicationEventMulticaster {

    /**
     * 添加一个监听器
     *
     * @param listener 要添加的监听器
     */
    fun addApplicationListener(listener: ApplicationListener<*>)

    /**
     * 移除一个监听器
     *
     * @param listener 要移除的监听器
     */
    fun removeApplicationListener(listener: ApplicationListener<*>)

    /**
     * 移除所有监听器
     */
    fun removeAllListeners()

    /**
     * 将事件多播给所有适当的监听器
     *
     * @param event 要多播的事件
     */
    fun multicastEvent(event: ApplicationEvent)

}