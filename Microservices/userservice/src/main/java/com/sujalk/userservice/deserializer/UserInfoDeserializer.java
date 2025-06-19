package com.sujalk.userservice.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sujalk.userservice.entities.UserInfoDto;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UserInfoDeserializer implements Deserializer<UserInfoDto>
{
    @Override public void close() {
    }
    @Override public void configure(Map<String, ?> arg0, boolean arg1) {
    }

    @Override
    public UserInfoDto deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        UserInfoDto user = null;
        try {
            user = mapper.readValue(arg1, UserInfoDto.class);
            if (user.getLastName() == null) {  // 🔴 Add this validation
                user.setLastName(""); // Default to empty string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}