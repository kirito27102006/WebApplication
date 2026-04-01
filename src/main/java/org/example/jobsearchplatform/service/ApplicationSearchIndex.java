package org.example.jobsearchplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ApplicationSearchIndex {
    private final Map<ApplicationSearchCacheKey, Page<ApplicationResponse>> index = new HashMap<>();

    public synchronized Page<ApplicationResponse> get(ApplicationSearchCacheKey key) {
        Page<ApplicationResponse> value = index.get(key);
        if (value == null) {
            log.debug("CACHE MISS for key: {}", key);
        } else {
            log.debug("CACHE HIT for key: {}", key);
        }
        return value;
    }

    public synchronized Page<ApplicationResponse> put(ApplicationSearchCacheKey key,
                                                      Page<ApplicationResponse> value) {
        if (index.containsKey(key)) {
            log.debug("CACHE UPDATE for key: {}", key);
        } else {
            log.debug("CACHE PUT for key: {}", key);
        }
        return index.put(key, value);
    }

    public synchronized void clear() {
        log.debug("CACHE CLEAR");
        index.clear();
    }
}
