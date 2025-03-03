package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.Advice
import org.bohan.minispring.aop.MethodInterceptor

/**
 * 通知适配器接口
 * 用于将不同类型的通知转换为MethodInterceptor
 *
 * @author Bohan
 */
interface AdvisorAdapter {

    /**
     * 判断是否支持给定的通知
     *
     * @param advice 通知
     * @return 是否支持
     */
    fun supportsAdvice(advice: Advice): Boolean

    /**
     * 将通知转换为方法拦截器
     *
     * @param advice 通知
     * @return 方法拦截器
     */
    fun getInterceptor(advice: Advice): MethodInterceptor

}