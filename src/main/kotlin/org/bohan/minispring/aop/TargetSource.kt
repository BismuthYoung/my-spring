package org.bohan.minispring.aop

/**
 * 目标对象源
 * 封装目标对象的类型和实例
 *
 * @author Bohan
 */
data class TargetSource(
    private val target: Any
) {
    /**
     * 获取目标对象的类型
     *
     * @return 目标对象的类型
     */
    fun getTargetClass(): Class<*> {
        return this.target.javaClass
    }

    /**
     * 获取目标对象
     *
     * @return 目标对象
     */
    fun getTarget(): Any {
        return this.target
    }
}