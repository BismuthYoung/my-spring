package org.bohan.minispring.aop

import java.lang.reflect.Method

/**
 * 方法返回后通知接口
 * 在目标方法正常返回后执行的通知
 *
 * @author Bohan
 */
interface AfterReturningAdvice: AfterAdvice {

    /**
     * 在目标方法正常返回后执行的操作
     *
     * @param returnValue 返回值
     * @param method 目标方法
     * @param args 方法参数
     * @param target 目标对象
     * @throws Throwable 执行异常
     */
    fun afterReturning(returnValue: Any?, method: Method, args: Array<Any>, target: Any)

}