package com.example.inittwodatasources.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Configuration for first database
 */
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "firstEntityManagerFactory",
        transactionManagerRef = "firstTransactionManager",
        basePackages = ["com.example.inittwodatasources.first.repo"]
)
class FirstDbConfig {

    @Resource
    private lateinit var jpaProperties: JpaProperties

    @Resource
    private lateinit var hibernateProperties: HibernateProperties

    /**
     * Primary datasource, first DB
     */
    @Bean
    @Primary
    fun dataSource(): DataSource {
        return firstDatasourceProperties().initializeDataSourceBuilder().build()
    }

    /**
     * Primary hibernate properties for first datasource
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.jpa.hibernate")
    fun hibernateProperties(): HibernateProperties {
        return HibernateProperties()
    }

    /**
     * Primary jpa properties, for first dataousrce
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.jpa")
    fun jpaProperties(): JpaProperties {
        return JpaProperties()
    }

    /**
     * Primary datasource configuratin
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    fun firstDatasourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    /**
     * Primary entity manager factory for first database
     */
    @Bean
    @Primary
    fun firstEntityManagerFactory(@Qualifier("dataSource") dataSource: DataSource,
                                  entityManagerFactoryBuilder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {
        val vendorProperties = getVendorProperties(this.hibernateProperties, this.jpaProperties)
        vendorProperties.putIfAbsent("javax.persistence.nonJtaDataSource", dataSource) //FIXME this should not be necessary
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("com.example.inittwodatasources.first.entity")
                .properties(vendorProperties)
                .persistenceUnit("first")
                .build()
    }

    /**
     * Primary transaction manager to access first db
     */
    @Bean
    @Primary
    fun firstTransactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
