package com.hackathon.fundtransfer.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void generateTokenSuccess() {
        String username = "testuser";

        String token = jwtUtil.generateToken(username);

        Assertions.assertNotNull(token);
    }

    @Test
    void extractUserNameTest() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaXlhYW4iLCJpYXQiOjE3MTk5NDI2NDksImV4cCI6MTcxOTk0NDQ0OX0.AUBmio2f2VLhcXUEQsMNfTxJ1m4PyxaOl9qLnBE7L1s";

        String username = jwtUtil.extractUsername(token);

        Assertions.assertNotNull(username);
    }
}
