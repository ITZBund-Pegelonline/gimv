package org.eesgmbh.gimv.samples.jfreechart.shared;

import org.eesgmbh.gimv.shared.util.Bounds;

public class CommonSettings {
    public static final int MAX_RANGE_IN_DAYS = 10;

    public static final Bounds MAX_BOUNDS = new Bounds(
            System.currentTimeMillis() - MAX_RANGE_IN_DAYS * 24 * 60 * 60 * 1000,
            System.currentTimeMillis(),
            null, null);

    public static final Bounds INITIAL_BOUNDS = new Bounds(
            System.currentTimeMillis() - (MAX_RANGE_IN_DAYS - 2) * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
            800L, 300L);

    public static final Bounds INITIAL_OVERVIEW_BOUNDS = MAX_BOUNDS
            .setTop(800L)
            .setBottom(300L);
}
