package org.example.jobsearchplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadSafeCounterServiceTest {

    private ThreadSafeCounterService threadSafeCounterService;

    @BeforeEach
    void setUp() {
        threadSafeCounterService = new ThreadSafeCounterService();
    }

    @Test
    void incrementAndGet_returnsIncrementedValue() {
        assertEquals(1L, threadSafeCounterService.incrementAndGet());
        assertEquals(2L, threadSafeCounterService.incrementAndGet());
    }

    @Test
    void reset_setsCounterToZero() {
        threadSafeCounterService.incrementAndGet();
        threadSafeCounterService.incrementAndGet();

        long valueAfterReset = threadSafeCounterService.reset();

        assertEquals(0L, valueAfterReset);
        assertEquals(0L, threadSafeCounterService.getValue());
    }

    @Test
    void incrementAndGet_isThreadSafeUnderConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        int incrementsPerThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        threadSafeCounterService.incrementAndGet();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        assertTrue(readyLatch.await(5, TimeUnit.SECONDS));
        startLatch.countDown();
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS));
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals((long) threadCount * incrementsPerThread, threadSafeCounterService.getValue());
    }
}
