package com.surya.onspot.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.otp.OTP;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;
import com.surya.onspot.utils.Validation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.surya.onspot.R.id.countrySpinner;


public class SignUpActivity extends AppCompatActivity implements OnClickListener {

    Context cntx;
    List<String> list;
    int positionCountry = 0;
    private AppCompatSpinner spinner1;
    private String country;
    private Button SignIn, SignUp;
    private TextInputLayout tetNewUserName, tetNewUserEmail, tetNewUserNo;
    private EditText etNewUserName, etNewUserEmail, etNewUserNo;
    private String serverURL, isoCode;
    private boolean timeOut;
    private ConnectionDetectorActivity cd;
    private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
    private JSONObject json = null;
    private Resources resources;
    private RadioButton radioButtonEnglish, radioButtonHindi;
    private AppCompatSpinner spinnerCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        cd = new ConnectionDetectorActivity(this);
        cntx = this;
        spinner1 = findViewById(countrySpinner);
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();

        registerViews();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.signin:
                Intent signin = new Intent(SignUpActivity.this,
                        SignInActivity.class);
                startActivity(signin);
                setResult(10);
                finish();
                break;
            case R.id.signup:
                if (checkValidation())
                    if (cd.isConnectingToInternet()) {
                        //
                        submitForm();
                    } else {
                        showNetworFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
                    }
                else
                    Toast.makeText(this, "Please Fill All fields", Toast.LENGTH_LONG).show();

