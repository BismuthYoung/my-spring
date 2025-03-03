package org.bohan.minispring.aop

/**
 * 类过滤器接口
 * 用于判断目标类是否匹配切点表达式
 *
 * @author Bohan
 */
interface ClassFilter {

    /**
     * 判断目标类是否匹配切点表达式
     *
     * @param targetClass 目标类
     * @return 是否匹配
     */
    fun matches(targetClass: Class<*>): Boolean

}