package org.example.jobsearchplatform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Job Search Platform API",
                version = "1.0",
                description = "REST API for users, resumes, vacancies, companies, employers and applications.",
                contact = @Contact(name = "Job Search Platform Team"),
                license = @License(name = "Internal Use")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
public class OpenApiConfig {
}
