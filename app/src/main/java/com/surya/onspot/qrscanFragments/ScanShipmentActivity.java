package com.surya.onspot.qrscanFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.LogDetailModel;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRresponse.SystemBarcodeModel;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocationDetails;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScanShipmentActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISION_CAMERA_STORAGE_LOCATION = 350;
    protected LocationManager locationManager;
    private EditText editTextNumberOfCartons = null;
    private Activity activity = null;
    private TextView textViewScanStatus = null;
    private Button buttonResumeNow = null;
    private Button buttonStopAndSubmit = null;
    private Button buttonDiscardAll = null;
    private ArrayList<String> arrBarcodes = new ArrayList<String>();
    private DatabaseHelper objDatabaseHelper = null;
    private JSONArray jsonArray = new JSONArray();
    private int logId = 0;
    private LogDetailModel logDetails = null;
    private Toolbar toolbar = null;
    private ConnectionDetectorActivity cd;
    private GoogleApiClient googleApiClient = null;
    private LocationDetails objLocationDetails = null;
    private String strLatitude = "", strLongitude = "", strAccuracy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_shipment);
        activity = ScanShipmentActivity.this;
        objLocationDetails = new LocationDetails(activity);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationStoragePermission()) {
                showLocationEnableDialog();
            } else {
                requestPermission();
            }
        } else {
            showLocationEnableDialog();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        objDatabaseHelper = new DatabaseHelper(activity);
        cd = new ConnectionDetectorActivity(activity);
        jsonArray = objDatabaseHelper.getSystemBarcode().getJsonArray();


        editTextNumberOfCartons = (EditText) findViewById(R.id.editText_number_of_cartons);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitle("Scan QR Codes");
        textViewScanStatus = (TextView) findViewById(R.id.textView_scan_status);
        buttonResumeNow = (Button) findViewById(R.id.button_resume_now);
        buttonStopAndSubmit = (Button) findViewById(R.id.button_stop_and_submit);
        buttonDiscardAll = (Button) findViewById(R.id.button_discard_all);

        findViewById(R.id.button_start_scanning).setOnClickListener(this);
        buttonResumeNow.setOnClickListener(this);
        buttonStopAndSubmit.setOnClickListener(this);
        buttonDiscardAll.setOnClickListener(this);

    }

    public boolean checkLocationStoragePermission() {
        return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, REQUEST_PERMISION_CAMERA_STORAGE_LOCATION);
    }

    private void showLocationEnableDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder2 = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder2.setAlwaysShow(true); //this is the key ingredient


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder2.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Utils.out("LOCATION ENABLE SUCCESSFULLY");
                        /**
                         * Opens the scanner screen to scan barcode.
                         * */

                        objLocationDetails.startTracking();
                        objLocationDetails.setCurrentLocationListener(new LocationDetails.CurrentLocationListener() {
                            @Override
                            public void getLatitudeLongitude(LatLng objLatLng, String accuracy) {
                                strLatitude = String.valueOf(objLatLng.latitude).equals("") ? "" : String.valueOf(objLatLng.latitude);
                                strLongitude = String.valueOf(objLatLng.longitude).equals("") ? "" : String.valueOf(objLatLng.longitude);
                                strAccuracy = String.valueOf(accuracy).equals("") ? "" : String.valueOf(accuracy);
                            }
                        });
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 11);
                            Utils.out("IN RESOLUTION REQUIRED");
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Utils.out("IN SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_scanning:
                if (Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText().length() > 0 ? editTextNumberOfCartons.getText() : "0")) > 0) {
                    if (Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText().length() > 0 ? editTextNumberOfCartons.getText() : "0")) <= jsonArray.length()) {
                        ArrayList<String> arrSystemBarcode = new ArrayList<String>();
                        for (int index = 0; index < jsonArray.length(); index++) {
                            try {
                                arrSystemBarcode.add(String.valueOf(jsonArray.get(index)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        /**
                         * INSERT LOG ENTRY AND PASS ID TO NEXT SCREEN
                         * */
                        logDetails = new LogDetailModel(0, Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText().length() > 0 ? editTextNumberOfCartons.getText() : "0")), "", Utils.getCurrentDate(), Utils.getCurrentTime(), 0, 0);
                        objDatabaseHelper.insertLog(logDetails);
                        logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                        Intent intent = new Intent(activity, CustomScannerActivity.class);
                        intent.putExtra("NUMBER_OF_CARTONS", Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText())));
                        intent.putStringArrayListExtra("SCANNED_BARCODE", arrBarcodes);
                        intent.putStringArrayListExtra("SYSTEM_BARCODE", arrSystemBarcode);
                        intent.putExtra("LOG_MODEL", logDetails);
                        startActivityForResult(intent, Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN);
                    } else {
                        Utils.showToast(activity, "Max number of cartons is " + jsonArray.length());
                    }
                } else {
                    Utils.showToast(activity, "Please enter number of cartons first");
                }
                break;
            case R.id.button_resume_now:
                if (Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText().length() > 0 ? editTextNumberOfCartons.getText() : "0")) > 0) {
                    ArrayList<String> arrSystemBarcode = new ArrayList<String>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        try {
                            arrSystemBarcode.add(String.valueOf(jsonArray.get(index)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                    Intent intent = new Intent(activity, CustomScannerActivity.class);
                    intent.putExtra("NUMBER_OF_CARTONS", Integer.parseInt(String.valueOf(editTextNumberOfCartons.getText())));
                    intent.putStringArrayListExtra("SCANNED_BARCODE", arrBarcodes);
                    intent.putStringArrayListExtra("SYSTEM_BARCODE", arrSystemBarcode);
//                    intent.putExtra("LOG_ID", logId);
                    intent.putExtra("LOG_MODEL", logDetails);
                    startActivityForResult(intent, Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN);
                } else {
                    Utils.showToast(activity, "Please enter number of cartons first");
                }
                break;
            case R.id.button_stop_and_submit:
                logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                try {
                    Utils.out("PPP SCANNED BARCODES : " + logDetails.getScannedData());
                    JSONArray jsonArray = new JSONArray(logDetails.getScannedData().isEmpty() ? "[]" : logDetails.getScannedData());
                    if (jsonArray.length() > 0) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Alert")
                                .setMessage("Are you sure you want to stop scanning and submit report to server ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                                        logDetails.setNumberOfCartons(arrBarcodes.size());
                                        logDetails.setLogCompleted(1);
                                        objDatabaseHelper.updateLog(logDetails);
                                        logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                                        try {
                                            submitDetailsToServer(new JSONArray(String.valueOf(logDetails.getScannedData())));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    } else {
                        Utils.showToast(activity, "Please scan at least one QR code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_discard_all:
                discardEntry();
                break;
        }
    }

    private void discardEntry() {
        if (logDetails != null) {
            /**
             * CHECK FOR UPLOAD SCANNED ENTRY SERVICE BEING EXECUTING OR NOT
             * */

            /**
             * Here first we need to check whether user has scanned any qr code or not.
             * If he has scanned any qr code then first we need to take system qr codes and add scanned qr codes into it.
             * After updating it we can delete local log entry.
             * */
            logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
            try {
                final JSONArray jsonArray = new JSONArray(logDetails.getScannedData().isEmpty() ? "[]" : logDetails.getScannedData());
                if (jsonArray.length() > 0) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to discard shipment ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SystemBarcodeModel model = objDatabaseHelper.getSystemBarcode();
                                    JSONArray systemBarcodes = model.getJsonArray();
                                    for (int index = 0; index < jsonArray.length(); index++) {
                                        try {
                                            systemBarcodes.put(jsonArray.get(index));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    model.setJsonArray(systemBarcodes);
                                    objDatabaseHelper.updateSystemBarcodeTable(model);
                                    objDatabaseHelper.discardEntry(logDetails);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                } else {
                    objDatabaseHelper.discardEntry(logDetails);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast(activity, "Some thing went wrong. Please try syncing server data again.");
        }
    }

    private void submitDetailsToServer(JSONArray barcodeList) {
        if (barcodeList.length() > 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                jsonObject.put("code_list", barcodeList);
                jsonObject.put(API_CONSTANTS.LATITUDE_KEY, strLatitude);
                jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, strLongitude);
                jsonObject.put(API_CONSTANTS.ACCURACY_KEY, strAccuracy);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("==", "========= FAIL TO CREATE JSON ==========");
            }

            ServiceCalls objServiceCall = new ServiceCalls(activity, API_CONSTANTS.API_UPLOAD_DATA_TO_SERVER,
                    jsonObject.toString(), "Uploading shipment to server. Please wait...", 0, new AsyncResponseActivity() {

                @Override
                public void myAsyncResponse(String result) {
                    parseData(result);
                }
            });
            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                removeScannedBarcode();
                objDatabaseHelper.updateSystemBarcodeTable(logDetails);
                Utils.showToast(activity, "Shipment saved offline as there's no internet connection");
                logDetails = null;
                finish();
            }
        } else {

        }
    }

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                removeScannedBarcode();
                logDetails.setLogUploadToServerStatus(1);
                objDatabaseHelper.updateLog(logDetails);
                objDatabaseHelper.updateSystemBarcodeTable(logDetails);
                Utils.showToast(activity, "Shipment saved Successfully");
                logDetails = null;
            } else {
                removeScannedBarcode();
                objDatabaseHelper.updateSystemBarcodeTable(logDetails);
                Utils.showToast(activity, jsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
                logDetails = null;
            }
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            removeScannedBarcode();
            objDatabaseHelper.updateSystemBarcodeTable(logDetails);
            finish();

        }
    }

    private void removeScannedBarcode() {
        try {
            SystemBarcodeModel model = objDatabaseHelper.getSystemBarcode();
            JSONArray systemBarcode = model.getJsonArray();
            JSONArray scannedBarcode = new JSONArray(String.valueOf(logDetails.getScannedData()));

            ArrayList<String> arrSystemBarcode = new ArrayList<>();
            for (int index = 0; index < systemBarcode.length(); index++) {
                arrSystemBarcode.add(String.valueOf(systemBarcode.get(index)));
            }

            for (int index = 0; index < scannedBarcode.length(); index++) {
                arrSystemBarcode.remove(String.valueOf(scannedBarcode.get(index)));
            }

            JSONArray finalJSONArray = new JSONArray();
            for (int index = 0; index < arrSystemBarcode.size(); index++) {
                finalJSONArray.put(arrSystemBarcode.get(index));
            }

            logDetails.setScannedData(String.valueOf(finalJSONArray));
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNetworkFailDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = (TextView) content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }

                });
    }

    @Override
    public void onBackPressed() {
        try {
            if (logDetails != null) {
                logDetails = objDatabaseHelper.getCurrentLogDetails(logDetails);
                final JSONArray jsonArray = new JSONArray(logDetails.getScannedData().isEmpty() ? "[]" : logDetails.getScannedData());
                if (jsonArray.length() > 0) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to discard shipment ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SystemBarcodeModel model = objDatabaseHelper.getSystemBarcode();
                                    JSONArray systemBarcodes = model.getJsonArray();
                                    for (int index = 0; index < jsonArray.length(); index++) {
                                        try {
                                            systemBarcodes.put(jsonArray.get(index));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    model.setJsonArray(systemBarcodes);
                                    objDatabaseHelper.updateSystemBarcodeTable(model);
                                    objDatabaseHelper.discardEntry(logDetails);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    objDatabaseHelper.discardEntry(logDetails);
                    finish();
                }
            } else {
                finish();
            }

        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN:
                    Utils.out("IN REQUEST CODE SCAN");
                    arrBarcodes.clear();
                    arrBarcodes = data.getStringArrayListExtra("SCANNED_BARCODE_LIST");
                    logDetails = (LogDetailModel) data.getSerializableExtra("LOG_MODEL");
                    Utils.out("FINAL BARCODE ARRAY : " + arrBarcodes);
                    Utils.out("FINAL BARCODE ARRAY : " + logDetails.getLogCompleted());

                    if (logDetails.getLogCompleted() == 1) {
                        /**
                         * User has scanned all barcode
                         * Upload data to server
                         * */
                        findViewById(R.id.linearLayout_resume).setVisibility(LinearLayout.GONE);
                        findViewById(R.id.linearLayout_start_scanning).setVisibility(LinearLayout.VISIBLE);
                        try {
                            submitDetailsToServer(new JSONArray(String.valueOf(logDetails.getScannedData())));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        /**
                         * Show resume layout
                         * */
                        findViewById(R.id.linearLayout_start_scanning).setVisibility(LinearLayout.GONE);
                        findViewById(R.id.linearLayout_resume).setVisibility(LinearLayout.VISIBLE);
                        textViewScanStatus.setText(String.valueOf(arrBarcodes.size()) + " scan done till now " + (logDetails.getNumberOfCartons() - arrBarcodes.size()) + " pending.");
                    }
                    break;
            }
        } else {

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
