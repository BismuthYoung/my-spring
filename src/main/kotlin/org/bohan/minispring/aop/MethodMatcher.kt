package org.bohan.minispring.aop

import java.lang.reflect.Method

/**
 * 方法匹配器
 * 用于判断目标方法是否符合切点表达式
 *
 * @author Bohan
 */
interface MethodMatcher {

    /**
     * 判断目标方法是否符合切点表达式
     *
     * @param method 目标方法
     * @param targetClass 目标类
     * @return 是否匹配
     */
    fun matches(method: Method, targetClass: Class<*>): Boolean

}