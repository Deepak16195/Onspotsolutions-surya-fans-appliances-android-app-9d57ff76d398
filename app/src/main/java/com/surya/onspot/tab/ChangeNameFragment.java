package com.surya.onspot.tab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;
import com.surya.onspot.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;

public class ChangeNameFragment extends Fragment implements OnClickListener {
    Button btnChangeNameProceed;
    Context cntx;
    View rootView1;
    private ConnectionDetectorActivity cd;
    private EditText editTextCurrentName, editTextNewName;
    private TextInputLayout textInputLayoutCurrentName, textInputLayoutNewName;
    private Hashtable<String, String> responseKeyValue = new Hashtable<String, String>();
    private String user_number;
    private String serverUrl;
    private boolean timeOut;
    private String oldName;
    private String newName;
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView1 = inflater.inflate(R.layout.act_change_name, container, false);
        cntx = getActivity();
        initViews();
        registerViews();
        cd = new ConnectionDetectorActivity(getActivity());
        return rootView1;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        textInputLayoutCurrentName.setHint(resources.getString(R.string.hint_current_name));
        textInputLayoutNewName.setHint(resources.getString(R.string.hint_new_name));
        btnChangeNameProceed.setText(resources.getString(R.string.submit_profile));
    }

    private void initViews() {
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        editTextCurrentName = rootView1.findViewById(R.id.editText_current_name);
        editTextNewName = rootView1.findViewById(R.id.editText_new_name);
        btnChangeNameProceed = rootView1.findViewById(R.id.btnChangeNameProceed);
        btnChangeNameProceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeNameProceed:
                if (checkValidation())
                    if (!isOldNameEmpty() && !isNewNameEmpty()) {
                        if (!isBothAreSame()) {
                            sendReq();
                        } else {
                            showNetworkFailDialog(resources.getString(R.string.both_names_can_not_same), "");
                        }
                    }
        }
    }


//	private void fetchUserInfo() {
////		DatabaseHelper myDBHelper = new DatabaseHelper(getActivity());
////		myDBHelper.getReadableDatabase();
////		listHash = myDBHelper.fetchRegisterdData();
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

    public void registerViews() {
        textInputLayoutCurrentName = rootView1.findViewById(R.id.textInputLayout_current_name);
        editTextCurrentName = rootView1.findViewById(R.id.editText_current_name);
        // TextWatcher would let us check validation error on the fly
        editTextCurrentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isName(textInputLayoutCurrentName, editTextCurrentName, false);
            }
        });
        textInputLayoutNewName = rootView1.findViewById(R.id.textInputLayout_new_name);
        editTextNewName = rootView1.findViewById(R.id.editText_new_name);
        // TextWatcher would let us check validation error on the fly
        editTextNewName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Validation.isName(textInputLayoutNewName, editTextNewName, false);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isName(textInputLayoutCurrentName, editTextCurrentName, true)) ret = false;
        if (!Validation.isName(textInputLayoutNewName, editTextNewName, true)) ret = false;


        return ret;
    }

    private boolean isBothAreSame() {
        String s = editTextCurrentName.getText().toString();
        String s2 = editTextNewName.getText().toString();
        Log.d("---#>", s);
        Log.d("---#>", s2);
        if (s.compareTo(s2) == 0) {
            Log.d("---#>", "SAME NUMBER");
            return true;
        } else {
            Log.d("---#>", "DIFF NUMBER");
            return false;
        }
    }


    private boolean isOldNameEmpty() {
        return editTextCurrentName.getText().toString().compareTo("") == 0;
    }

    private boolean isNewNameEmpty() {
        return editTextNewName.getText().toString().compareTo("") == 0;
    }

    @SuppressLint("InflateParams")
    private void showNetworkFailDialog(String msg, String headMsg) {
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
        oldName = editTextCurrentName.getText().toString();
        newName = editTextNewName.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_name", oldName);
            jsonObject.put("new_name", newName);
            jsonObject.put("auth_token", new DatabaseHelper(getActivity()).getAuthToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServiceCalls Login = new ServiceCalls(getActivity(), API_CONSTANTS.API_UPDATE_NAME,
                jsonObject.toString(), resources.getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {

            @Override
            public void myAsyncResponse(String output) {
                parseJason(output);
            }
        });

        if (cd.isConnectingToInternet()) {
            Login.execute(1);
        } else {
            showNetworkFailDialog(resources.getString(R.string.no_internet_connection), "Oops! ");
        }
    }

    private void parseJason(String result) {
        JSONObject json;
        try {
            responseKeyValue.clear();
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

        try {
            if (responseKeyValue.get("success").compareTo("true") == 0) {
//                popUpDialog(responseKeyValue.get("responseMessage"), true);
                popUpDialog(resources.getString(R.string.api_name_update_successfully), true);
            } else {
//                popUpDialog(responseKeyValue.get("responseMessage"), false);
                popUpDialog(resources.getString(R.string.api_old_name_is_wrong), false);
            }
        } catch (Exception e) {
            popUpDialog(resources.getString(R.string.crash_error), false);
        }
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
                        if (validResponse) {
                            editTextNewName.setText("");
                            editTextCurrentName.setText("");
                        }
                    }
                });
    }

    public void fragmentBecameVisible() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        textInputLayoutCurrentName.setHint(resources.getString(R.string.hint_current_name));
        textInputLayoutNewName.setHint(resources.getString(R.string.hint_new_name));
        btnChangeNameProceed.setText(resources.getString(R.string.submit_profile));
    }
}
