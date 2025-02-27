package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationContext
import org.bohan.minispring.context.ApplicationEvent

/**
 * 应用上下文事件的抽象基类
 * 所有与应用上下文相关的事件都应该继承此类
 *
 * @author Bohan
 */
abstract class ContextEvent(
    source: ApplicationContext
): ApplicationEvent(source) {

    /**
     * 获取产生事件的应用上下文
     *
     * @return 应用上下文
     */
    final fun getApplicationContext(): ApplicationContext {
        return getSource() as ApplicationContext
    }

}