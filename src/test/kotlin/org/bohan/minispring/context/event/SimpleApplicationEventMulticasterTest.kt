package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SimpleApplicationEventMulticasterTest {

    private lateinit var multicaster: SimpleApplicationEventMulticaster
    private lateinit var testListener: TestListener

    @BeforeEach
    fun setup() {
        multicaster = SimpleApplicationEventMulticaster()
        testListener = TestListener()
        multicaster.addApplicationListener(testListener)
    }

    @Test
    fun testSynchronousEventMulticasting() {
        val event = TestEvent(this)
        multicaster.multicastEvent(event)

        assertEquals(1, testListener.getEventCount())
    }

    @Test
    fun testAsynchronousEventMulticasting() {
        // 设置异步执行器
        val executor = Executors.newSingleThreadExecutor()
        multicaster.setExecutor(executor)

        // 使用CountDownLatch等待异步事件处理完成
        val latch = CountDownLatch(1)
        val asyncListener: TestListener = object: TestListener() {
            override fun onApplicationEvent(event: ApplicationEvent) {
                super.onApplicationEvent(event)
                latch.countDown()
            }
        }

        // 移除之前的监听器，只使用新的异步监听器
        multicaster.removeAllListeners()
        multicaster.addApplicationListener(asyncListener)

        val event = TestEvent(this)
        multicaster.multicastEvent(event)

        // 等待事件处理完成
        assertTrue(latch.await(1, TimeUnit.SECONDS))
        assertEquals(1, asyncListener.getEventCount())
        assertSame(event, asyncListener.getLastEvent())
    }

    @Test
    fun testListenerRemoval() {
        multicaster.removeApplicationListener(testListener)
        multicaster.multicastEvent(TestEvent(this))

        assertEquals(0, testListener.getEventCount())
    }

    @Test
    fun testRemoveAllListeners() {
        multicaster.removeAllListeners()
        multicaster.multicastEvent(TestEvent(this))

        assertEquals(0, testListener.getEventCount())
    }

    @Test
    fun testEventTypeFiltering() {
        // 发送不同类型的事件
        multicaster.multicastEvent(TestEvent(this))
        multicaster.multicastEvent(OtherEvent(this))

        // 只有TestEvent应该被处理
        assertEquals(1, testListener.getEventCount())
        assertTrue(testListener.getLastEvent() is TestEvent)
    }

    /**
     * 测试用监听器
     */
    open class TestListener: ApplicationListener<TestEvent> {

        private val eventCount = AtomicInteger(0)
        @Volatile
        private lateinit var lastEvent: ApplicationEvent

        override fun onApplicationEvent(event: ApplicationEvent) {
            eventCount.incrementAndGet()
            lastEvent = event
        }

        fun getEventCount(): Int {
            return eventCount.get()
        }

        fun getLastEvent(): ApplicationEvent {
            return lastEvent
        }

    }

    /**
     * 测试用事件类
     */
    class TestEvent(
        source: Any
    ): ApplicationEvent(source)

    /**
     * 其他测试用的事件类
     */
    class OtherEvent(
        source: Any
    ): ApplicationEvent(source)

}