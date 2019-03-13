package com.surya.onspot.tab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.surya.onspot.otp.OTPChangeMobile;
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
import java.util.Iterator;

public class ChangeNumberFragment extends Fragment implements OnClickListener {
    public int stateIDnew;
    Context cntx;
    Button btnChangeNumberProceed;
    View rootView;
    private EditText editTextCurrentNumber;
    private EditText editTextOldNumber;
    private TextInputLayout textInputLayoutNewNumber, textInputLayoutCurrentNumber;
    private String user_email;
    private ConnectionDetectorActivity cd;
    private boolean timeOut;
    private String host;
    private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.act_change_number, container, false);
        cntx = getActivity();
        initViews();
        registerViews();
        cd = new ConnectionDetectorActivity(getActivity());

        return rootView;
    }

    private void initViews() {

        editTextCurrentNumber = rootView.findViewById(R.id.etOldNumber);
        editTextOldNumber = rootView.findViewById(R.id.etNewNumber);
        btnChangeNumberProceed = rootView.findViewById(R.id.btnChangeNumberProceed);
        btnChangeNumberProceed.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();
        textInputLayoutNewNumber.setHint(resources.getString(R.string.hint_new_mobile_number));
        textInputLayoutCurrentNumber.setHint(resources.getString(R.string.hint_current_mobile_number));
        btnChangeNumberProceed.setText(resources.getString(R.string.submit_profile));
        ((TextView) rootView.findViewById(R.id.textView_update_your_mobile_number)).setText(resources.getString(R.string.update_your_mobile_number));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeNumberProceed:
                if (checkValidation())
                    if (!isBothAreSame()) {
                        sendReq();
                    } else {
                        showNetworkFailDialog(resources.getString(R.string.both_number_cannot_be_same), "");
                    }
        }
    }


    //	private void fetchUserInfo1() {
//		// TODO Auto-generated method stub
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
//
//
//	}
//
    private boolean isBothAreSame() {
        // TODO Auto-generated method stub

        String s = editTextCurrentNumber.getText().toString();

        String s2 = editTextOldNumber.getText().toString();

        return s.compareTo(s2) == 0;
    }

    @SuppressLint("InflateParams")
    private void showNetworkFailDialog(String msg, String headMsg) {
        // TODO Auto-generated method stub
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
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                    }

                });
    }

    private void sendReq() {
        // TODO Auto-generated method stub

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_mobile_num", editTextCurrentNumber.getText().toString());
            jsonObject.put("new_mobile_num", editTextOldNumber.getText().toString());
            jsonObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
//            jsonObject.put(API_CONSTANTS.USER_Id_KEY, Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ServiceCalls Login = new ServiceCalls(getActivity(), API_CONSTANTS.API_VERIFY_MOBILE_NUMBER,
                jsonObject.toString(), resources.getString(R.string.api_loading_dialog_message_authenticating), 0,
                new AsyncResponseActivity() {

                    @Override
                    public void myAsyncResponse(String output) {
                        // TODO Auto-generated method stub
                        parseJason(output);
                    }
                });

        if (cd.isConnectingToInternet()) {
            Login.execute(1);
        } else {
            showNetworkFailDialog(resources.getString(R.string.no_internet_connection), "Oops! ");
        }

    }

    public void fragmentBecameVisible() {
        updateUI();
    }

    private void parseJason(String result) {
        // TODO Auto-generated method stub
        JSONObject json;
//		JSONObject nestedJson;
        try {
            json = new JSONObject(result);
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = json.get(key);
                responseKeyValue.put(key, value.toString());
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            if (responseKeyValue.get("success").compareTo("true") == 0) {
//                popUpDialog(responseKeyValue.get("responseMessage"), true);
                popUpDialog(resources.getString(R.string.verification_request_message_for_mobile_number), true);
            } else {
//                popUpDialog(responseKeyValue.get("responseMessage"), false);
                popUpDialog(resources.getString(R.string.incorrect_current_mobile_number), false);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            popUpDialog(resources.getString(R.string.crash_error), false);
        }

    }

    public void registerViews() {

        textInputLayoutCurrentNumber = rootView.findViewById(R.id.til_etOldNumber);
        editTextCurrentNumber = rootView.findViewById(R.id.etOldNumber);
        editTextCurrentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isPhoneNumber(textInputLayoutCurrentNumber, editTextCurrentNumber, false);
            }
        });
        textInputLayoutNewNumber = rootView.findViewById(R.id.til_etNewNumber);
        editTextOldNumber = rootView.findViewById(R.id.etNewNumber);
        editTextOldNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isPhoneNumber(textInputLayoutNewNumber, editTextOldNumber, false);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isPhoneNumber(textInputLayoutCurrentNumber, editTextCurrentNumber, true))
            ret = false;
        if (!Validation.isPhoneNumber(textInputLayoutNewNumber, editTextCurrentNumber, true))
            ret = false;
        return ret;
    }

    @SuppressLint("InflateParams")
    private void popUpDialog(String result1, final boolean validResponse) {
        // TODO Auto-generated method stub
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
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                        if (validResponse)
                            jumpToOTP();
                        editTextOldNumber.setText("");
                        editTextCurrentNumber.setText("");
                    }

                });
    }

    private void jumpToOTP() {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();

        API_CONSTANTS.CHANGE_NO_EMAIL_VALUE = user_email;
        bundle.putString("id", Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
        bundle.putString("userEmailValue", API_CONSTANTS.CHANGE_NO_EMAIL_VALUE);
        bundle.putString("userNewNumber", editTextCurrentNumber.getText().toString());
        bundle.putString("userOldNumber", editTextOldNumber.getText().toString());

        Intent i = new Intent(getActivity(), OTPChangeMobile.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(host);

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
                parseJason(result);
            else
                showNetworkFailDialog(resources.getString(R.string.request_time_out), "oops!");
        }

    }

}
