package org.bohan.minispring.aop

/**
 * 基于表达式的切点接口
 * 用于获取和设置切点表达式
 *
 * @author Bohan
 */
interface ExpressionPointcut: Pointcut {

    /**
     * 获取切点表达式
     *
     * @return 切点表达式
     */
    fun getExpression(): String?

    /**
     * 设置切点表达式
     *
     * @param expression 切点表达式
     */
    fun setExpression(expression: String)

}