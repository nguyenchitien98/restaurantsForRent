package com.tien.multitenancy.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaRoutingDataSource extends AbstractDataSource {
    private final DataSource defaultDataSource;
    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();

    public SchemaRoutingDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    private DataSource determineTargetDataSource() {
        String tenant = TenantContext.getTenant();
        if (tenant == null) return defaultDataSource;

        return tenantDataSources.computeIfAbsent(tenant, this::createDataSourceForTenant);
    }

    private DataSource createDataSourceForTenant(String tenantId) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/restaurant_" + tenantId); // chỉnh đúng format schema
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return new HikariDataSource(config);
    }
}
