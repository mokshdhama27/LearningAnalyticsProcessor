/**
 *
 */
package org.apereo.lap.services.storage.h2;

import javax.sql.DataSource;

import org.apereo.lap.services.storage.DatasourceProperties;
import org.apereo.lap.services.storage.h2.model.RiskConfidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ggilbert
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "persistentEntityManagerFactory",
        transactionManagerRef = "persistentTransactionManager",
        basePackages = "org.apereo.lap.services.storage.h2.model")
public class H2PersistentConfig {
    @Autowired
    private DatasourceProperties datasourceProperties;

    @Bean
    public DataSource persistentDataSource() {
        DatasourceProperties.JdbcInfo p = datasourceProperties.getPersistent();
        return DataSourceBuilder
                .create()
                .driverClassName(p.getDriverClassName())
                .password(p.getPassword())
                .url(p.getUrl())
                .username(p.getUsername())
                .build();
    }

    @Bean
    PlatformTransactionManager persistentTransactionManager() {
        return new JpaTransactionManager(persistentEntityManagerFactory().getObject());
    }

    @Bean
    LocalContainerEntityManagerFactoryBean persistentEntityManagerFactory() {

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(persistentDataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPackagesToScan(RiskConfidence.class.getPackage().getName());

        return factoryBean;
    }
}
