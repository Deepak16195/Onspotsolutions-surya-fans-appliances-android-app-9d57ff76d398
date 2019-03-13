package com.surya.onspot.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.login.RegisterValidation;
import com.surya.onspot.tables.TblInvalidQRresult;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class ReportQrcode extends Activity implements ListView.OnItemClickListener, OnClickListener {

    String ResponseMessage;
    String ResponseType;
    String QrCode;
    String ResponseTime;
    Context cntx;
    private String serverURL;
    private EditText Report;
    private ConnectionDetectorActivity cd;
    private boolean timout;
    private String user_number;
    private String user_email;
    private String user_name;
    private Button submit;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        cntx = this;
        Context context = LocaleHelper.setLocale(this, Utils.getPreference(this, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();
        registerViews();
        fetchFromDatabase();
        Report = findViewById(R.id.reportComment);
        submit = findViewById(R.id.reportsubmit);
        submit.setOnClickListener(this);
        cd = new ConnectionDetectorActivity(this);
        ((TextView) findViewById(R.id.textView_please_enter_comment)).setText(resources.getString(R.string.please_enter_feedback_for_report));
        submit.setText(resources.getString(R.string.submit_feedback));

        if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.en_footer_jgj);
        } else {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.hi_footer_jgj);
        }
    }

//	private void initViews() {
//		registerViews();
//		fetchFromDatabase();
//		
//	}


    public void registerViews() {

        Report = findViewById(R.id.reportComment);
        // TextWatcher would let us check validation error on the fly
        Report.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                RegisterValidation.isName2(Report, false);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!RegisterValidation.isName2(Report, true)) ret = false;


        return ret;
    }

    private void fetchFromDatabase() {
        //		Bundle bundle = getArguments();
        //		id = bundle.getInt("Id", 0);

        Cursor cr1 = getContentResolver().query(TblInvalidQRresult.BASE_CONTENT_URI, null,
                null, null, TblInvalidQRresult.KEY_ID + " DESC");

        {

            while (cr1.moveToNext()) {
                QrCode = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.Code));
                ResponseTime = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.ResponseTime));
                ResponseMessage = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.COL_RESPONSE_MESSAGE));
                ResponseType = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.COL_RESPONSE_TYPE));
                //				x.add(cr1.getInt(cr1.getColumnIndex(TblInvalidQRresult.KEYID)));

            }
        }
    }

    public void sendreq() {

        //		API_CONSTANTS.REPORT_INVALID_QR_CODE_VALUE = invalidQR;
        //		String n = user_number;
        //
        //		if (!n.contains("+")) {
        //			n = "+" + n.trim();
        //		}
        String Feedback = Report.getText().toString();
        API_CONSTANTS.REPORT_USER_COMMENT_VALUE = Report.getText().toString();
        serverURL = (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_SCAN_QR // -------
                + "name=" + user_name//
                + "&mobile_number=" + user_number//
                + "&email=" + user_email //
                + "app_user_id" + Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, "")
                + "&report=" + Feedback; //
        //				+ "&referred_from=" + refFrom;

        serverURL = serverURL.replace(" ", "%20");

        Log.d("*******", serverURL);

        JSONObject jsonObject = new JSONObject();
        try {
//			jsonObject.put(API_CONSTANTS.REPORT_REG_EMAIL_KEY, "manishk@onspotsolutions.com");
//			jsonObject.put(API_CONSTANTS.REPORT_REG_MOB_NO_KEY, user_number);
//			jsonObject.put(API_CONSTANTS.USER_Id_KEY,Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
            jsonObject.put("auth_token", new DatabaseHelper(this).getAuthToken());
            jsonObject.put(API_CONSTANTS.REPORT_INVALID_QR_CODE_KEY, QrCode);
//			jsonObject.put(API_CONSTANTS.REPORT_RESPONSE_TYPE_KEY,
//					ResponseType);
            jsonObject.put(API_CONSTANTS.REPORT_USER_COMMENT_KEY,
                    Feedback);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("==", API_CONSTANTS.API_REPORT_INVALID_BARCODE_PRODUCT + "-----" + jsonObject.toString());

        ServiceCalls Login = new ServiceCalls(this, API_CONSTANTS.API_REPORT_INVALID_BARCODE_PRODUCT,
                jsonObject.toString(), resources.getString(R.string.sending_report), 0, new AsyncResponseActivity() {

            @Override
            public void myAsyncResponse(String output) {
                try {
                    JSONObject objJsonObject1 = new JSONObject(output);
                    if (objJsonObject1.getBoolean(API_CONSTANTS.SUCCESS)) {
                        showmsg(resources.getString(R.string.report_success));
                    } else {
                        showNetworkFailDialog(resources.getString(R.string.crash_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Toast.makeText(ReportQR.this, serverURL, Toast.LENGTH_LONG).show();
        timout = false;
        if (cd.isConnectingToInternet())
            Login.execute(1);
        else
            showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
    }

    private void showNetworkFailDialog(String msg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportQrcode.this);
        // Get the layout inflater
        LayoutInflater inflater = ReportQrcode.this.getLayoutInflater();

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
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();

                    }

                });
    }

    private void showmsg(String msg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportQrcode.this);
        // Get the layout inflater
        LayoutInflater inflater = ReportQrcode.this.getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        TextView tvMsgoops = content.findViewById(R.id.oops);
        tvMsgoops.setVisibility(View.GONE);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                        Intent signin = new Intent(ReportQrcode.this,
                                HomeScreen.class);
                        startActivity(signin);
                        setResult(10);
                        finish();


                    }

                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.reportsubmit:
                if (checkValidation1())
                    sendreq();
//			else
//				Toast.makeText(getActivity(), "Please provide your report", Toast.LENGTH_LONG).show();

                break;

            default:
                break;
        }

    }

}



