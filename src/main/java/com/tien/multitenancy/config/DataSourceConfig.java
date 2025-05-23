package com.tien.multitenancy.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource defaultDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/central_db"); // default schema
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource dataSource(DataSource defaultDataSource) {
        return new SchemaRoutingDataSource(defaultDataSource);
    }

    @Bean
    public JdbcTemplate tenantAwareJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(new SchemaRoutingDataSource(dataSource));
    }
}