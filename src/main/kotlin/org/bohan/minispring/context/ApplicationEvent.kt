package org.bohan.minispring.context

import java.time.Clock
import java.time.Instant

/**
 * 应用事件的基类
 * 所有应用事件都应该继承此类
 *
 * @author Bohan
 */
abstract class ApplicationEvent(
    private val source: Any,
    private val timeStamp: Instant
) {

    constructor(source: Any): this(source, Clock.systemDefaultZone().instant())

    /**
     * 获取事件发生的时间戳
     *
     * @return 事件时间戳
     */
    fun getTimeStamp(): Instant {
        return this.timeStamp
    }

    /**
     * 获取事件源对象
     *
     * @return 事件源对象
     */
    fun getSource(): Any {
        return this.source
    }

    override fun toString(): String {
        return this.javaClass.simpleName + "[source='$source']"
    }

}