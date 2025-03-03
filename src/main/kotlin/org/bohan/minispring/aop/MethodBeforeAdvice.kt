package org.bohan.minispring.aop

import java.lang.reflect.Method

/**
 * 方法前置通知接口
 * 在目标方法执行前执行的通知
 *
 * @author Bohan
 */
interface MethodBeforeAdvice: BeforeAdvice {

    /**
     * 在目标方法执行前执行的操作
     *
     * @param method 目标方法
     * @param args 方法参数
     * @param target 目标对象
     * @throws Throwable 执行异常
     */
    fun before(method: Method, args: Array<Any>, target: Any)

}