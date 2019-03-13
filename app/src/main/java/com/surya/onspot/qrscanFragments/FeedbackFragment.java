package com.surya.onspot.qrscanFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.login.RegisterValidation;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


public class FeedbackFragment extends Fragment implements OnClickListener {

    Context cntx;
    View view;
    private Button submit;
    private EditText feedback;
    private AppCompatSpinner countrySpinner;
    private String user_number;
    private String user_email;
    private String user_name;
    private String refFrom;
    private String serverUrl;
    private boolean timeOut;
    private ConnectionDetectorActivity cd;

    private Resources resources;
    private String authToken;
    private JSONObject json = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page_feedback, container, false);
        cntx = getActivity();
        cd = new ConnectionDetectorActivity(getActivity());
        initViews();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.feedback_title));
        submit.setText(resources.getString(R.string.submit_feedback));
        feedback.setHint(resources.getString(R.string.enter_your_feedback));
        ArrayAdapter adapter = new ArrayAdapter<>(cntx, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.feddback_array));
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        countrySpinner.setAdapter(adapter);
//        countrySpinner.setAdapter(new ArrayAdapter<String>(cntx, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.feddback_array)));
    }

    private void initViews() {
        registerViews();
        setSpinner();
        feedback = view.findViewById(R.id.feedback);
        submit = view.findViewById(R.id.feedbacksubmit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedbacksubmit:
                if (checkValidation1())
                    sendReq();
//			else
//				Toast.makeText(getActivity(), "Please provide your feedback", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        // TODO Auto-generated method stub
//		switchFragment(new FeedbackFragment());
    }

    private void setSpinner() {
        // TODO Auto-generated method stub
        countrySpinner = view.findViewById(R.id.spinnerFeedback);
        countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                // your code here

                String[] array = getResources().getStringArray(
                        R.array.feddback_array);
                refFrom = array[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void registerViews() {

        feedback = view.findViewById(R.id.feedback);
        // TextWatcher would let us check validation error on the fly
        feedback.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                RegisterValidation.isName2(feedback, false);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!RegisterValidation.isName2(feedback, true)) ret = false;
        return ret;
    }

    private void showNetworkFailDialog(String msg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        TextView tvoo = content.findViewById(R.id.oops);
        tvoo.setVisibility(View.GONE);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                        feedback.setText("");
                    }

                });
    }

    private void sendReq() {
        // TODO Auto-generated method stub
        String Feedback = feedback.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            DatabaseHelper database = new DatabaseHelper(getActivity());
            jsonObject.put("auth_token", database.getAuthToken());
            jsonObject.put("feedback", Feedback);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        timeOut = false;
        ServiceCalls Login = new ServiceCalls(getActivity(), API_CONSTANTS.API_SUBMIT_FEEDBACK, jsonObject.toString(),
                resources.getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {

            @Override
            public void myAsyncResponse(String result) {
                try {
                    json = new JSONObject(result);
                    if (json.getBoolean(API_CONSTANTS.SUCCESS)) {
                        showNetworkFailDialog(resources.getString(R.string.feedback_successfully_submitted));
                    }
                } catch (Exception e) {
                    popUpDialog(resources.getString(R.string.crash_error), false);
                }
            }
        });

        if (cd.isConnectingToInternet()) {
            Login.execute(1);
        } else {
            showNetworkFailDialog(resources.getString(R.string.no_internet_connection), "Oops! ");
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
                            feedback.setText("");
                        }
                    }
                });
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private void showNetworkFailDialog(String msg, String headMsg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        feedback.setText("");
                        alertDialog.dismiss();
                        feedback.setText("");
                    }

                });
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
            HttpPost httpPost = new HttpPost(serverUrl);

            try {

                httpClient.execute(httpPost);

            } catch (Exception e) {
                timeOut = true;
                Log.d("ERROR", " NO RESPONSE ");
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            // popUpDialog(result);
            if (!timeOut)
                showNetworkFailDialog(resources.getString(R.string.feedback_successfully_submitted));
            else
                showNetworkFailDialog(resources.getString(R.string.request_time_out), "oops!");
            Log.d("----->", "      Response     " + result);
        }
    }
//	private void switchFragment(Fragment mTarget){
//		((FragmentActivity) cntx).getSupportFragmentManager().beginTransaction()
//		.replace(R.id.fragment_root, mTarget)
//		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//		.addToBackStack(null)
//		.commit();
//	}

}
