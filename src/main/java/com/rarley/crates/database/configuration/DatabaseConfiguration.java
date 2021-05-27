/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;


@RequiredArgsConstructor
public class DatabaseConfiguration {

    private final HikariConfig config;

    public DatabaseConfiguration addDefaultSafeProperty() {
        config.setMaxLifetime(259200000L); // unsafe
        config.addDataSourceProperty("useSSL", "false"); // mysql localhost
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("jdbcCompliantTruncation", "false");
        config.addDataSourceProperty("cachePrepStmts", "true"); // safe
        config.addDataSourceProperty("prepStmtCacheSize", "275"); // safe
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // safe
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("encoding", "UTF-8");

        return this;
    }

    public HikariDataSource connect(int size) throws SQLException {
        config.setMaximumPoolSize(size);

        return new HikariDataSource(config);
    }

}
