package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationContext

class ContextRefreshedEvent(
    source: ApplicationContext
): ContextEvent(source) {
}