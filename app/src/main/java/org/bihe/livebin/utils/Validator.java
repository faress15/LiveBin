package org.bihe.livebin.utils;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isPasswordValid(@NonNull String s) {
        String regex = "[a-zA-Z0-9]{3,25}$";
        return s.matches(regex);
    }

    //    chatgpt
    public static boolean isIdentificationCodeValid(String nationalCode) {
        if (nationalCode == null || nationalCode.length() != 10 || !nationalCode.matches("\\d+")) {
            return false;
        }

        if (nationalCode.matches("^(\\d)\\1{9}$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nationalCode.charAt(i)) * (10 - i);
        }

        int remainder = sum % 11;
        int checkDigit = Character.getNumericValue(nationalCode.charAt(9));

        return (remainder < 2 && checkDigit == remainder) || (remainder >= 2 && checkDigit == (11 - remainder));
    }

    public static boolean isValidIranianPhoneNumber(String phoneNumber) {
        return Pattern.matches("^(\\+98|0)?9\\d{9}$", phoneNumber);
    }

}