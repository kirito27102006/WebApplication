package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class ApplicationSearchIndex {
    private final Map<ApplicationSearchCacheKey, Page<ApplicationResponse>> index = new HashMap<>();

    public synchronized Page<ApplicationResponse> get(ApplicationSearchCacheKey key) {
        Page<ApplicationResponse> value = index.get(key);
        if (value == null) {
            System.out.println("[ApplicationSearchIndex] CACHE MISS for key: " + key);
        } else {
            System.out.println("[ApplicationSearchIndex] CACHE HIT for key: " + key);
        }
        return value;
    }

    public synchronized Page<ApplicationResponse> put(ApplicationSearchCacheKey key,
                                                      Page<ApplicationResponse> value) {
        if (index.containsKey(key)) {
            System.out.println("[ApplicationSearchIndex] CACHE UPDATE for key: " + key);
        } else {
            System.out.println("[ApplicationSearchIndex] CACHE PUT for key: " + key);
        }
        return index.put(key, value);
    }

    public synchronized void clear() {
        System.out.println("[ApplicationSearchIndex] CACHE CLEAR");
        index.clear();
    }
}
