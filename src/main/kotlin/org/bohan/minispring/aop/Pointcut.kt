package org.bohan.minispring.aop

/**
 * 切点接口
 * 定义切点的行为,包括类过滤和方法匹配
 *
 * @author Bohan
 */
interface Pointcut {

    /**
     * 获取类过滤器
     * 用于判断目标类是否匹配切点表达式
     *
     * @return 类过滤器
     */
    fun getClassFilter(): ClassFilter

    /**
     * 获取方法匹配器
     * 用于判断目标方法是否匹配切点表达式
     *
     * @return 方法匹配器
     */
    fun getMethodMatcher(): MethodMatcher

}