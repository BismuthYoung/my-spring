package org.bohan.minispring.aop

/**
 * AOP配置管理类
 * 存储AOP代理的配置信息,包括目标对象、拦截器等
 *
 * @author Bohan
 */
class AdvisedSupport {

    /** 是否使用CGLIB代理 */
    var isProxyTargetClass: Boolean = false

    /** 目标对象 */
    var targetSource: TargetSource? = null

    /** 方法拦截器列表 */
    val interceptors = mutableListOf<MethodInterceptor>()

    /** 方法匹配器(检查目标方法是否符合通知条件) */
    var methodMatcher: MethodMatcher? = null

    fun addMethodInterceptor(methodInterceptor: MethodInterceptor){
        this.interceptors.add(methodInterceptor)
    }
}