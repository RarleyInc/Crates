/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.user;

import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.rarley.crates.database.services.CratesUserService;
import com.rarley.crates.model.UserCrate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.util.concurrent.Futures.immediateFuture;


@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class UserCacheLoader extends CacheLoader<String, UserCrate> {

    private final CratesUserService<UserCrate> database;

    @Getter
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    @SneakyThrows
    public UserCrate load(@NonNull String key) {
        final ListenableFuture<UserCrate> user = immediateFuture(database.load(key));

        return user.get();
    }
}
