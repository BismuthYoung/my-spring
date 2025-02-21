package org.bohan.minispring.beans.factory.support

/**
 * 别名注册表接口，提供别名的注册和解析功能
 */
interface AliasRegistry {

    /**
     * 注册一个别名
     *
     * @param name bean的名称
     * @param alias 要注册的别名
     */
    fun registerAlias(name: String, alias: String)

    /**
     * 移除一个别名
     *
     * @param alias 要移除的别名
     */
    fun removeAlias(alias: String)

    /**
     * 判断指定的名称是否是别名
     *
     * @param name 要检查的名称
     * @return 如果是别名返回true，否则返回false
     */
    fun isAlias(alias: String): Boolean

    /**
     * 获取指定名称的所有别名
     *
     * @param name bean的名称
     * @return 别名数组
     */
    fun getAliases(name: String): Array<String>

}