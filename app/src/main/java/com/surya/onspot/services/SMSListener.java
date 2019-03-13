package com.surya.onspot.services;

public interface SMSListener {
    void smsReceived(String messageText);
}