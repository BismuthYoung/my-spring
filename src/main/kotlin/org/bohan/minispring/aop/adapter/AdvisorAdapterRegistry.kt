package org.bohan.minispring.aop.adapter

import org.bohan.minispring.aop.Advice
import org.bohan.minispring.aop.MethodInterceptor

/**
 * 通知适配器注册表接口
 * 管理所有的通知适配器
 *
 * @author Bohan
 */
interface AdvisorAdapterRegistry {

    /**
     * 注册通知适配器
     *
     * @param adapter 通知适配器
     */
    fun registerAdvisorAdapter(adapter: AdvisorAdapter)

    /**
     * 将通知转换为方法拦截器
     *
     * @param advice 通知
     * @return 方法拦截器
     */
    fun getInterceptors(advice: Advice): Array<MethodInterceptor>

    /**
     * 包装通知为方法拦截器
     *
     * @param advice 通知
     * @return 方法拦截器
     */
    fun wrap(advice: Advice): MethodInterceptor

}