package org.bohan.minispring.context

import org.bohan.minispring.context.support.ResourceBundleMessageResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.*

class ResourceBundleMessageSourceTest {

    private lateinit var messageResource: ResourceBundleMessageResource

    @BeforeEach
    fun setup() {
        messageResource = ResourceBundleMessageResource()
        messageResource.setBasename("messages")
    }

    @Test
    fun testGetMessageWithDefaultLocale() {
        val message = messageResource.getMessage("greeting", null, Locale.getDefault())
        assertNotNull(message)
        assertEquals("Hello", message)
    }

    @Test
    fun testGetMessageWithChineseLocale() {
        val message = messageResource.getMessage("greeting", null, Locale.CHINESE)
        assertNotNull(message)
        assertEquals("你好", message)
    }

    @Test
    fun testGetMessageWithDefaultMessage() {
        val message = messageResource.getMessage("non-exist",
            null,
            "default message",
            Locale.getDefault())

        assertEquals("default message", message)
    }

    @Test
    fun testMessageNotFound() {
        assertThrows<NoSuchMessageException> {
            messageResource.getMessage("non-exist", null, Locale.getDefault())
        }
    }

    @Test
    fun testMessageSourceResolvable() {
        val resolvable = object: MessageSourceResolvable {
            override fun getCodes(): Array<String> {
                return arrayOf("greeting")
            }

            override fun getArguments(): Array<Any>? {
                return null
            }

            override fun getDefaultMessage(): String {
                return "default"
            }
        }

        val message = messageResource.getMessage(resolvable, Locale.getDefault())
        assertNotNull(message)
        assertEquals("Hello", message)
    }

}