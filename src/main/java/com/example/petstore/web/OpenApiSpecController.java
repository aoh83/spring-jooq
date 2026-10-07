package com.example.petstore.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenApiSpecController {

    private static final MediaType YAML = MediaType.parseMediaType("application/yaml");

    @GetMapping(value = "/openapi.yaml", produces = "application/yaml")
    public Resource openApiSpec() {
        return new ClassPathResource("openapi/petstore.yaml");
    }
}
