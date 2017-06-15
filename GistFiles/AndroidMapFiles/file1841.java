package com.genyware.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TManager {

    private static TManager instance;
    ExecutorService pool;

    private TManager() {
        pool = Executors.newCachedThreadPool();
    }

    public static TManager getInstance() {
        if (instance == null) instance = new TManager();
        return instance;
    }

    public void exec(Runnable r) {
        pool.execute(r);
    }

    public void end() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        instance = null;
    }
}
