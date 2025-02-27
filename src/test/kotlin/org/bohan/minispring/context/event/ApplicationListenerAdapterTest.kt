package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

import java.lang.reflect.Method


class ApplicationListenerAdapterTest {

    private lateinit var testEventHandler: TestEventHandler

    private lateinit var adapter: ApplicationListenerAdapter

    private lateinit var handleEventMethod: Method

    @BeforeEach
    fun setup() {
        testEventHandler = TestEventHandler()
        handleEventMethod = TestEventHandler::class.java.getMethod("handleEvent", TestEvent::class.java)
        adapter = ApplicationListenerAdapter(testEventHandler, handleEventMethod, TestEvent::class.java)
    }

    @Test
    fun testEventHandling() {
        // 创建测试事件
        val event = TestEvent("test")
        adapter.onApplicationEvent(event)

        // 验证事件是否被正确处理
        assertTrue(testEventHandler.isEventHandled())
        assertEquals(event, testEventHandler.getLastHandledEvent())
    }

    @Test
    fun testEventTypeFiltering() {
        // 创建不匹配的事件类型
        val event = OtherEvent("test")
        adapter.onApplicationEvent(event)

        // 验证不匹配的事件是否被正确过滤
        assertFalse(testEventHandler.isEventHandled())
        assertNull(testEventHandler.getLastHandledEvent())
    }

    @Test
    fun testEqualAndHashCode() {
        val adapter2 = ApplicationListenerAdapter(testEventHandler, handleEventMethod, TestEvent::class.java)
        val otherHandler = TestEventHandler()
        val adapter3 = ApplicationListenerAdapter(otherHandler, handleEventMethod, TestEvent::class.java)

        // 测试相等性
        assertEquals(adapter, adapter2)
        assertNotEquals(adapter, adapter)

        // 测试哈希码
        assertEquals(adapter.hashCode(), adapter2.hashCode())
        assertNotEquals(adapter.hashCode(), adapter3.hashCode())
    }

    /**
     * 测试用事件处理器
     */
    class TestEventHandler {
        private var eventHandled = false
        private var lastHandledEvent: TestEvent? = null

        // 无参构造方法，使用默认的 TestEvent
        constructor() {}

        // 带参数的构造方法，接受一个 TestEvent
        fun handleEvent(event: TestEvent) {
            this.lastHandledEvent = event
            this.eventHandled = true
        }

        fun isEventHandled(): Boolean {
            return this.eventHandled
        }

        fun getLastHandledEvent(): TestEvent? {
            return lastHandledEvent
        }
    }


    /**
     * 测试用事件
     */
    class TestEvent(
        source: Any
    ): ApplicationEvent(source)


    /**
     * 测试用其他事件
     */
    class OtherEvent(
        source: Any
    ): ApplicationEvent(source)
}