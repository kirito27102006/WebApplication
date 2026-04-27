package org.example.jobsearchplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.CounterConcurrencyDemoResponse;
import org.example.jobsearchplatform.dto.RaceConditionDemoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RaceConditionDemoService {

    private static final int DEFAULT_THREAD_COUNT = 64;
    private static final int DEFAULT_INCREMENTS_PER_THREAD = 2_000;
    private static final int MAX_DEMO_ATTEMPTS = 3;
    private static final long AWAIT_TIMEOUT_SECONDS = 15;

    public RaceConditionDemoResponse runDefaultDemo() {
        return runDemo(DEFAULT_THREAD_COUNT, DEFAULT_INCREMENTS_PER_THREAD);
    }

    public CounterConcurrencyDemoResponse runUnsafeDemo() {
        return runUnsafeDemo(DEFAULT_THREAD_COUNT, DEFAULT_INCREMENTS_PER_THREAD);
    }

    public CounterConcurrencyDemoResponse runAtomicDemo() {
        return runAtomicDemo(DEFAULT_THREAD_COUNT, DEFAULT_INCREMENTS_PER_THREAD);
    }

    public CounterConcurrencyDemoResponse runSynchronizedDemo() {
        return runSynchronizedDemo(DEFAULT_THREAD_COUNT, DEFAULT_INCREMENTS_PER_THREAD);
    }

    public RaceConditionDemoResponse runDemo(int threadCount, int incrementsPerThread) {
        validateArguments(threadCount, incrementsPerThread);

        CounterRunResult unsafeResult = runCounterDemo(threadCount, incrementsPerThread, CounterMode.UNSAFE);
        CounterRunResult safeResult = runCounterDemo(threadCount, incrementsPerThread, CounterMode.SAFE_ATOMIC);
        long expectedValue = calculateExpectedValue(threadCount, incrementsPerThread);
        long unsafeLostUpdates = expectedValue - unsafeResult.actualValue();
        long safeLostUpdates = expectedValue - safeResult.actualValue();

        log.info(
                "Race condition demo finished: threads={}, incrementsPerThread={}, expected={}, unsafe={}, safe={}",
                threadCount,
                incrementsPerThread,
                expectedValue,
                unsafeResult.actualValue(),
                safeResult.actualValue()
        );

        return RaceConditionDemoResponse.builder()
                .threadCount(threadCount)
                .incrementsPerThread(incrementsPerThread)
                .expectedValue(expectedValue)
                .unsafeValue(unsafeResult.actualValue())
                .safeValue(safeResult.actualValue())
                .unsafeLostUpdates(unsafeLostUpdates)
                .safeLostUpdates(safeLostUpdates)
                .raceConditionDetected(unsafeLostUpdates > 0)
                .message(buildMessage(unsafeLostUpdates, safeLostUpdates))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CounterConcurrencyDemoResponse runUnsafeDemo(int threadCount, int incrementsPerThread) {
        validateArguments(threadCount, incrementsPerThread);
        CounterRunResult result = runCounterDemo(threadCount, incrementsPerThread, CounterMode.UNSAFE);
        long expectedValue = calculateExpectedValue(threadCount, incrementsPerThread);
        long lostUpdates = expectedValue - result.actualValue();

        return CounterConcurrencyDemoResponse.builder()
                .strategy("unsafe")
                .threadCount(threadCount)
                .incrementsPerThread(incrementsPerThread)
                .expectedValue(expectedValue)
                .actualValue(result.actualValue())
                .lostUpdates(lostUpdates)
                .threadSafe(false)
                .message(lostUpdates > 0
                        ? "Counter lost updates under concurrent access"
                        : "Race condition was not reproduced in this run")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CounterConcurrencyDemoResponse runAtomicDemo(int threadCount, int incrementsPerThread) {
        return runSingleStrategyDemo("atomic", CounterMode.SAFE_ATOMIC, true, threadCount, incrementsPerThread);
    }

    public CounterConcurrencyDemoResponse runSynchronizedDemo(int threadCount, int incrementsPerThread) {
        return runSingleStrategyDemo("synchronized", CounterMode.SAFE_SYNCHRONIZED, true, threadCount,
                incrementsPerThread);
    }

    private CounterConcurrencyDemoResponse runSingleStrategyDemo(
            String strategy,
            CounterMode mode,
            boolean threadSafe,
            int threadCount,
            int incrementsPerThread) {
        validateArguments(threadCount, incrementsPerThread);
        CounterRunResult result = runCounterDemo(threadCount, incrementsPerThread, mode);
        long expectedValue = calculateExpectedValue(threadCount, incrementsPerThread);
        long lostUpdates = expectedValue - result.actualValue();

        return CounterConcurrencyDemoResponse.builder()
                .strategy(strategy)
                .threadCount(threadCount)
                .incrementsPerThread(incrementsPerThread)
                .expectedValue(expectedValue)
                .actualValue(result.actualValue())
                .lostUpdates(lostUpdates)
                .threadSafe(threadSafe)
                .message(lostUpdates == 0
                        ? "Counter completed without lost updates"
                        : "Counter lost updates under concurrent access")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private CounterRunResult runCounterDemo(int threadCount, int incrementsPerThread, CounterMode mode) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        AtomicInteger atomicCounter = new AtomicInteger(0);
        SynchronizedCounter synchronizedCounter = new SynchronizedCounter();

        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                        for (int j = 0; j < incrementsPerThread; j++) {
                            incrementByMode(mode, unsafeCounter, atomicCounter, synchronizedCounter);
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            awaitLatch(readyLatch, "Workers were not ready in time");
            startLatch.countDown();
            awaitLatch(doneLatch, "Workers did not finish in time");
        } finally {
            executorService.shutdown();
            awaitExecutorShutdown(executorService);
        }

        return new CounterRunResult(getActualValue(mode, unsafeCounter, atomicCounter, synchronizedCounter));
    }

    private void incrementByMode(
            CounterMode mode,
            UnsafeCounter unsafeCounter,
            AtomicInteger atomicCounter,
            SynchronizedCounter synchronizedCounter
    ) {
        switch (mode) {
            case UNSAFE -> unsafeCounter.increment();
            case SAFE_ATOMIC -> atomicCounter.incrementAndGet();
            case SAFE_SYNCHRONIZED -> synchronizedCounter.increment();
        }
    }

    private long getActualValue(
            CounterMode mode,
            UnsafeCounter unsafeCounter,
            AtomicInteger atomicCounter,
            SynchronizedCounter synchronizedCounter
    ) {
        return switch (mode) {
            case UNSAFE -> unsafeCounter.get();
            case SAFE_ATOMIC -> atomicCounter.get();
            case SAFE_SYNCHRONIZED -> synchronizedCounter.get();
        };
    }

    private void awaitLatch(CountDownLatch latch, String errorMessage) {
        try {
            if (!latch.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException(errorMessage);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted while waiting for concurrent demo", ex);
        }
    }

    private void awaitExecutorShutdown(ExecutorService executorService) {
        try {
            if (!executorService.awaitTermination(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("ExecutorService did not shut down in time");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted while shutting down executor", ex);
        }
    }

    private void validateArguments(int threadCount, int incrementsPerThread) {
        if (threadCount < 2) {
            throw new IllegalArgumentException("threadCount must be at least 2");
        }
        if (incrementsPerThread < 1) {
            throw new IllegalArgumentException("incrementsPerThread must be at least 1");
        }
    }

    private long calculateExpectedValue(int threadCount, int incrementsPerThread) {
        return (long) threadCount * incrementsPerThread;
    }

    private String buildMessage(long unsafeLostUpdates, long safeLostUpdates) {
        if (unsafeLostUpdates > 0 && safeLostUpdates == 0) {
            return "Unsafe counter lost updates under concurrent access, Atomic counter solved the issue";
        }
        return "Race condition was not reproduced in this run, but Atomic counter still completed without lost updates";
    }

    private enum CounterMode {
        UNSAFE,
        SAFE_ATOMIC,
        SAFE_SYNCHRONIZED
    }

    private static final class UnsafeCounter {

        private int value;

        private void increment() {
            value++;
        }

        private int get() {
            return value;
        }
    }

    private static final class SynchronizedCounter {

        private int value;

        private synchronized void increment() {
            value++;
        }

        private synchronized int get() {
            return value;
        }
    }

    private record CounterRunResult(long actualValue) {
    }
}
