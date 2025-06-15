package com.example.whatsapp;

import static org.junit.Assert.*;
import org.junit.Test;

public class LoginValidatorTest {

    @Test
    public void email_isValid() {
        assertTrue(LoginValidator.isValidEmail("user@example.com"));
        assertFalse(LoginValidator.isValidEmail("invalidemail"));
        assertFalse(LoginValidator.isValidEmail(""));
        assertFalse(LoginValidator.isValidEmail(null));
    }

    @Test
    public void password_isValid() {
        assertTrue(LoginValidator.isValidPassword("123456"));
        assertFalse(LoginValidator.isValidPassword("123"));
        assertFalse(LoginValidator.isValidPassword(""));
        assertFalse(LoginValidator.isValidPassword(null));
    }
}
