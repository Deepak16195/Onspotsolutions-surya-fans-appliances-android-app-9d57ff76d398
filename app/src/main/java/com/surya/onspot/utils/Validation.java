package com.surya.onspot.utils;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Pattern;

public class Validation {
    private static final String NAME_REGEX = "^[a-zA-Z ]{3,}$";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^[7-9][0-9]{9}$";
    private static final String PINCODE_REGEX = "^[0-9]{6}$";
    private static final String PASSWORD_REGEX = "^[0-9]{6}$";
    private static final String SAPCODE_REGEX = "^[0-9]{7}$";
    private static final String REQUIRED_MSG = "Required";
    private static final String INVALID_NAME_MSG = "Invalid name";
    private static final String INVALID_EMAIL_MSG = "Invalid email address";
    private static final String INVALID_PASSOWRD_MSG = "Invalid password";
    private static final String INVALID_PINCODE_MSG = "Invalid pin code";
    private static final String PHONE_REGEX_India = "^[7-9][0-9]{9}$";
    private static final String PHONE_MSG = "This Code is not valid for selected Country";
    private static final String PHONE_REGEX_US = "^[1-9][0-9]{9}$";
    private static final String INVALID_SAP_MSG = "Invalid sap code";
    private static final String INVALID_PHONE_MSG = "Invalid phone number";
    private static String ph_regex;

    public static boolean isName(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, NAME_REGEX, INVALID_NAME_MSG, required);
    }

    public static boolean isEmailAddress(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, EMAIL_REGEX, INVALID_EMAIL_MSG, required);
    }

    public static boolean isShopAddress(TextInputLayout textInputLayout, EditText editText) {
        return hasText(textInputLayout, editText);
    }

    public static boolean isPhoneNumber(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, PHONE_REGEX, INVALID_PHONE_MSG, required);
    }

    public static boolean isPhoneNumber1(TextInputLayout textInputLayout, EditText editText, int CN_code, boolean required) {

        switch (CN_code) {
            case 0:
                ph_regex = PHONE_REGEX_India;
                break;

            case 1:
                ph_regex = PHONE_REGEX_US;
                break;
        }
        return isValid(textInputLayout, editText, ph_regex, PHONE_MSG, required);
    }

    public static boolean isPassword(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, PASSWORD_REGEX, INVALID_PASSOWRD_MSG, required);
    }

    public static boolean isPincode(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, PINCODE_REGEX, INVALID_PINCODE_MSG, required);
    }

    public static boolean isSapCode(TextInputLayout textInputLayout, EditText editText, boolean required) {
        return isValid(textInputLayout, editText, SAPCODE_REGEX, INVALID_SAP_MSG, required);
    }

    public static boolean isOpeningDate(TextInputLayout textInputLayout, EditText editText) {
        return hasText(textInputLayout, editText);
    }

    private static boolean isValid(TextInputLayout textInputLayout, EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        textInputLayout.setError(null);
        if (required && !hasText(textInputLayout, editText)) return false;
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean hasText(TextInputLayout textInputLayout, EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            textInputLayout.setError(REQUIRED_MSG);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }
}
