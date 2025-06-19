package com.sujalk.userservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujalk.userservice.entities.UserInfo;
import com.sujalk.userservice.entities.UserInfoDto;
import com.sujalk.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload(required = false) UserInfoDto userInfoDto) {
        if (userInfoDto == null || userInfoDto.getUserId() == null) {
            System.err.println("Received invalid message: " + userInfoDto);
            return;
        }

        if (userInfoDto.getLastName() == null) {
            userInfoDto.setLastName(""); // 🔴 Prevent null values
        }

        System.out.println("Processing user: " + userInfoDto.getUserId());

        UserInfoDto savedUser = userService.createOrUpdateUser(userInfoDto);

        System.out.println("User saved in DB: " + savedUser);
    }
}
