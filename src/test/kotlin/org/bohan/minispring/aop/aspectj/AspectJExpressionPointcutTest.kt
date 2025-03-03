package org.bohan.minispring.aop.aspectj

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class AspectJExpressionPointcutTest {

    @Test
    fun testExecutionExpression() {
        val pointcut = AspectJExpressionPointcut()
        pointcut.setExpression("execution(* org.bohan.minispring.aop.aspectj.AspectJExpressionPointcutTest.*(..))")

        assertTrue(pointcut.matches(AspectJExpressionPointcutTest::class.java))
        assertTrue(pointcut.matches(
            AspectJExpressionPointcutTest::class.java.getDeclaredMethod("testExecutionExpression"),
            AspectJExpressionPointcutTest::class.java
        ))
    }

    @Test
    @Throws(Exception::class)
    fun testMethodMatchWithArgs() {
        val pointcut = AspectJExpressionPointcut()
        pointcut.setExpression("execution(* org.bohan.minispring.aop.aspectj.AspectJExpressionPointcutTest.testMethodMatch**(..))")
        assertTrue(pointcut.matches(
            AspectJExpressionPointcutTest::class.java.getDeclaredMethod("testMethodMatchWithArgs"),
            AspectJExpressionPointcutTest::class.java
        ))

        assertFalse(pointcut.matches(
            AspectJExpressionPointcutTest::class.java.getDeclaredMethod("testExecutionExpression"),
            AspectJExpressionPointcutTest::class.java
        ))
    }

}