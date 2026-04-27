package org.example.jobsearchplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JobSearchPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobSearchPlatformApplication.class, args);
    }
}
