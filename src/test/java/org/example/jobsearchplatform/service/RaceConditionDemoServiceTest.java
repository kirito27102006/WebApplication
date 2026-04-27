package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.dto.CounterConcurrencyDemoResponse;
import org.example.jobsearchplatform.dto.RaceConditionDemoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService raceConditionDemoService = new RaceConditionDemoService();

    @Test
    void runDefaultDemo_detectsRaceConditionAndKeepsAtomicCounterCorrect() {
        RaceConditionDemoResponse response = raceConditionDemoService.runDefaultDemo();

        assertEquals(64, response.getThreadCount());
        assertEquals(2_000, response.getIncrementsPerThread());
        assertEquals(response.getExpectedValue(), response.getSafeValue());
        assertEquals(0L, response.getSafeLostUpdates());
        assertTrue(response.getUnsafeValue() < response.getExpectedValue());
        assertTrue(response.isRaceConditionDetected());
    }

    @Test
    void runDemo_withCustomSettings_returnsExpectedSafeValue() {
        RaceConditionDemoResponse response = raceConditionDemoService.runDemo(50, 1_500);

        assertEquals(75_000L, response.getExpectedValue());
        assertEquals(75_000L, response.getSafeValue());
        assertEquals(0L, response.getSafeLostUpdates());
        assertTrue(response.getUnsafeValue() < response.getExpectedValue());
    }

    @Test
    void runUnsafeDemo_withCustomSettings_losesUpdates() {
        CounterConcurrencyDemoResponse response = raceConditionDemoService.runUnsafeDemo(50, 1_500);

        assertEquals("unsafe", response.getStrategy());
        assertEquals(75_000L, response.getExpectedValue());
        assertTrue(response.getActualValue() < response.getExpectedValue());
        assertTrue(response.getLostUpdates() > 0);
    }

    @Test
    void runAtomicDemo_withCustomSettings_keepsExpectedValue() {
        CounterConcurrencyDemoResponse response = raceConditionDemoService.runAtomicDemo(50, 1_500);

        assertEquals("atomic", response.getStrategy());
        assertEquals(75_000L, response.getExpectedValue());
        assertEquals(75_000L, response.getActualValue());
        assertEquals(0L, response.getLostUpdates());
        assertTrue(response.isThreadSafe());
    }

    @Test
    void runSynchronizedDemo_withCustomSettings_keepsExpectedValue() {
        CounterConcurrencyDemoResponse response = raceConditionDemoService.runSynchronizedDemo(50, 1_500);

        assertEquals("synchronized", response.getStrategy());
        assertEquals(75_000L, response.getExpectedValue());
        assertEquals(75_000L, response.getActualValue());
        assertEquals(0L, response.getLostUpdates());
        assertTrue(response.isThreadSafe());
    }

    @Test
    void runDemo_withInvalidThreadCount_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> raceConditionDemoService.runDemo(1, 1_000)
        );

        assertEquals("threadCount must be at least 2", exception.getMessage());
    }

    @Test
    void runDemo_withInvalidIncrementCount_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> raceConditionDemoService.runDemo(50, 0)
        );

        assertEquals("incrementsPerThread must be at least 1", exception.getMessage());
    }
}
