/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.services;

import com.rarley.crates.model.UserCrate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public interface CratesDatabaseService {

    HikariDataSource connect(HikariConfig configuration) throws SQLException;

    void createTable();

    void disconnect();

    CratesUserService<UserCrate> getUserService();
}
