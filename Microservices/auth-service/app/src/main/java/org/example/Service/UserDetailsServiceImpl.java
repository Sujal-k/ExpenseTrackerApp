package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.UserInfo;
import org.example.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepo;
import org.example.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepo.findByUsername(username);
        if (user == null) {
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("Could not find user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto) {
        return userRepo.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signupUser(UserInfoDto userInfoDto) {
        ValidationUtil.validateUserAttributes(userInfoDto);
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if (Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userInfoDto.setUserId(userId);

        // Populate the missing fields
        // Assuming these fields are in the original userInfoDto that is passed into this method.
        // If they are not, you will need to get them from somewhere else.
        // You may need to change the names of the fields, depending on the names of the fields in the original userInfoDto.
        userInfoDto.setFirstName(userInfoDto.getFirstName());
        userInfoDto.setLastName(userInfoDto.getLastName());
        userInfoDto.setPhoneNumber(userInfoDto.getPhoneNumber());

        userRepo.save(new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>()));

        System.out.println("Before sending event to Kafka: " + userInfoDto);
        userInfoProducer.sendEventToKafka(userInfoDto);
        return true;
    }

    public String getUserByUsername(String userName){
        return Optional.of(userRepo.findByUsername(userName)).map(UserInfo::getUserId).orElse(null);
    }

}