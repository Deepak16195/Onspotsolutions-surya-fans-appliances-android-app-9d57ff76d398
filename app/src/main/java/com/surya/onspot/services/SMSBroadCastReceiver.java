package com.surya.onspot.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "OtpReader";
    private static SMSListener smsListener;
    private static String receiverString1 = "VK-SRLKSP";
    private static String receiverString2 = "VM-SRLKSP";

    public static void bind(SMSListener listener) {
        smsListener = listener;
//        receiverString = sender;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {

            final Object[] pdusArr = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdusArr.length; i++) {

                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusArr[i]);
                String senderNum = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();
                Log.i(TAG, "senderNum: " + senderNum + " message: " + message);
                try {
                    if (senderNum.toLowerCase().contains(receiverString1.toLowerCase()) || senderNum.toLowerCase().contains(receiverString2.toLowerCase())) { //If message received is from required number.
                        //If bound a listener interface, callback the overriden method.
                        if (smsListener != null) {
                            smsListener.smsReceived(message);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "" + e);
                }
            }
        }
    }
}