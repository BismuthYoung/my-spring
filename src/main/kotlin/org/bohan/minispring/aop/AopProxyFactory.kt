package org.bohan.minispring.aop

/**
 * AOP代理工厂接口
 * 负责创建AOP代理对象
 *
 * @author Bohan
 */
interface AopProxyFactory {

    /**
     * 创建AOP代理
     *
     * @param config AOP配置
     * @return AOP代理
     */
    fun createAopProxy(): AopProxy

}