package com.example.inittwodatasources.config

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties

/**
 * Gets merged Hibernate properties (from [org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration.getVendorProperties])
 */
fun getVendorProperties(hibernateProperties: HibernateProperties, jpaProperties: JpaProperties): MutableMap<String, Any> {
    val defaultDllAuto = HibernateSettings().ddlAuto { "none" }
    return hibernateProperties.determineHibernateProperties(jpaProperties.properties, defaultDllAuto)
}
