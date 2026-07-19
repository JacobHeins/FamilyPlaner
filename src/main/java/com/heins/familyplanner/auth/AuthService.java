package com.heins.familyplanner.auth;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.auth.dtos.LoginRequest;
import com.heins.familyplanner.auth.dtos.LoginResponse;
import com.heins.familyplanner.auth.dtos.RegisterRequest;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.repositories.FamilyRepository;
import com.heins.familyplanner.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final FamilyAccountsRepository familyAccountsRepository;
    private final FamilyRepository familyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public Result<LoginResponse> login(LoginRequest loginRequest) {
        log.debug("Try to login: {}", loginRequest.familySlug());

        Optional<FamilyAccount> familyAccountOpt = familyAccountsRepository.findByPublicSlug(loginRequest.familySlug());
        if(familyAccountOpt.isEmpty()){
            log.error("familyAccount with slug {} not found", loginRequest.familySlug());
            return Result.unauthorized("Invalid credentials");
        }
        FamilyAccount familyAccount = familyAccountOpt.get();

        if(!passwordEncoder.matches(loginRequest.password(), familyAccount.getPasswordHash())){
            log.error("password not match");
            return Result.unauthorized("Invalid credentials");
        }

        return Result.success(new LoginResponse(jwtService.generateToken(familyAccount)));
    }

    @Transactional
    public Result<LoginResponse> register(RegisterRequest registerRequest) {
        log.debug("Try to register : {} with family name: {}", registerRequest.familySlug(), registerRequest.familyName());

        Optional<FamilyAccount> accountOpt = familyAccountsRepository.findByPublicSlug(registerRequest.familySlug());
        if(accountOpt.isPresent()){
            return Result.conflict("Account already exists");
        }

        Family family = familyRepository.save(new Family(registerRequest.familyName()));

        String hash =  passwordEncoder.encode(registerRequest.password());

        FamilyAccount account = familyAccountsRepository.save(
                new FamilyAccount(registerRequest.familySlug(), hash, family));

        return Result.success(new LoginResponse(jwtService.generateToken(account)));
    }



}
