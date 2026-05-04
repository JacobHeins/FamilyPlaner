package com.heins.familyplanner.auth;

import com.heins.familyplanner.auth.dtos.LoginRequest;
import com.heins.familyplanner.auth.dtos.LoginResponse;
import com.heins.familyplanner.auth.dtos.RegisterRequest;
import com.heins.familyplanner.exceptions.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        return switch (authService.login(loginRequest)) {
            case Result.Success<LoginResponse> s -> ResponseEntity.ok(s.value());
            case Result.Failure<LoginResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest registerRequest
    )
    {
        return switch (authService.register(registerRequest)) {
            case Result.Success<LoginResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<LoginResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

}
