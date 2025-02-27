package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationContext
import org.bohan.minispring.context.ApplicationEvent
import org.bohan.minispring.context.ApplicationListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ApplicationEventTest {

    @Mock
    private lateinit var mockContext: ApplicationContext

    private lateinit var eventMulticaster: SimpleApplicationEventMulticaster

    private lateinit var testListener: TestApplicationListener

    companion object {
        private val FIXED_TIME: Instant = Instant.parse("2024-01-10T10:00:00Z")
        private val FIXED_CLOCK: Clock = Clock.fixed(FIXED_TIME, ZoneId.systemDefault())
    }

    @BeforeEach
    fun setup() {
        this.eventMulticaster = SimpleApplicationEventMulticaster()
        this.testListener = TestApplicationListener()
        eventMulticaster.addApplicationListener(testListener)
        MockitoAnnotations.openMocks(this) // 初始化mock对象
    }

    @Test
    fun testContextRefreshedEvent() {
        // 创建并发布ContextRefreshedEvent
        val event = ContextRefreshedEvent(mockContext)
        eventMulticaster.multicastEvent(event)

        // 验证监听器是否正确接收到事件
        assertEquals(1, testListener.getRefreshedEvent().size)
        assertEquals(0, testListener.getClosedEvent().size)
        assertSame(event, testListener.getRefreshedEvent()[0])
    }

    @Test
    fun testContextClosedEvent() {
        // 创建并发布ContextClosedEvent
        val event = ContextClosedEvent(mockContext)
        eventMulticaster.multicastEvent(event)

        // 验证监听器是否正确接收到事件
        assertEquals(0, testListener.getRefreshedEvent().size)
        assertEquals(1, testListener.getClosedEvent().size)
        assertSame(event, testListener.getClosedEvent()[0])
    }

    @Test
    fun testEventTimeStamp() {
        // 使用固定时间创建事件
        val event = TestEvent("test", FIXED_CLOCK)
        assertEquals(FIXED_TIME, event.getTimeStamp())
    }

    @Test
    fun testEventSource() {
        val source = "testSource"
        val event = TestEvent(source)
        assertEquals(event.getSource(), source)
    }

    @Test
    fun testListenerRemoval() {
        eventMulticaster.removeApplicationListener(testListener)
        val event = ContextRefreshedEvent(mockContext)
        eventMulticaster.multicastEvent(event)

        assertEquals(0, testListener.getRefreshedEvent().size)
    }

    @Test
    fun testRemoveAllListeners() {
        eventMulticaster.removeAllListeners()

        assertEquals(0, testListener.getRefreshedEvent().size)
    }

    /**
     * 测试用事件类
     */
    class TestEvent(
        source: Any,
        clock: Clock = Clock.systemDefaultZone() // 使用默认值的Clock
    ) : ApplicationEvent(source, clock.instant()) {


    }

    /**
     * 测试用监听器类
     */
    class TestApplicationListener: ApplicationListener<ApplicationEvent> {

        private val refreshedEvent = mutableListOf<ContextRefreshedEvent>()
        private val closedEvent = mutableListOf<ContextClosedEvent>()

        override fun  onApplicationEvent(event: ApplicationEvent) {
            when(event) {
                is ContextClosedEvent -> closedEvent.add(event)
                is ContextRefreshedEvent -> refreshedEvent.add(event)
            }
        }

        fun getRefreshedEvent(): List<ContextRefreshedEvent> {
            return this.refreshedEvent
        }

        fun getClosedEvent(): List<ContextClosedEvent> {
            return this.closedEvent
        }

    }

}