package org.bohan.minispring.beans.converter

import org.bohan.minispring.beans.BeansException

/**
 * 类型不匹配异常，当无法进行类型转换时抛出
 */
class TypeMismatchException(
    value: Any,
    requiredType: Class<*>
): BeansException("Failed to convert value '$value' to type '$requiredType'") {

    constructor(value: Any, requiredType: Class<*>, cause: Throwable): this(value, requiredType) {
        initCause(cause)
    }

}