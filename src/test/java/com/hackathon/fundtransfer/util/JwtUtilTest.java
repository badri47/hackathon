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
}
