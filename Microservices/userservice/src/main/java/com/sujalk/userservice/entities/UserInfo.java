package com.sujalk.userservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class UserInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @JsonProperty("first_name")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonProperty("phone_number")
    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @JsonProperty("email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonProperty("profile_pic")
    @Column(name = "profile_pic")
    private String profilePic;

}