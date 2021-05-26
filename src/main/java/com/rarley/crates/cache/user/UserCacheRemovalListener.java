/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.user;

import com.google.common.cache.RemovalNotification;
import com.rarley.crates.database.services.CratesUserService;
import com.rarley.crates.model.UserCrate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCacheRemovalListener implements com.google.common.cache.RemovalListener<String, UserCrate> {

    private final CratesUserService<UserCrate> userService;

    @Override
    public void onRemoval(@NonNull RemovalNotification notification) {
        if (notification.getValue() instanceof UserCrate)
            userService.update((UserCrate) notification.getValue());
    }
}
