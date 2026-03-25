package org.example.jobsearchplatform.service;

import java.util.Objects;

public class ApplicationSearchCacheKey {

    private final int page;
    private final int size;
    private final String sort;

    public ApplicationSearchCacheKey(int page, int size, String sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ApplicationSearchCacheKey that = (ApplicationSearchCacheKey) object;
        return page == that.page
                && size == that.size
                && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, sort);
    }

    @Override
    public String toString() {
        return "ApplicationSearchCacheKey{" +
                "page=" + page +
                ", size=" + size +
                ", sort='" + sort + '\'' +
                '}';
    }
}
