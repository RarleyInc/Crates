/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.loader.plugin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PluginInfo {

    String name() default "undefined";

    String description() default "undefined";

    boolean useConfig() default false;

    boolean useInitializer() default true;

}
