package org.bohan.minispring.context

interface ApplicationListener<E: ApplicationEvent> {

    /**
     * 处理应用事件
     *
     * @param event 要处理的事件
     */
    fun <E> onApplicationEvent(event: E)

}