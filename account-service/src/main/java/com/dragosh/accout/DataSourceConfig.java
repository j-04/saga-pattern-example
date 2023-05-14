package com.dragosh.accout;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.liquibase.change-log}")
    private String liquibaseChangeLog;

    @Value("${spring.liquibase.default-schema}")
    private String liquibaseDefaultSchema;

    @Value("${spring.datasource.connection-timeout}")
    private long connectionTimeout;

    @Value("${spring.datasource.initFailTimeout}")
    private long initFailTimeout;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setConnectionTimeout(connectionTimeout);
        config.setInitializationFailTimeout(initFailTimeout);
        return new HikariDataSource(config);
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseChangeLog);
        liquibase.setDefaultSchema(liquibaseDefaultSchema);
        return liquibase;
    }
}

