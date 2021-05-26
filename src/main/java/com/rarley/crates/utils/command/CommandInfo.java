/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CommandInfo {

    String name() default "undefined";

}
