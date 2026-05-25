package com.rudra.financemanager.exceptions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestExceptionController {

    @GetMapping("/bad-request")
    public void badRequest() {
        throw new BadRequestException("Bad request");
    }

    @GetMapping("/unauthorized")
    public void unauthorized() {
        throw new UnauthorizedException("Unauthorized");
    }

    @GetMapping("/forbidden")
    public void forbidden() {
        throw new ForbiddenException("Forbidden");
    }

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Not found");
    }

    @GetMapping("/conflict")
    public void conflict() {
        throw new ConflictException("Conflict");
    }

    @GetMapping("/runtime")
    public void runtime() {
        throw new RuntimeException("Unexpected error");
    }

    @PostMapping("/validate")
    public void validate(@Valid @RequestBody InvalidRequest request) {
    }

    public static class InvalidRequest {

        @NotBlank(message = "Name is required")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}