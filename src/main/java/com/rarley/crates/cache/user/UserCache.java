/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.rarley.crates.CratesPlugin;
import com.rarley.crates.model.UserCrate;
import lombok.NonNull;
import lombok.SneakyThrows;

public class UserCache {

    private final UserCacheLoader userCacheLoader;
    private final LoadingCache<String, UserCrate> cache;

    public UserCache(CratesPlugin instance, UserCacheLoader cacheLoader) {
        this.userCacheLoader = cacheLoader;

        final String spec = "expireAfterWrite=24h";

        this.cache = CacheBuilder.from(spec)
                .removalListener(new UserCacheRemovalListener(instance.getDatabase().getUserService()))
                .build(cacheLoader);
    }

    public void shutdown() {
        cache.cleanUp();
        cache.invalidateAll();

        userCacheLoader.getExecutor().shutdown();
    }

    @SneakyThrows
    public UserCrate getAndPut(@NonNull String user) {
        return cache.getUnchecked(user);
    }

    public void forceRemove(@NonNull String name) {
        cache.invalidate(name);
    }

}
