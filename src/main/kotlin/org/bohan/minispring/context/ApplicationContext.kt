package org.bohan.minispring.context

import org.bohan.minispring.beans.factory.ListableBeanFactory
import org.bohan.minispring.core.io.ResourceLoader

/**
 * 应用上下文的中央接口
 * 扩展了ListableBeanFactory，提供了更多的应用层特性
 *
 * @author Bohan
 */
interface ApplicationContext: ListableBeanFactory, ResourceLoader {

    /**
     * 获取应用上下文的唯一ID
     *
     * @return 应用上下文ID
     */
    fun getId(): String

    /**
     * 获取应用上下文的显示名称
     *
     * @return 显示名称
     */
    fun getDisplayName(): String

    /**
     * 获取应用上下文的启动时间
     *
     * @return 启动时间戳
     */
    fun getStartUpDate(): Long

    /**
     * 获取父级上下文
     *
     * @return 父级上下文，如果没有则返回null
     */
    fun getParent(): ApplicationContext?

}