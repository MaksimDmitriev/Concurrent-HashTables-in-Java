package ru.maksim.concurrentmaps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Main {

    public static final int OPERATION_COUNT = 10_000;
    public static final int THREAD_COUNT = 1000;
    private static final Random RANDOM = new Random();

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private Map<Integer, String> mConcurrentHashMap = new ConcurrentHashMap<>();
    private Map<Integer, String> mSyncHashMap = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, String> mHashtable = new Hashtable<>();

    public static void main(String[] args) {
        Main main = new Main();
        main.workWithSyncHashMap();
    }

    private void workWithHashtable() {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            int limit = THREAD_COUNT / 2;
            for (int i = 0; i < limit; i++) {
                mExecutorService.submit(new Reader(mHashtable));
                mExecutorService.submit(new Writer(mHashtable));
            }
            try {
                mExecutorService.shutdown();
                mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                System.out.print("workWithHashtable: " + (System.currentTimeMillis() - start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "workWithHashtable").start();
    }

    private void workWithConcurrentHashMap() {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            int limit = THREAD_COUNT / 2;
            for (int i = 0; i < limit; i++) {
                mExecutorService.submit(new Reader(mConcurrentHashMap));
                mExecutorService.submit(new Writer(mConcurrentHashMap));
            }
            try {
                mExecutorService.shutdown();
                mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                System.out.print("workWithConcurrentHashMap: " + (System.currentTimeMillis() - start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "workWithConcurrentHashMap").start();
    }

    private void workWithSyncHashMap() {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            int limit = THREAD_COUNT / 2;
            for (int i = 0; i < limit; i++) {
                mExecutorService.submit(new Reader(mSyncHashMap));
                mExecutorService.submit(new Writer(mSyncHashMap));
            }
            try {
                mExecutorService.shutdown();
                mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                System.out.print("workWithSyncHashMap: " + (System.currentTimeMillis() - start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "workWithSyncHashMap").start();
    }

    private static class Reader implements Runnable {

        private final Map<Integer, String> mMap;

        Reader(Map<Integer, String> map) {
            mMap = map;
        }

        @Override
        public void run() {
            int i = 0;
            Iterator<Integer> iterator = mMap.keySet().iterator();
            while (i < OPERATION_COUNT) {
                if (!iterator.hasNext()) {
                    break;
                }
                iterator.next(); // Do nothing
                i++;
            }
        }
    }

    private static class Writer implements Runnable {

        private final Map<Integer, String> mMap;

        Writer(Map<Integer, String> map) {
            mMap = map;
        }

        @Override
        public void run() {
            int start = RANDOM.nextInt(Integer.MAX_VALUE - OPERATION_COUNT);
            int limit = start + OPERATION_COUNT;
            for (int i = start; i < limit; i++) {
                mMap.put(i, Integer.toString(i));
            }
        }
    }
}
