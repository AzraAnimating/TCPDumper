/*
 * Copyright (c) 2021. Zyonic Software - Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Zyonic Software and may not be used without explicit permission to do so.
 *
 * contact(at)zyonicsoftware.com
 */

/*
 * Zyonic Software - 2020 - Tobias Rempe
 * This File, its contents and by extention the corresponding project may be used freely in compliance with the Apache 2.0 License.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.tcpdumper;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadHandler {

    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void startExecute(final Runnable runnable) {
        ThreadHandler.getExecutor().execute(runnable);
    }

    public static void removeExecute(final Runnable runnable) {
        ThreadHandler.getExecutor().remove(runnable);
    }


    public static ThreadPoolExecutor getExecutor() {
        return ThreadHandler.executor;
    }
}

