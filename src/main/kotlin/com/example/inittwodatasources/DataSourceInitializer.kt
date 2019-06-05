package com.example.inittwodatasources

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent
import org.springframework.boot.jdbc.DataSourceInitializationMode
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.context.ApplicationListener
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils
import javax.sql.DataSource

/**
 * Datasource initializer which uses [DataSourceSchemaCreatedEvent] to start initialization
 */
class DataSourceInitializer(
        private val dataSource: DataSource,
        private val databasePopulator: DatabasePopulator,
        private val dataSourceProperties: DataSourceProperties) : ApplicationListener<DataSourceSchemaCreatedEvent> {

    private var initialized = false

    /**
     * Executes initialization on [DataSourceSchemaCreatedEvent]
     */
    override fun onApplicationEvent(event: DataSourceSchemaCreatedEvent) {
        if (event.source === dataSource) {
            if (!initialized && isEnabled()) {
                DatabasePopulatorUtils.execute(databasePopulator, this.dataSource)
                initialized = true
            }
        }
    }

    private fun isEnabled(): Boolean {
        val mode = this.dataSourceProperties.initializationMode
        return if (mode == DataSourceInitializationMode.NEVER) {
            false
        } else {
            mode != DataSourceInitializationMode.EMBEDDED || this.isEmbedded()
        }
    }

    private fun isEmbedded(): Boolean {
        return try {
            EmbeddedDatabaseConnection.isEmbedded(this.dataSource)
        } catch (e: Exception) {
            false
        }

    }
}