                break;
        }
    }

    //Om Submit For getting the values
    private void submitForm() {
        sendUserValue();
    }

    //For Getting Countries name
    private void setSpinner() {
        // TODO Auto-generated method stub
        spinnerCountry = findViewById(R.id.countrySpinner);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.country_arrays));
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinnerCountry.setAdapter(adapter);
        spinnerCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                String[] arrayCountry = getResources().getStringArray(
                        R.array.country_arrays);
                country = arrayCountry[position];
                positionCountry = position;
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
            ((ImageView) findViewById(R.id.imageView_jago_grahak_jago)).setImageResource(R.drawable.en_jago_grahak_jago);
            radioButtonEnglish.setSelected(true);
        } else {
            ((ImageView) findViewById(R.id.imageView_jago_grahak_jago)).setImageResource(R.drawable.hi_jago_grahak_jago);
            radioButtonHindi.setSelected(true);
        }
        ((TextInputLayout) findViewById(R.id.til_username)).setHint(resources.getString(R.string.name));
        ((TextInputLayout) findViewById(R.id.til_useremail)).setHint(resources.getString(R.string.email));
        ((TextInputLayout) findViewById(R.id.til_usernumber)).setHint(resources.getString(R.string.mobile_number));
        ((Button) findViewById(R.id.signup)).setText(resources.getString(R.string.sign_up_button_text));
        ((Button) findViewById(R.id.signin)).setText(resources.getString(R.string.sign_in_button_text));
        ((TextView) findViewById(R.id.textView_update_your_email_address)).setText(resources.getString(R.string.sign_in_bottom_text));
        ((TextView) findViewById(R.id.textView_select_language)).setText(resources.getString(R.string.choose_language_text));

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.country_arrays));
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinnerCountry.setAdapter(adapter);
        spinnerCountry.setSelection(positionCountry);
    }

    //Network fail Dialog
    @SuppressLint("InflateParams")
    private void showNetworFailDialog(String msg) {
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

    // Register View for validations
    public void registerViews() {

        tetNewUserName = findViewById(R.id.til_username);
        etNewUserName = findViewById(R.id.etNewUserName);
        SignUp = findViewById(R.id.signup);
        SignUp.setOnClickListener(this);
        SignIn = findViewById(R.id.signin);
        SignIn.setOnClickListener(this);

        radioButtonEnglish = findViewById(R.id.radioButton_english);
        radioButtonHindi = findViewById(R.id.radioButton_hindi);

        setSpinner();

        etNewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isName(tetNewUserName, etNewUserName, false);
            }
        });
        tetNewUserEmail = findViewById(R.id.til_useremail);
        etNewUserEmail = findViewById(R.id.etNewUserEmail);
        etNewUserEmail.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isEmailAddress(tetNewUserEmail, etNewUserEmail, false);
            }
        });
        tetNewUserNo = findViewById(R.id.til_usernumber);
        etNewUserNo = findViewById(R.id.etNewUserNo);
        etNewUserNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isPhoneNumber1(tetNewUserNo, etNewUserNo, positionCountry, false);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isName(tetNewUserName, etNewUserName, true)) ret = false;
        if (!Validation.isEmailAddress(tetNewUserEmail, etNewUserEmail, true)) ret = false;
        if (!Validation.isPhoneNumber1(tetNewUserNo, etNewUserNo, positionCountry, true))
            ret = false;
        return ret;
    }

    //		Foe sending the values to the server post
    private void sendUserValue() {
        API_CONSTANTS.USER_EMAIL_VALUE = etNewUserEmail.getText().toString();
        API_CONSTANTS.USER_MOB_VALUE = etNewUserNo.getText().toString();
        API_CONSTANTS.USER_NAME_VALUE = etNewUserName.getText().toString();
        API_CONSTANTS.USER_COUNTRY_VALUE = country;
        if (country.equals("India")) {
            isoCode = "+91";
        } else {
            isoCode = "+1";
        }
        API_CONSTANTS.USER_STATUS_VALUE = "false";

        JSONObject objInputJSONObject = new JSONObject();
        try {
            objInputJSONObject.put(API_CONSTANTS.USER_EMAIL_KEY, API_CONSTANTS.USER_EMAIL_VALUE);
            objInputJSONObject.put(API_CONSTANTS.USER_MOB_KEY, API_CONSTANTS.USER_MOB_VALUE);
            objInputJSONObject.put(API_CONSTANTS.USER_NAME_KEY, API_CONSTANTS.USER_NAME_VALUE);
//            objInputJSONObject.put("country", country);
//            objInputJSONObject.put("country_code", isoCode);
//            objInputJSONObject.put(API_CONSTANTS.USER_STATUS_KEY, API_CONSTANTS.USER_STATUS_VALUE);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ServiceCalls Login = new ServiceCalls(this, API_CONSTANTS.API_SIGN_UP, objInputJSONObject.toString(),
                resources.getString(R.string.api_loading_dialog_message_authenticating), 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String output) {
                // TODO Auto-generated method stub
                parseJson(output);
            }
        });

        timeOut = false;

        cd = new ConnectionDetectorActivity(this);
        if (cd.isConnectingToInternet()) {
            Login.execute(1);
        } else {
            //			showNetworFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
            showNetworFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
            Log.d("------->", API_CONSTANTS.NO_INTERNET_MSG);
        }
    }

    /**
     * @param result = Parse the result of API : login
     */
    private void parseJson(String result) {
        // TODO Auto-generated method stub
        try {
            json = new JSONObject(result);
            try {
                if ((json.getString("user_exist").compareTo("false") == 0))
//                    popUpDialog(json.getString(API_CONSTANTS.RESPONSE_MESSAGE), true, json.getString(API_CONSTANTS.SUCCESS));
                    popUpDialog(resources.getString(R.string.verification_request_message_for_login), true, json.getString(API_CONSTANTS.SUCCESS));
                else
                    //                    popUpDialog(json.getString(API_CONSTANTS.RESPONSE_MESSAGE), false, json.getString(API_CONSTANTS.SUCCESS));
                    popUpDialog(resources.getString(R.string.mobile_number_already_exists), false, json.getString(API_CONSTANTS.SUCCESS));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                popUpServerFail(resources.getString(R.string.crash_error));
            }

            /*try {
                if ((responseKeyValue.get("user_exist").compareTo("false") == 0))
                    popUpDialog(responseKeyValue.get("responseMessage"), true, responseKeyValue.get("success"));
                else
                    popUpDialog(responseKeyValue.get("responseMessage"), false, responseKeyValue.get("success"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                popUpServerFail(API_CONSTANTS.CrashError);
            }*/

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param result1 : Popup Message displayed.
     * @param newUser : New User -> true else false
     * @param success : API success (true/false)
     */
    private void popUpDialog(String result1, boolean newUser, String success) {
        // TODO Auto-generated method stub
        final String msuccess = success;
        final boolean new_user = newUser;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);
        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);
        ((Button) content.findViewById(R.id.btnOTPAlertOK)).setText(resources.getString(R.string.action_text_ok));

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                        if (msuccess.equalsIgnoreCase("false")) {
                            if (!new_user) {
                                Intent i = new Intent(SignUpActivity.this,
                                        SignInActivity.class);
                                startActivity(i);
                                setResult(10);
                                finish();
                            }
                        } else {
                            jumpToOTP();
                        }
                    }

                });
    }

    private void jumpToOTP() {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putString("serverURL", serverURL);
        bundle.putString("id", json.optString("user_id"));
        bundle.putString("userEmailValue", API_CONSTANTS.USER_EMAIL_VALUE);
        bundle.putString("userMobValue", API_CONSTANTS.USER_MOB_VALUE);
        bundle.putString("userNameValue", API_CONSTANTS.USER_NAME_VALUE);
        bundle.putString("userCountryValue", API_CONSTANTS.USER_COUNTRY_VALUE);
        bundle.putString("userStatusValue", API_CONSTANTS.USER_STATUS_VALUE);
        bundle.putString("user_exist", json.optString("user_exist"));

        Intent i = new Intent(SignUpActivity.this, OTP.class);
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

    private void popUpServerFail(String result1) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View content = inflater.inflate(R.layout.otp_verification_dialog, null);

        TextView tv = content.findViewById(R.id.otpAlertMessage1);
        tv.setText(result1);
        ((Button) content.findViewById(R.id.btnOTPAlertOK)).setText(resources.getString(R.string.action_text_ok));
        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        content.findViewById(R.id.btnOTPAlertOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                    }

                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();
            dialog = new ProgressDialog(SignUpActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(serverURL);

            try {

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } catch (Exception e) {
                timeOut = true;
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            // popUpDialog(result);
            if (!timeOut)
                parseJson(result);
            else
                showNetworFailDialog(API_CONSTANTS.REQ_TIMEOUT_MSG);
            //			Toast.makeText(this, "Time Out Message", Toast.LENGTH_LONG).show();

        }

    }
}
