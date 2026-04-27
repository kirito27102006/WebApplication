package org.example.jobsearchplatform.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ThreadSafeCounterService {

    private final AtomicLong counter = new AtomicLong();

    public long incrementAndGet() {
        return counter.incrementAndGet();
    }

    public long getValue() {
        return counter.get();
    }

    public long reset() {
        counter.set(0);
        return counter.get();
    }
}
