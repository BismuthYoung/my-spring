package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

open class SimpleAliasRegistry: AliasRegistry {

    private val logger = LoggerFactory.getLogger(SimpleAliasRegistry::class.java)
    private val aliasMap = ConcurrentHashMap<String, String>(16)

    override fun registerAlias(name: String, alias: String) {
        if (name == alias) {
            removeAlias(alias)
            return
        }
        // 检查循环引用
        if (hasAliasCycle(name, alias)) {
            throw BeansException("Circular reference between alias '$alias' and name '$name'")
        }
        // 检查该名称是否已注册
        val registerName = aliasMap[alias]
        if (registerName != null) {
            if (registerName == name) {
                return
            } else {
                throw BeansException(
                    "Cannot register alias '" + alias + "' for name '" +
                            name + "': It is already registered for name '" + registerName + "'"
                )
            }
        }

        aliasMap[alias] = name
        logger.debug("Registered alias '{}' for name '{}'", alias, name)

    }

    override fun removeAlias(alias: String) {
        val name = aliasMap.remove(alias)
        if (name != null) {
            logger.debug("Removed alias '{}' for name '{}'", alias, name)
        }
    }

    override fun isAlias(alias: String): Boolean {
        return aliasMap.containsKey(alias)
    }

    override fun getAlias(name: String): Array<String> {
        TODO("Not yet implemented")
    }

    /**
     * 检查是否存在别名循环引用
     *
     * @param name 要注册的bean名称
     * @param alias 要注册的别名
     * @return 如果存在循环引用返回true，否则返回false
     */
    protected fun hasAliasCycle(name: String, alias: String): Boolean {
        var registeredName = aliasMap[name]
        while (registeredName != null) {
            if (registeredName.equals(alias)) {
                return true
            }
            registeredName = aliasMap[registeredName]
        }

        return false
    }

     fun canonicalName(name: String): String {
        var canonicalName = name
        var resolvedName: String?

        // 循环解析别名，直到找到最终的bean名称
        do {
            resolvedName = aliasMap[name]
            if (resolvedName != null) {
                canonicalName = resolvedName
            }
        } while (resolvedName != null)

        return canonicalName
    }

}