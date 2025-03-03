package org.bohan.minispring.aop

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class CglibMethodInvocation(
    target: Any,
    method: Method,
    arguments: Array<Any>,
    private val methodProxy: MethodProxy,
    interceptors: List<MethodInterceptor>
) : ReflectiveMethodInvocation(target, method, arguments, interceptors) {

    @Throws(Throwable::class)
    override fun proceed(): Any {
        return methodProxy.invoke(getThis(), getArguments())
    }
}
