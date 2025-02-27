package org.bohan.minispring.context.event

import org.bohan.minispring.context.ApplicationContext

class ContextClosedEvent(
    source: ApplicationContext
): ContextEvent(source) {
}