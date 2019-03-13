package com.surya.onspot.tab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.otp.OTPChangeEmail;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;
import com.surya.onspot.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class ChangeEmailFragment extends Fragment implements OnClickListener {
    Button btnChangeEmailProceed;
    View rootView;
    Context cntx;
    private ConnectionDetectorActivity cd;
    private EditText editTextOldEmail;
    private EditText editTextNewEmail;
    private TextView textViewUpdateYourEmailAddress;
    private String user_number;
    private TextInputLayout textInputLayoutOldEmail, textViewInputLayoutNewEmail;
    private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.act_change_email, container, false);
        cntx = getActivity();
        initViews();
        registerViews();
        cd = new ConnectionDetectorActivity(getActivity());

        return rootView;
    }

    private void initViews() {

        textViewUpdateYourEmailAddress = rootView.findViewById(R.id.textView_update_your_email_address);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        editTextOldEmail = rootView.findViewById(R.id.editText_old_email);
        editTextNewEmail = rootView.findViewById(R.id.editText_new_email);
        btnChangeEmailProceed = rootView.findViewById(R.id.btnChangeEmailProceed);
        btnChangeEmailProceed.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        textInputLayoutOldEmail.setHint(resources.getString(R.string.hint_current_email));
        textViewInputLayoutNewEmail.setHint(resources.getString(R.string.hint_new_email));
        btnChangeEmailProceed.setText(resources.getString(R.string.submit_profile));
        textViewUpdateYourEmailAddress.setText(resources.getString(R.string.update_your_email_address));
    }

    //code below checks old and new email strings are not same

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeEmailProceed:
                if (checkValidation())
                    if (editTextNewEmail.getText().toString()
                            .compareTo(editTextOldEmail.getText().toString()) != 0) {
                        sendReq();
                    } else {
                        showNetworFailDialog(resources.getString(R.string.both_emails_cannot_be_same), "");
                    }

        }
    }


//	private void fetchUserInfo2() {
//		DatabaseHelper myDBHelper = new DatabaseHelper(getActivity());
//		myDBHelper.getReadableDatabase();
//		listHash = myDBHelper.fetchRegisterdData();
//
//		for (int i = 0; i < listHash.size(); i++) {
//
//			Set<String> hKeys2 = listHash.get(i).keySet();
//			for (String string : hKeys2) {
//				Log.d("====>", string + " ==>" + listHash.get(i).get(string));
//				if (string.compareTo(DatabaseHelper.USER_COL_NUMBER) == 0)
//					user_number = listHash.get(i).get(string);
//				if (string.compareTo(DatabaseHelper.USER_COL_COUNTRY_CODE) == 0)
//					user_country_code = listHash.get(i).get(string).trim();
//				if (string.compareTo(DatabaseHelper.USER_COL_EMAIL) == 0)
//					user_email = listHash.get(i).get(string);
//				if (string.compareTo(DatabaseHelper.USER_COL_NAME) == 0)
//					user_name = listHash.get(i).get(string);
//				if (string.compareTo(DatabaseHelper.USER_COL_COUNTRY) == 0)
//					user_country = listHash.get(i).get(string);
//			}
//		}
//	}
//
//

    @SuppressLint("InflateParams")
    private void showNetworFailDialog(String msg, String headMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        tvMsg.setGravity(Gravity.CENTER);

        if (headMsg.compareTo("") == 0) {
            TextView oops = content.findViewById(R.id.oops);
            oops.setText("headMsg");
            oops.setVisibility(View.GONE);
        }

        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private void sendReq() {
        String oldEmail = editTextOldEmail.getText().toString();
        String newEmail = editTextNewEmail.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_email", oldEmail);
            jsonObject.put("new_email", newEmail);
            jsonObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServiceCalls Login = new ServiceCalls(getActivity(), API_CONSTANTS.API_VERIFY_EMAIL, jsonObject.toString(),
                resources.getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {

            @Override
            public void myAsyncResponse(String result) {
                parseJason(result);
            }
        });
        if (cd.isConnectingToInternet()) {
            Login.execute(1);
        } else {
            showNetworFailDialog(resources.getString(R.string.no_internet_connection), "Oops! ");
        }
    }

    private void parseJason(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = json.get(key);
                responseKeyValue.put(key, value.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Set<String> hKeys2 = responseKeyValue.keySet();
        try {
            for (String string : hKeys2) {
                Log.d("====>", string + " ==>" + responseKeyValue.get(string));
            }
            if (responseKeyValue.get("success").compareTo("true") == 0) {
//                popUpDialog(responseKeyValue.get("responseMessage"), true);
                popUpDialog(resources.getString(R.string.verification_request_message), true);
            } else {
//                popUpDialog(responseKeyValue.get("responseMessage"), false);
                popUpDialog(resources.getString(R.string.incorrect_current_email_address), false);
            }

        } catch (Exception e) {
            Log.d("====>", "Exception");
            popUpDialog(API_CONSTANTS.CrashError, false);
        }
        /*
         * if (responseKeyValue.get("user_exist").compareTo("false") == 0)
         * popUpDialog(
         * "Sorry we are unable to track your phone number in our database, Please use the new user registration form to register."
         * , false); else popUpDialog(responseKeyValue.get("responseMessage"),
         * true);
         */
    }

    @SuppressLint("InflateParams")
    private void popUpDialog(String result1, final boolean validResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

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
                        if (validResponse)
                            jumpToOTP();
                        editTextNewEmail.setText("");
                        editTextOldEmail.setText("");
                    }

                });
    }

    private void jumpToOTP() {

        Log.d("-----", "Jump To OTP");

        Bundle bundle = new Bundle();
        String oldEmail = editTextOldEmail.getText().toString();
        String newEmail = editTextNewEmail.getText().toString();

        bundle.putString("id", Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
        bundle.putString("userOldEmail", oldEmail);
        bundle.putString("userNewEmail", newEmail);
        bundle.putString("userNumber", user_number);

        Intent i = new Intent(getActivity(), OTPChangeEmail.class);
        i.putExtras(bundle);
        startActivity(i);

    }


    public void registerViews() {
        textInputLayoutOldEmail = rootView.findViewById(R.id.textInputLayout_old_email);
        editTextOldEmail = rootView.findViewById(R.id.editText_old_email);
        editTextOldEmail.addTextChangedListener(new TextWatcher() {
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
                Validation.isEmailAddress(textViewInputLayoutNewEmail, editTextOldEmail, false);
            }
        });
        textViewInputLayoutNewEmail = rootView.findViewById(R.id.textInputLayout_new_email);
        editTextNewEmail = rootView.findViewById(R.id.editText_new_email);
        editTextNewEmail.addTextChangedListener(new TextWatcher() {
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
                Validation.isEmailAddress(textViewInputLayoutNewEmail, editTextNewEmail, false);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isEmailAddress(textInputLayoutOldEmail, editTextOldEmail, true))
            ret = false;
        if (!Validation.isEmailAddress(textViewInputLayoutNewEmail, editTextNewEmail, true))
            ret = false;
        return ret;
    }

    public void fragmentBecameVisible() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        textInputLayoutOldEmail.setHint(resources.getString(R.string.hint_current_email));
        textViewInputLayoutNewEmail.setHint(resources.getString(R.string.hint_new_email));
        btnChangeEmailProceed.setText(resources.getString(R.string.submit_profile));
        textViewUpdateYourEmailAddress.setText(resources.getString(R.string.update_your_email_address));
    }


//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//		return true;
//	}
}
