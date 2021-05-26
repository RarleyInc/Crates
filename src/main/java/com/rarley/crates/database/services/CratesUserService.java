/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.services;

import com.rarley.crates.model.UserCrate;


public interface CratesUserService<U extends UserCrate> {

    U load(String name);

    void update(U user);

}
