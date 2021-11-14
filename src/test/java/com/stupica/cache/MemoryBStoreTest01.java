package com.stupica.cache;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MemoryBStoreTest01 {

    private final int numberOfThreads = 13;

    private Random objRand = null;
    private final int upperbound = 450;

    private MemoryBStore    objCache;


    @BeforeEach
    public void setUp() throws Exception {
        objRand = new Random(); //instance of random class
        objCache = new MemoryBStore();
    }

    @Test
    public void addThread11() {
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Initialization
        System.out.println("--");
        System.out.println("Test: addThread11() - " + this.getClass().getName());

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                long    iTS = 0L;

                // .. generate random values from 0-24
                int int_random = objRand.nextInt(upperbound);
                System.out.println("Test: Thread: " + Thread.currentThread().getName() + "; Random: " + int_random);
                try { // Pause for X MilliSecond(s)
                    Thread.sleep(int_random);
                } catch (Exception ex) {
                    System.err.println("test(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
                }
                iTS = System.currentTimeMillis();
                System.out.println("Test: Thread: " + Thread.currentThread().getName() + "; TS: " + Long.valueOf(iTS).toString() + " .. adding ..");
                objCache.add(Thread.currentThread().getName() + "-" + Long.valueOf(iTS).toString(), Integer.valueOf(1), 1000 * 1);
                try { // Pause for X MilliSecond(s)
                    Thread.sleep(100);
                } catch (Exception ex) {
                    System.err.println("test(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
                }
                if (int_random % 2 == 0) {
                    System.out.println("Test: Thread: " + Thread.currentThread().getName() + " .. sleep for 1 sec ..");
                    try { // Pause for X MilliSecond(s)
                        Thread.sleep(1101);
                    } catch (Exception ex) {
                        System.err.println("test(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
                    }
                }
                System.out.println(".. size: " + objCache.size()
                    + "\n\tCache: " + objCache.toString());
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("ERROR: Msg.: " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("==\nTS: " + Long.valueOf(System.currentTimeMillis()).toString() + ".. after add > size: " + objCache.size());
        System.out.println("==\nTS: " + Long.valueOf(System.currentTimeMillis()).toString() + ".. after add > size: " + objCache.size()
                + "\n\tCache: " + objCache.toString());
        //assertEquals(0, objCache.size());
    }
}
