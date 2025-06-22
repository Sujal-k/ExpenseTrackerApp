package org.example.Service;

import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.repository.RefreshTokenRepo;
import org.example.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

     @Autowired
    RefreshTokenRepo refreshTokenRepo;

     @Autowired
    UserRepo userRepo;

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtracted = userRepo.findByUsername(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoExtracted)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(60 * 60 * 1000))
                .build();
        return refreshTokenRepo.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        Instant expiryInstant = token.getExpiryDate();  // Already an Instant, no need for .toInstant()
        Instant now = Instant.now();

        System.out.println("Current time (now): " + now);
        System.out.println("Token expiry time: " + expiryInstant);

        if (expiryInstant.isBefore(now)) {
            // Log expiry and deletion
            System.out.println("❌ Token expired: " + token.getToken() + " (Expired at: " + expiryInstant + ")");

            // Delete expired token
            refreshTokenRepo.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please log in again.");
        }

        System.out.println("✅ Token is valid until: " + expiryInstant);
        return token;
    }


}
