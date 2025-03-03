package org.bohan.minispring.aop

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class CglibMethodInvocation(
    target: Any,
    method: Method,
    arguments: Array<Any>,
    private val methodProxy: MethodProxy
) : ReflectiveMethodInvocation(target, method, arguments) {

    @Throws(Throwable::class)
    override fun proceed(): Any {
        return methodProxy.invoke(getThis(), getArguments())
    }
}
