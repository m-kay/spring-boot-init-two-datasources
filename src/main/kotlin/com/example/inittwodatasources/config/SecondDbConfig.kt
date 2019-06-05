package com.example.inittwodatasources.config

import com.example.inittwodatasources.DataSourceInitializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Configuration for the second database where legacy configurations are stored directly
 */
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "secondEntityManagerFactory",
        transactionManagerRef = "secondTransactionManager",
        basePackages = ["com.example.inittwodatasources.second.repo"]
)
class SecondDbConfig {

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    /**
     * Datasource for second-DB
     */
    @Bean
    fun secondDataSource(@Qualifier("secondDatasourceProperties") secondDatasourceProperties: DataSourceProperties): DataSource {
        return secondDatasourceProperties.initializeDataSourceBuilder().build()
    }

    /**
     * Hibernate properties for second db
     */
    @Bean
    @ConfigurationProperties("second.jpa.hibernate")
    fun secondHibernateProperties(): HibernateProperties {
        return HibernateProperties()
    }

    /**
     * JPA properties for second db
     */
    @Bean
    @ConfigurationProperties("second.jpa")
    fun secondJpaProperties(): JpaProperties {
        return JpaProperties()
    }


    /**
     * Datasource properties for second db
     */
    @Bean
    @ConfigurationProperties("second.datasource")
    fun secondDatasourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    /**
     * Entity manager factory for second db
     */
    @Bean
    fun secondEntityManagerFactory(
            entityManagerFactoryBuilder: EntityManagerFactoryBuilder,
            @Qualifier("secondDataSource") dataSource: DataSource,
            @Qualifier("secondJpaProperties") jpaProperties: JpaProperties,
            @Qualifier("secondHibernateProperties") hibernateProperties: HibernateProperties): LocalContainerEntityManagerFactoryBean {
        val vendorProperties = getVendorProperties(hibernateProperties, jpaProperties)
        vendorProperties.putIfAbsent("javax.persistence.nonJtaDataSource", dataSource) //FIXME this should not be necessary
        return entityManagerFactoryBuilder.dataSource(dataSource)
                .packages("com.example.inittwodatasources.second.entity")
                .persistenceUnit("second")
                .properties(vendorProperties)
                .build()
    }

    /**
     * transaction manager to access second db
     */
    @Bean
    fun secondTransactionManager(@Qualifier("secondEntityManagerFactory") entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }


    /**
     * Initializer to fill in-memory second db with data from scripts
     */
    @Bean
    fun secondDbInitializer(@Qualifier("secondDataSource") dataSource: DataSource,
                            @Qualifier("secondDatasourceProperties") secondDatasourceProperties: DataSourceProperties): DataSourceInitializer {
        val resourceDatabasePopulator = ResourceDatabasePopulator()
        resourceDatabasePopulator.addScripts(*getScripts(secondDatasourceProperties))
        return DataSourceInitializer(dataSource, resourceDatabasePopulator, secondDatasourceProperties)
    }

    private fun getScripts(secondDatasourceProperties: DataSourceProperties): Array<org.springframework.core.io.Resource> {
        val resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
        return secondDatasourceProperties.data
                .map { resourcePatternResolver.getResource(it) }
                .filter { it.exists() }
                .toTypedArray()
    }
}

