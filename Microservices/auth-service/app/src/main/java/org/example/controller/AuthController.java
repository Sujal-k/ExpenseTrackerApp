package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.Service.JwtService;
import org.example.Service.RefreshTokenService;
import org.example.Service.UserDetailsServiceImpl;
import org.example.entities.RefreshToken;
import org.example.model.UserInfoDto;
import org.example.response.JwtResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@AllArgsConstructor
@RestController
public class AuthController
{

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDto userInfoDto) {
        System.out.println("➡️ Received signup request: " + userInfoDto);
        try {
            Boolean isSignUped = userDetailsService.signupUser(userInfoDto);

            if (Boolean.FALSE.equals(isSignUped)) {
                System.out.println("❌ Signup failed: User already exists");
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());

            System.out.println("✅ Generated Access Token: " + jwtToken);
            System.out.println("✅ Generated Refresh Token: " + refreshToken.getToken());

            JwtResponseDto response = JwtResponseDto.builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .build();

            // 📦 Final response log before sending to frontend
            System.out.println("📤 Sending response: " + new ObjectMapper().writeValueAsString(response));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception ex) {
            System.out.println("💥 Exception during signup: " + ex.getMessage());
            ex.printStackTrace();
            return new ResponseEntity<>("Exception: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = userDetailsService.getUserByUsername(authentication.getName());
            if(Objects.nonNull(userId)){
                return ResponseEntity.ok().body(userId);
            }

        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

}
