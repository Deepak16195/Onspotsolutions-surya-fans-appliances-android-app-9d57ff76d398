package com.surya.onspot.login;


import android.widget.EditText;

import java.util.regex.Pattern;

public class RegisterValidation {
    // Regular Expression
    // you can change the expression based on your need
    private static final String NAME_REGEX = "^[a-zA-Z0-9 ]{3,25}$";
    private static final String NAME_REGEX2 = "^[a-zA-Z0-9_-]{6}$";
    private static final String NAME_REGEX3 = "^[a-zA-Z0-9 _\\.\\n]{30,455}$";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX_India = "^[7-9][0-9]{9}$";
    private static final String PHONE_REGEX_US = "^[1-9][0-9]{9}$";
    //	private static final String PHONE_REGEX_UK = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
    private static final String NAME_REGEX1 = "";
    // Error Messages
    private static final String REQUIRED_MSG = "required";
    private static final String NAME_MSG = "Please Enter Valid Name";
    private static final String NAME_MSG2 = "Please Enter Valid 6 digit Pin";
    private static final String NAME_MSG3 = "Please enter atleast 30 characters";
    private static final String EMAIL_MSG = "Please Enter Valid Email Address";
    private static final String PHONE_MSG = "This Code is not valid for selected Country";
    private static String ph_regex;

    // call this method when you need to check email validation
    public static boolean isName(EditText editText, boolean required) {
        return isValid(editText, NAME_REGEX, NAME_MSG, required);
    }

    public static boolean isName1(EditText editText, boolean required) {
        return isValid(editText, NAME_REGEX2, NAME_MSG2, required);
    }

    public static boolean isName2(EditText editText, boolean required) {
        return isValid(editText, NAME_REGEX3, NAME_MSG3, required);
    }

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }


    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, int CN_code, boolean required) {

        switch (CN_code) {
            case 0:
                ph_regex = PHONE_REGEX_India;
                break;

            case 1:
                ph_regex = PHONE_REGEX_US;
                break;
        }
        return isValid(editText, ph_regex, PHONE_MSG, required);
    }

    public static boolean isDatebirth(EditText editText, boolean required) {
        return isValid(editText, NAME_REGEX1, NAME_MSG, required);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        //		editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
        //
    }


}