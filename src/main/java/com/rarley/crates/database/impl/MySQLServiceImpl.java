/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.impl;

import com.google.gson.GsonBuilder;
import com.rarley.crates.database.configuration.DatabaseConfiguration;
import com.rarley.crates.database.exception.DatabaseException;
import com.rarley.crates.database.services.CratesDatabaseService;
import com.rarley.crates.database.services.CratesUserService;
import com.rarley.crates.model.UserCrate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class MySQLServiceImpl implements CratesDatabaseService {

    private final HikariDataSource dataSource;

    private final CratesUserService<UserCrate> userService;

    private final String table;

    public MySQLServiceImpl(String host, String user, String pass, String db, String table) {
        if (db.equals("@"))
            throw new DatabaseException(DatabaseException.DatabaseStatus.DEFAULT_CONFIG);

        final HikariConfig config = new HikariConfig();

        config.setUsername(user);
        config.setPassword(pass);

        config.setJdbcUrl(String.format("jdbc:mysql://%s:3306/%s?autoReconnect=true", host, db));
        config.setPoolName("Crates-SQL");
        config.setConnectionTestQuery("SELECT 1");

        this.table = table;
        this.dataSource = connect(config);

        createTable();

        this.userService = (this.dataSource != null)
                ? new UserServiceImpl(table, dataSource, new GsonBuilder().create())
                : null;
    }

    @Override
    public HikariDataSource connect(HikariConfig configuration) {
        try {
            return new DatabaseConfiguration(configuration)
                    .addDefaultSafeProperty()
                    .connect(15); // pool size = Tn x (Cm - 1) + 1
        } catch (SQLException ignored) {
            throw new DatabaseException(DatabaseException.DatabaseStatus.CONNECT);
        }
    }

    @Override
    public void createTable() {
        final String query = "CREATE TABLE IF NOT EXISTS " + table
                + "(player VARCHAR(20), "
                + "serialize TEXT NOT NULL, "
                + "PRIMARY KEY(player))";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.execute();
        } catch (SQLException exception) {
            throw new DatabaseException(DatabaseException.DatabaseStatus.CREATE_TABLE);
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null)
            dataSource.close();
    }
}
