package edu.sakovichKseniya.junit.dto;

import lombok.*;

@Value(staticConstructor = "of")
public class User {
    Integer id;
    String username;
    String password;
}

