package org.bohan.minispring.aop.aspectj

import org.aspectj.weaver.tools.PointcutExpression
import org.aspectj.weaver.tools.PointcutParser
import org.aspectj.weaver.tools.PointcutPrimitive
import org.bohan.minispring.aop.ClassFilter
import org.bohan.minispring.aop.ExpressionPointcut
import org.bohan.minispring.aop.MethodMatcher
import java.lang.IllegalStateException
import java.lang.reflect.Method

class AspectJExpressionPointcut(
    private val pointcutParser: PointcutParser =
        PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
            SUPPORTED_PRIMITIVES,
            AspectJExpressionPointcut::class.java.classLoader
        )
): ExpressionPointcut, ClassFilter, MethodMatcher {

    private var expression: String? = null
    private lateinit var pointcutExpression: PointcutExpression

    companion object {
        private val SUPPORTED_PRIMITIVES = mutableSetOf<PointcutPrimitive>()
    }

    init {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION)
    }

    override fun matches(targetClass: Class<*>): Boolean {
        checkReadyToMatch()
        return pointcutExpression.couldMatchJoinPointsInType(targetClass)
    }

    override fun getExpression(): String? {
        return this.expression
    }

    override fun setExpression(expression: String) {
        this.expression = expression
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression)
    }

    override fun getClassFilter(): ClassFilter {
        return this
    }

    override fun getMethodMatcher(): MethodMatcher {
        return this
    }

    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        checkReadyToMatch()
        val shadowMatch = pointcutExpression.matchesMethodExecution(method)

        return shadowMatch.alwaysMatches()
    }

    private fun checkReadyToMatch() {
        if (getExpression() == null) {
            throw IllegalStateException("Must set property 'expression' before attempting to match")
        }
        if (pointcutExpression == null) {
            pointcutExpression = pointcutParser.parsePointcutExpression(expression)
        }
    }
}