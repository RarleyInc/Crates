/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.impl;

import com.google.gson.Gson;
import com.rarley.crates.database.services.CratesUserService;
import com.rarley.crates.model.UserCrate;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@RequiredArgsConstructor
public class UserServiceImpl implements CratesUserService<UserCrate> {

    private final String table;

    private final HikariDataSource dataSource;
    private final Gson gson;

    @Override
    public UserCrate load(String name) {
        UserCrate user = new UserCrate(name);

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = loadStatement(user.getName(), con);
             ResultSet result = ps.executeQuery()) {

            if (result.next())
                user = gson.fromJson(result.getString("serialize"), UserCrate.class);
            else
                update(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void update(UserCrate user) {
        if (user == null) return;

        final String serialize = gson.toJson(user);

        final String query = String.format("INSERT INTO %s " +
                "(player,serialize)" +
                " VALUES (?,?)" +
                " ON DUPLICATE KEY UPDATE serialize=VALUES(serialize)", table);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getName());
            ps.setString(2, serialize);

            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private PreparedStatement loadStatement(String player, Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT * FROM " + table + " WHERE player='" + player + "'");
    }
}

