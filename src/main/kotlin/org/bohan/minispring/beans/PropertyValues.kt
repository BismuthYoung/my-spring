package org.bohan.minispring.beans

import org.bohan.minispring.beans.factory.config.PropertyValue

/**
 * 属性值集合
 */
data class PropertyValues(
    private val propertyValueList: MutableList<PropertyValue> = mutableListOf()
) {
    fun addPropertyValue(propertyValue: PropertyValue) {
        this.propertyValueList.add(propertyValue)
    }

    fun getPropertyValues(): Array<PropertyValue> {
        return this.propertyValueList.toTypedArray()
    }

    fun getPropertyValue(propertyValueName: String): PropertyValue? {
        return propertyValueList.find { it.name == propertyValueName }
    }
}
