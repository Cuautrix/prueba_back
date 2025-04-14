package com.financial.system.shared.utils;

import com.financial.system.shared.exceptions.FinancialSystemException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FinancialSystemUtils {
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%&*+-<>])(?!.*(123|abc|ABC|xyz|XYZ))[A-Za-z\\d@#$%&*+-<>]{10,}$";
    private static final String PASSWORD_PATTERN2 = "^(?!.*(012|123|234|345|456|567|678|789|890|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz)).*(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,}$";
    private static final String CURP_REGEX = "^[A-Z]{4}[0-9]{6}[HM][A-Z]{2}[A-Z]{2}[0-9A-Z]{2}[0-9]$";

    private static final String _rfc_pattern_pf = "^(([A-ZÑ&]{4})([0-9]{2})([0][13578]|[1][02])(([0][1-9]|[12][\\d])|[3][01])([A-Z0-9]{3}))|" +
            "(([A-ZÑ&]{4})([0-9]{2})([0][13456789]|[1][012])(([0][1-9]|[12][\\d])|[3][0])([A-Z0-9]{3}))|" +
            "(([A-ZÑ&]{4})([02468][048]|[13579][26])[0][2]([0][1-9]|[12][\\d])([A-Z0-9]{3}))|" +
            "(([A-ZÑ&]{4})([0-9]{2})[0][2]([0][1-9]|[1][0-9]|[2][0-8])([A-Z0-9]{3}))$";


    public static boolean validateCLABE(String clabe) {
        if (clabe.length() != 18) {
            return false;
        }

        if (!clabe.matches("\\d+")) {
            return false;
        }

        int[] weights = {3, 7, 1};

        int sum = 0;

        for (int i = 0; i < 17; i++) {
            int digit = Character.getNumericValue(clabe.charAt(i));
            sum += digit * weights[i % 3];
        }

        int calculatedCheckDigit = (10 - (sum % 10)) % 10;
        int actualCheckDigit = Character.getNumericValue(clabe.charAt(17));

        return calculatedCheckDigit == actualCheckDigit;
    }


    public static boolean validatePassword(String password) {
        return Pattern.compile(PASSWORD_PATTERN2).matcher(password).matches();
    }

    public static boolean validatePasswordV2(String password) {
        return Pattern.compile(PASSWORD_PATTERN2).matcher(password).matches();
    }

    public static boolean isValidCurp(String curp) {
        Pattern pattern = Pattern.compile(CURP_REGEX);
        Matcher matcher = pattern.matcher(curp);
        return matcher.matches();
    }

    public static boolean isValidRfc(String rfc) {
        Pattern pattern = Pattern.compile(_rfc_pattern_pf);
        Matcher matcher = pattern.matcher(rfc.trim().toUpperCase());
        return matcher.matches();
    }

    public static String validateEmptyString(Object obj) throws FinancialSystemException {
        String value = (String) obj;

        if (Objects.isNull(value) || value.isEmpty()) {
            throw new FinancialSystemException("You must provide a value");
        }

        return value;
    }

    public static Integer validateNumber(Object obj) throws FinancialSystemException {
        return Integer.valueOf(String.valueOf(obj));
    }
}