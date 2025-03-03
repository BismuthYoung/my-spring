package org.bohan.minispring.aop

import java.lang.reflect.Method

/**
 * 方法调用接口
 * 封装方法调用的相关信息
 *
 * @author Bohan
 */
interface MethodInvocation {

    /**
     * 获取方法
     */
    fun getMethod(): Method

    /**
     * 获取目标对象
     */
    fun getThis(): Any

    /**
     * 获取方法参数
     */
    fun getArguments(): Array<Any>

    /**
     * 执行方法调用
     *
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Throws(Throwable::class)
    fun proceed(): Any?

}