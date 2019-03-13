package com.surya.onspot.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.*;
import com.surya.onspot.otp.OTP;
import com.surya.onspot.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

// sign In Page
public class SignInActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener {

    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_PERMISION_SMS = 350;
    Context cntx;
    List<String> list;
    int positioncountry = 0;
    Resources resources;
    private ConnectionDetectorActivity cd;
    private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
    private String country;
    private Button SignIn;
    private Button SignUp;
    private EditText etExistingUserNo;
    private AppCompatSpinner spinnerCountry;
    private boolean isFirstTime = true;
    private RadioButton radioButtonEnglish, radioButtonHindi;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cd = new ConnectionDetectorActivity(this);
        cntx = getApplicationContext();
        cntx = this;
        list = new ArrayList<String>();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();
        registerViews();
        requestPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                if (cd.isConnectingToInternet()) {
                    // Log.d("666666666", host);
                    if (!etExistingUserNo.getText().toString().trim().isEmpty() && etExistingUserNo.getText().toString().trim().length() > 9)
                        sendReq();
                    else Utils.AlertDialog(this, "","Please enter a valid mobile number");
                } else {
                    showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
                    Log.d("------->", API_CONSTANTS.NO_INTERNET_MSG);
                }
                break;
            case R.id.signup:
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
                setResult(10);
                finish();
        }
    }


    public void registerViews() {
        etExistingUserNo = findViewById(R.id.etExistingUserNo);
        spinnerCountry = findViewById(R.id.countrySpinner);
        radioButtonEnglish = findViewById(R.id.radioButton_english);
        radioButtonHindi = findViewById(R.id.radioButton_hindi);

        SignIn = findViewById(R.id.signin);
        SignIn.setOnClickListener(this);
        SignUp = findViewById(R.id.signup);
        SignUp.setOnClickListener(this);
        setSpinner();
    }

    private void setSpinner() {
        // TODO Auto-generated method stub
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.country_arrays));
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinnerCountry.setAdapter(adapter);

        spinnerCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String[] arrayCountry = getResources().getStringArray(R.array.country_arrays);
                country = arrayCountry[position];
                positioncountry = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if ("en".equals(Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"))) {
            radioButtonEnglish.setChecked(true);
        } else {
            radioButtonHindi.setChecked(true);
        }

        radioButtonEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocaleHelper.setLocale(cntx, "en");
                    Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en");
                    updateView();
                }
            }
        });

        radioButtonHindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocaleHelper.setLocale(cntx, "hi");
                    Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "hi");
                    updateView();
                }
            }
        });

        updateView();
    }

    private void updateView() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();
        if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            ((ImageView) findViewById(R.id.imageView_jago_grahak_jago)).setImageResource(R.drawable.surya_logo);
            radioButtonEnglish.setSelected(true);
        } else {
            ((ImageView) findViewById(R.id.imageView_jago_grahak_jago)).setImageResource(R.drawable.surya_logo);
            radioButtonHindi.setSelected(true);
        }
        ((TextInputLayout) findViewById(R.id.tit_exsuserno)).setHint(resources.getString(R.string.mobile_number));
        ((Button) findViewById(R.id.signup)).setText(resources.getString(R.string.sign_up_button_text));
        ((Button) findViewById(R.id.signin)).setText(resources.getString(R.string.sign_in_button_text));
        ((TextView) findViewById(R.id.textView_update_your_email_address)).setText(resources.getString(R.string.sign_in_bottom_text));
        ((TextView) findViewById(R.id.textView_select_language)).setText(resources.getString(R.string.choose_language_text));
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.country_arrays));
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinnerCountry.setAdapter(adapter);
        spinnerCountry.setSelection(positioncountry);
    }

    @SuppressLint("InflateParams")
    private void showNetworkFailDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                    }
                });
    }

    /**
     * API CALL : Sigin In
     */
    private void sendReq() {
       /* Bundle bundle = new Bundle();
        bundle.putString("mob_no", etExistingUserNo.getText().toString());
        Intent i = new Intent(SignInActivity.this, HomeScreen.class);
        // Intent i = new Intent(SignInActivity.this, OTP.class);
        i.putExtras(bundle);
        startActivity(i);
        setResult(10);
        finish();*/

        API_CONSTANTS.USER_COUNTRY_VALUE = country;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(API_CONSTANTS.API_SIGN_IN_MOB_NUM, etExistingUserNo.getText().toString().trim());

            ServiceCalls Login = new ServiceCalls(this, API_CONSTANTS.API_SIGN_IN,
                    jsonObject.toString(), resources.getString(R.string.api_loading_dialog_message_authenticating), 0,
                    new AsyncResponseActivity() {
                        @Override
                        public void myAsyncResponse(String output) {
                            parseSignInApiResponse(output);
                        }
                    });
            if (cd.isConnectingToInternet()) {
                // new MyAsyncTask().execute();
                Login.execute(1);
            } else {
                showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param result Gets API response as input and parse it.
     */
    private void parseSignInApiResponse(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            Iterator<String> iter = json.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                Object value = json.get(key);
                responseKeyValue.put(key, value.toString());
            }

            String id = json.optString("user_id");
            Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, id);

            Set<String> hKeys2 = responseKeyValue.keySet();
            for (String string : hKeys2) {
                Log.d("====>", string + " ==>" + responseKeyValue.get(string));
            }
            if (responseKeyValue.get("user_exist").compareTo("false") == 0)
//                popUpDialog(responseKeyValue.get("responseMessage"), true);
                Utils.showToast(SignInActivity.this, responseKeyValue.get("responseMessage"));
//                popUpDialog(resources.getString(R.string.verification_request_message_for_login_error), true);
            else
//                popUpDialog(responseKeyValue.get("responseMessage"), false);
                popUpDialog(resources.getString(R.string.verification_request_message_for_login), false);
        } catch (Exception e) {
            Log.d("====>", "Exception");
            popUpServerFail(resources.getString(R.string.crash_error));
        }
    }

    /**
     * @param message   = Message to show in dialog
     * @param isNewUser = Uses Boolean value as
     *                  true for new user
     *                  false for existing user
     */
    @SuppressLint("InflateParams")
    private void popUpDialog(String message, final boolean isNewUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);

        ((Button) content.findViewById(R.id.btnOTPAlertOK)).setText(resources.getString(R.string.action_text_ok));
        tv.setText(message);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (isNewUser) {
                            Intent i = new Intent(SignInActivity.this,
                                    SignUpActivity.class);
                            startActivity(i);
                            setResult(10);
                            finish();
                        } else {
                            jumpToOTP();
                        }
                    }

                });

    }

    private void jumpToOTP() {
        Bundle bundle = new Bundle();
        bundle.putString("userEmailValue", API_CONSTANTS.USER_EMAIL_VALUE);
        bundle.putString("userMobValue", etExistingUserNo.getText().toString());
        bundle.putString("id", Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
        bundle.putString("userNameValue", API_CONSTANTS.USER_NAME_VALUE);
        bundle.putString("userCountryValue", API_CONSTANTS.USER_COUNTRY_VALUE);
        bundle.putString("userStatusValue", API_CONSTANTS.USER_STATUS_VALUE);
        bundle.putString("user_exist", responseKeyValue.get("user_exist"));
      //  Intent i = new Intent(SignInActivity.this, HomeScreen.class);
        Intent i = new Intent(SignInActivity.this, OTP.class);
        i.putExtras(bundle);
        startActivity(i);
        setResult(10);
        finish();
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

    @SuppressLint("InflateParams")
    private void popUpServerFail(String result1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }

                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, REQUEST_PERMISION_SMS);
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("App needs to access Read SMS permissions to allow system auto populate otp. Would you like to allow it?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showSettingsAlert();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestPermission();

                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("App needs to access Read SMS permissions to allow system auto populate otp. Would you like to allow it?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity();

                    }
                });
        alertDialog.show();
    }

    public void startInstalledAppDetailsActivity() {
        if (cntx == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + cntx.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        cntx.startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION_SMS: {
                int count = 0;
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        count = count + 1;
                    }
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(SignInActivity.this, permission);
                        Utils.out("SHOW RATIONAL PERMISSION : " + showRationale);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            Utils.out("REQUEST DENIED");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isFirstTime) {
            Utils.out("POSITION : " + position);
            if (position == 0) {
                Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en");
            } else {
                Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "hi");
            }
            updateView();
        } else {
            isFirstTime = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}