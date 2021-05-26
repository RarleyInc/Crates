/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserCrate {

    @Getter
    private final String name;

    private long lastCrateOpen, firstCrateDay;
    private int crates;

    public void applyCooldown(int delay) {
        if (firstCrateDay == 0L)
            firstCrateDay = (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));

        this.lastCrateOpen = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay));
        this.crates++;
    }

    public boolean hasCooldown() {
        return System.currentTimeMillis() <= lastCrateOpen;
    }

    public long getCooldown() {
        return TimeUnit.MILLISECONDS.toSeconds(lastCrateOpen - System.currentTimeMillis());
    }

    public boolean maxCratesToday(int max) {
        if (firstCrateDay < System.currentTimeMillis())
            resetDayDelay();

        return (firstCrateDay >= System.currentTimeMillis() && crates >= max);
    }

    private void resetDayDelay() {
        this.crates = 0;
        this.firstCrateDay = 0L;
    }

}
