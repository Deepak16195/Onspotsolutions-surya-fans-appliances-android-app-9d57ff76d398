package com.surya.onspot.otp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.login.RegisterValidation;
import com.surya.onspot.login.SignInActivity;
import com.surya.onspot.model.AgentListModel;
import com.surya.onspot.services.SMSBroadCastReceiver;
import com.surya.onspot.services.SMSListener;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class OTP extends FragmentActivity implements SMSListener {

    //	private Button OTP;
    Context cntx;
    private String userMobValue;
    private String otpVerUrl;
    private String userNameValue;
    private String userEmailValue;
    private String userCountryValue;
    private String userId;
    private DatabaseHelper databaseHelper;
    private String userCountryCode;
    private boolean timeOut;
    private EditText etOTPVerifaction;
    private ConnectionDetectorActivity cd;
    private String user_exist;
    private SMSBroadCastReceiver receiver;
    private IntentFilter intentFilter;
    private JSONObject json = null;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        cntx = this;
        databaseHelper = new DatabaseHelper(OTP.this);

        etOTPVerifaction = findViewById(R.id.etOTPVerifaction);
        cd = new ConnectionDetectorActivity(OTP.this);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("id");
        userEmailValue = bundle.getString("userEmailValue");
        userMobValue = bundle.getString("userMobValue");
        userNameValue = bundle.getString("userNameValue");
        userCountryValue = bundle.getString("userCountryValue");
//		userStatusValue = bundle.getString("userStatusValue");
        userCountryCode = bundle.getString("userCountryCode");
        user_exist = bundle.getString("user_exist");
//		OTP= (Button) findViewById(R.id.btnOTPVerify);
        //		OTP.setOnClickListener(this);
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();

        registerViews();
        registerSMSReceiver();
    }

    public void registerSMSReceiver() {
        try {
            intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(999);
            receiver = new SMSBroadCastReceiver();
            SMSBroadCastReceiver.bind(OTP.this);
            registerReceiver(receiver, intentFilter);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void registerViews() {

        etOTPVerifaction = findViewById(R.id.etOTPVerifaction);
        // TextWatcher would let us check validation error on the fly
        etOTPVerifaction.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                RegisterValidation.isName(etOTPVerifaction, false);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        /*if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            ((ImageView) findViewById(R.id.imageView_logo)).setImageResource(R.drawable.en_jago_grahak_jago);
        } else {
            ((ImageView) findViewById(R.id.imageView_logo)).setImageResource(R.drawable.hi_jago_grahak_jago);
        }*/

        etOTPVerifaction.setHint(resources.getString(R.string.enter_verification_key));
        ((Button) findViewById(R.id.btnOTPVerify)).setHint(resources.getString(R.string.submit_verify_otp));
        ((TextView) findViewById(R.id.textView_verification_message)).setHint(resources.getString(R.string.enter_verification_key_for_login));

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!RegisterValidation.isName(etOTPVerifaction, true)) ret = false;
        return ret;
    }

    @SuppressLint("InflateParams")
    private void popUpDialog(String result1, final boolean newuser) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(OTP.this);
        // Get the layout inflater
        LayoutInflater inflater = OTP.this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);
        ((Button) content.findViewById(R.id.btnOTPAlertOK)).setText(resources.getString(R.string.action_text_ok));
        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                        if (newuser) {
                            Bundle bundle = new Bundle();
                            bundle.putString("defaultFrag", "newUserFrag");
//                            Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en");
                            Intent i = new Intent(OTP.this, HomeScreen.class);
                            i.putExtras(bundle);
                            startActivity(i);
                            setResult(10);
                            finish();

                        } else {
                            Toast.makeText(cntx, "Please enter Valid OTP", Toast.LENGTH_LONG).show();
                            etOTPVerifaction.setText("");
//							Bundle bundle = new Bundle();
//							bundle.putString("defaultFrag", "newUserFrag");
//							Intent i = new Intent(OTP.this, SignInActivity.class);
//							i.putExtras(bundle);
//							startActivity(i);
//							setResult(10);
//							finish();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSMSReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyOTPKEY(View v) {
        if (checkValidation()) {
            EditText etOTPVerification = findViewById(R.id.etOTPVerifaction);
            API_CONSTANTS.OTP_VER_VALUE = etOTPVerification.getText().toString();
            API_CONSTANTS.OTP_MOB_VALUE = userMobValue;

            if (user_exist.compareTo("true") == 0)
                API_CONSTANTS.OTP_USER_EXIST_VALUE = "true";
            else
                API_CONSTANTS.OTP_USER_EXIST_VALUE = "false";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(API_CONSTANTS.OTP_VER_KEY, API_CONSTANTS.OTP_VER_VALUE);
                jsonObject.put(API_CONSTANTS.OTP_USER_ID, userId);
//                jsonObject.put(API_CONSTANTS.OTP_MOB_KEY, API_CONSTANTS.OTP_MOB_VALUE);
//                jsonObject.put(API_CONSTANTS.OTP_USER_EXIST_KEY, API_CONSTANTS.OTP_USER_EXIST_VALUE);
//                jsonObject.put(API_CONSTANTS.USER_Id_KEY, userId);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("==", "========= FAIL TO CREATE JSON ==========");
            }

            ServiceCalls Login = new ServiceCalls(this, API_CONSTANTS.API_VERIFY_OTP_WHILE_SIGN_IN_UP,
                    jsonObject.toString(), resources.getString(R.string.verifying), 0, new AsyncResponseActivity() {

                @Override
                public void myAsyncResponse(String result) {
                    // TODO Auto-generated method stub
                    try {
                        json = new JSONObject(result);
                        if (json.getBoolean(API_CONSTANTS.SUCCESS)) {
                            Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf(json.getJSONArray("shipment_types")));
                            JSONArray jsonArray = new JSONArray(Utils.getPreference(OTP.this, PreferenceKeys.USER_SHIPMENT_TYPE, ""));
                            ArrayList<String> list = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                list.add(jsonArray.getString(i));
                                setupAgencyList(jsonArray.getString(i));
                            }
                            parseOTPRsponseJason(result);
                        } else {
                            // SUCCESS = FALSE
                            popUpDialog(json.optString(API_CONSTANTS.RESPONSE_MESSAGE), false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            if (cd.isConnectingToInternet()) {
                Login.execute(1);
            } else {
                showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
            }
        } else {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_LONG).show();
        }
    }

    private void parseOTPRsponseJason(String result) {
        // TODO Auto-generated method stub
        try {
            json = new JSONObject(result);
            try {
                if (json.getBoolean(API_CONSTANTS.SUCCESS)) {
                    // SUCCESS = TRUE
                    userNameValue = json.optString("name");
                    Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, userNameValue);
                    Utils.addPreference(cntx, PreferenceKeys.USER_TYPE, json.getString("user_type"));
                    Utils.addPreference(cntx, PreferenceKeys.USER_ACCESS_LEVEL, String.valueOf(json.getInt("access_level")));
                    Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf(json.getJSONArray("shipment_types")));

                    //                    popUpDialogSuccess(new SpannableString(json.optString(API_CONSTANTS.RESPONSE_MESSAGE).toString()));
                    if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
                        Spannable wordtoSpan = new SpannableString("Hi "
                                + userNameValue + " , welcome to Surya");
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 3,
                                3 + userNameValue.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        popUpDialogSuccess(wordtoSpan);
                    } else {
                        Spannable wordtoSpan = new SpannableString("नमस्ते "
                                + userNameValue + " , आपका स्वागत है ");
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 3,
                                3 + userNameValue.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        popUpDialogSuccess(wordtoSpan);
                    }
                } else {
                    // SUCCESS = FALSE
                    popUpDialog(json.optString(API_CONSTANTS.RESPONSE_MESSAGE), false);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("====>", "Exception");
                popUpDialog(resources.getString(R.string.crash_error), false);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressLint("InflateParams")
    private void popUpDialogSuccess(Spannable result1) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(OTP.this);
        // Get the layout inflater
        LayoutInflater inflater = OTP.this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);

        ((Button) content.findViewById(R.id.btnOTPAlertOK)).setText(resources.getString(R.string.action_text_ok));

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                        insertUserPfref();
                    }
                });
    }

    private void insertUserPfref() {
        // TODO Auto-generated method stub
        databaseHelper = new DatabaseHelper(OTP.this);
        databaseHelper.getWritableDatabase();
        if (json != null) {
            try {
                databaseHelper.insertRegisteredUser(
                        json.optString("email"),
                        json.optString("mobile"),
                        json.optString("name"),
                        json.optString("country"),
                        json.optString("country_code"),
                        json.optString("auth_token"),
                        json.optString("loyalty_point"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("====>", "Exception");
                popUpDialog(API_CONSTANTS.CrashError, false);
            }
        }
        Log.d("------>", user_exist);
//        Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en");
        startActivity(new Intent(OTP.this, HomeScreen.class));
        setResult(10);
        finish();
    }

    @SuppressLint("InflateParams")
    private void showNetworkFailDialog(String msg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(OTP.this);
        // Get the layout inflater
        LayoutInflater inflater = OTP.this.getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                        setResult(10);
                        finish();
                    }

                });
    }

    public void setupAgencyList(final String shipment_type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth_token", json.optString("auth_token"));
            jsonObject.put("shipment_type", shipment_type);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("==", "========= FAIL TO CREATE JSON ==========");
        }

        ServiceCalls objServiceCall = new ServiceCalls(this, (Utils.getPreference(this, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE) + API_CONSTANTS.API_AGENT_LIST,
                jsonObject.toString(), getResources().getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String result) {
//                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                JSONObject objJsonObject = null;
                try {
                    objJsonObject = new JSONObject(result);
                    if (objJsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                        JSONArray objJsonArrayData = objJsonObject.getJSONArray("agencies");
                        for (int index = 0; index < objJsonArrayData.length(); index++) {
                            databaseHelper.insertAgentList(new AgentListModel(
                                    String.valueOf(objJsonArrayData.getJSONObject(index).getInt("id")),
                                    String.valueOf(objJsonArrayData.getJSONObject(index).getString("city")),
                                    String.valueOf(objJsonArrayData.getJSONObject(index).getString("state")),
                                    String.valueOf(objJsonArrayData.getJSONObject(index).getString("mobile")),
                                    String.valueOf(objJsonArrayData.getJSONObject(index).getString("name")),
                                    shipment_type
                            ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (cd.isConnectingToInternet()) {
            objServiceCall.execute(1);
        } else {
            showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(OTP.this, SignInActivity.class);
        startActivity(i);
        setResult(10);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void smsReceived(String messageText) {
//        Your Onspot Verification Key is: FR9DPY17
        try {
//            String[] otp = messageText.split(":");
//            Utils.out("RECEIVED MESSAGE : " + otp[1].toString().trim());
            String message = messageText.substring(messageText.length() - 6, messageText.length());
            if (etOTPVerifaction != null) {
                etOTPVerifaction.setText(message.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
