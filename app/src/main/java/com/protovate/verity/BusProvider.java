package com.protovate.verity;

/**
 * Created by Yan on 6/13/15.
 */
public class BusProvider {
    private static final MainThreadBus BUS = new MainThreadBus();

    public static MainThreadBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
