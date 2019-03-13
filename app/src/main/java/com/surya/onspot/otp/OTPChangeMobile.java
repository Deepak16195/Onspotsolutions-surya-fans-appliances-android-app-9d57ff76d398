package com.surya.onspot.otp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.login.RegisterValidation;
import com.surya.onspot.services.SMSBroadCastReceiver;
import com.surya.onspot.services.SMSListener;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class OTPChangeMobile extends FragmentActivity implements SMSListener {
    //	private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
//	private DatabaseHelper myDBHelper;
    Context cntx;
    SMSBroadCastReceiver receiver;
    private ConnectionDetectorActivity cd;
    private String userEmailValue;
    private String userNewNumber;
    private String userOldNumber;
    private String otpVerUrl;
    private boolean timeOut;
    private EditText etOTPChangeNOVerification;
    private Hashtable<String, String> verifiedResponseKeyValue = new Hashtable<String, String>();
    private Resources resources;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_for_change_mob);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cd = new ConnectionDetectorActivity(this);
        cntx = getApplicationContext();
        Bundle bundle = getIntent().getExtras();

        registerViews();

        userEmailValue = bundle.getString("userEmailValue");
        userNewNumber = bundle.getString("userNewNumber");
        userOldNumber = bundle.getString("userOldNumber");

        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
    }


    private void registerReceiver() {
        try {
            intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(999);
            receiver = new SMSBroadCastReceiver();
            SMSBroadCastReceiver.bind(this);
            registerReceiver(receiver, intentFilter);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
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

    public void registerViews() {
        /*if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "").equals("hi")) {
            ((ImageView) findViewById(R.id.imageView_logo)).setImageResource(R.drawable.hi_jago_grahak_jago);
        } else {
            ((ImageView) findViewById(R.id.imageView_logo)).setImageResource(R.drawable.en_jago_grahak_jago);
        }*/
        etOTPChangeNOVerification = findViewById(R.id.OtPmobile);
        // TextWatcher would let us check validation error on the fly
        etOTPChangeNOVerification.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                RegisterValidation.isName(etOTPChangeNOVerification, false);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!RegisterValidation.isName(etOTPChangeNOVerification, true)) ret = false;


        return ret;
    }

    public void onbtnOTPVerify(View v) {
        if (checkValidation()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("new_mobile_num", userOldNumber);
                jsonObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
                jsonObject.put("verify_key", etOTPChangeNOVerification.getText().toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ServiceCalls Login = new ServiceCalls(this, API_CONSTANTS.API_UPDATE_MOBILE_NUMBER,
                    jsonObject.toString(), resources.getString(R.string.api_loading_dialog_message_authenticating), 0,
                    new AsyncResponseActivity() {

                        @Override
                        public void myAsyncResponse(String output) {
                            // TODO Auto-generated method stub
                            parseOTPResponseJSON(output);
                        }
                    });

            timeOut = false;
            if (cd.isConnectingToInternet()) {
                Login.execute(1);
            } else {
                showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.please_enter_otp), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void smsReceived(String messageText) {
        String[] otp = messageText.split(":");
        Utils.out("RECEIVED MESSAGE : " + otp[1].trim());
        if (etOTPChangeNOVerification != null) {
            etOTPChangeNOVerification.setText(otp[1].trim());
        }
    }

    private void parseOTPResponseJSON(String result) {
        // TODO Auto-generated method stub
        JSONObject json;
//		JSONObject nestedJson;
        try {
            json = new JSONObject(result);
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = json.get(key);
                verifiedResponseKeyValue.put(key, value.toString());
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Set<String> hKeys2 = verifiedResponseKeyValue.keySet();
            for (String string : hKeys2) {
                Log.d("====>",
                        string + " ==>" + verifiedResponseKeyValue.get(string));
            }
            String x = verifiedResponseKeyValue.get("success");
            if (x.compareTo("true") == 0) {
//                popUpDialog(verifiedResponseKeyValue.get("responseMessage"), true);
                popUpDialog(resources.getString(R.string.mobile_number_updated_successfully), true);
            } else
//                popUpDialog(verifiedResponseKeyValue.get("responseMessage"), false);
                popUpDialog(resources.getString(R.string.invalid_verification_key), false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            popUpDialog(resources.getString(R.string.crash_error), false);
        }

    }

    @SuppressLint("InflateParams")
    private void showNetworkFailDialog(String msg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(
                OTPChangeMobile.this);
        // Get the layout inflater
        LayoutInflater inflater = OTPChangeMobile.this.getLayoutInflater();

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

                    }

                });
    }

    @SuppressLint("InflateParams")
    private void popUpDialog(String result1, final boolean correct) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(
                OTPChangeMobile.this);
        // Get the layout inflater
        LayoutInflater inflater = OTPChangeMobile.this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);

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
                        if (correct) {
                            updateNO();
//							startActivity(new Intent(OTPChangeMobile.this,
//									HomeScreen.class));
//							setResult(10);
                            finish();
                        }
                    }

                });
    }

    private void updateNO() {
        // TODO Auto-generated method stub
//		myDBHelper = new DatabaseHelper(OTPChangeMobile.this);
//		myDBHelper.getWritableDatabase();
//
//		myDBHelper.updateMobileNO(userNewNumber, userOldNumber);

        /*
         * Toast.makeText(OTPForChangeMob.this, "Number Changed",
         * Toast.LENGTH_LONG).show();
         */

    }

    @Override
    public void onBackPressed() {
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
}
