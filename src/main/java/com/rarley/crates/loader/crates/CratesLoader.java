/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.loader.crates;

import com.rarley.crates.cache.crate.CrateCache;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class CratesLoader {

    public CratesLoader(File[] crates, CrateCache cache) {
        if (crates.length == 0) return;

        final ExecutorService cachedThread = Executors.newCachedThreadPool();

        if (cachedThread.submit(() -> Stream.of(crates).forEach(cache::loadCrate)).isDone())
            cachedThread.shutdown();
    }

}
