package com.surya.onspot;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.qrscanFragments.SingleScanActivity;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocationDetails;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.surya.onspot.utils.Constants.REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN;

public class FragmentRetailerReplacement extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private View mainView = null;
    private Toolbar toolbar = null;
    private Button buttonScanOld = null;
    private Button buttonScanNew = null;
    private Button buttonSubmit = null;

    private String strOldBarcode = "";
    private String strNewBarcode = "";
    private Activity activity = null;
    private DatabaseHelper objDatabaseHelper = null;
    private ConnectionDetectorActivity cd = null;

    private LocationDetails objLocationDetails = null;
    private String strLatitude = "", strLongitude = "", strAccuracy = "";
    private GoogleApiClient googleApiClient;
    private int REQUEST_PERMISION_CAMERA_STORAGE_LOCATION = 350;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        cd = new ConnectionDetectorActivity(activity);
        objDatabaseHelper = new DatabaseHelper(activity);
        objLocationDetails = new LocationDetails(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_retailer_replacement, container, false);
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.replacement_warranty));
        ((HomeScreen) getActivity()).refreshDrawer();
        initView();

        return mainView;
    }

    private void initView() {

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationStoragePermission()) {
                showLocationEnableDialog(0);
            } else {
                requestPermission();
            }
        } else {
            showLocationEnableDialog(0);
        }

        buttonScanOld = mainView.findViewById(R.id.button_scan_old_code);
        buttonScanNew = mainView.findViewById(R.id.button_scan_new_code);
        buttonSubmit = mainView.findViewById(R.id.button_submit);
        buttonScanOld.setOnClickListener(this);
        buttonScanNew.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
    }

    public boolean checkLocationStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, REQUEST_PERMISION_CAMERA_STORAGE_LOCATION);
    }

    private void showLocationEnableDialog(final int api_flag) {
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

                        if (api_flag == 1) {
                            if (cd.isConnectingToInternet()) {
                                CommentDialog commentDialog = new CommentDialog(activity);
                                commentDialog.setOnCommentSubmitClickListener(new CommentDialog.OnCommentSubmitClickListener() {
                                    @Override
                                    public void onCommentSubmitClickListener(String comment) {
                                        updateProductWarranty(comment);
                                    }

                                    @Override
                                    public void onUploadImageButtonClickListener() {

                                    }
                                });
                                commentDialog.show();
                            } else {
                                showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
                            }
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), 11);
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
    public void onResume() {
        super.onResume();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.replacement_warranty));
        ((HomeScreen) getActivity()).refreshDrawer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_scan_old_code:
                Intent objIntentScanOldBarcode = new Intent(activity, SingleScanActivity.class);
                objIntentScanOldBarcode.putExtra("IS_OLD_BARCODE_SCANNING", true);
                startActivityForResult(objIntentScanOldBarcode, REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN);
                break;
            case R.id.button_scan_new_code:
                Intent objIntentScanNewBarcode = new Intent(activity, SingleScanActivity.class);
                objIntentScanNewBarcode.putExtra("IS_OLD_BARCODE_SCANNING", false);
                startActivityForResult(objIntentScanNewBarcode, REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN);
                break;
            case R.id.button_submit:
                Utils.out("CONNECTION STATUS : " + googleApiClient.isConnected());
                if (!strOldBarcode.isEmpty() && !strNewBarcode.isEmpty()) {
                    if (strNewBarcode.equals(strOldBarcode)) {
                        Utils.showToast(getActivity(), "Both QR codes are same.");
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkLocationStoragePermission()) {
                                showLocationEnableDialog(1);
                            } else {
                                requestPermission();
                            }
                        } else {
                            showLocationEnableDialog(1);
                        }
                    }
                } else {
                    Utils.showToast(activity, "Please scan old and new barcode first");
                }
                break;
        }
    }

    private void updateProductWarranty(String comment) {
        if (strOldBarcode.equals(strNewBarcode)) {
            Utils.showToast(getActivity(), "Please select another barcode");
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                jsonObject.put("old_qr_code", strOldBarcode);
                jsonObject.put("qr_code", strNewBarcode);
                jsonObject.put("comment", comment);
               // jsonObject.put(API_CONSTANTS.LATITUDE_KEY, strLatitude);
               // jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, strLongitude);
              //  jsonObject.put(API_CONSTANTS.ACCURACY_KEY, strAccuracy);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("==", "========= FAIL TO CREATE JSON ==========");
            }

            ServiceCalls objServiceCall = new ServiceCalls(activity, (Utils.getPreference(activity, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_REPLACE_WARRANTY,
                    jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {
                @Override
                public void myAsyncResponse(String result) {
                    parseData(result);
                }
            });

            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    private void showNetworkFailDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

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
                        alertDialog.dismiss();
                    }
                });
    }

    private void parseData(String result) {
        try {
            strOldBarcode = "";
            strNewBarcode = "";
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                Utils.showToast(activity, "Warranty updated successfully");
            } else {
                Utils.showToast(activity, jsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getStringExtra("BARCODE").equals(strOldBarcode) || data.getStringExtra("BARCODE").equals(strNewBarcode)) {
                        Utils.showToast(activity, "QR Code already scanned. Please try different");
                    } else {
                        if (data.getExtras().getBoolean("IS_OLD_BARCODE_SCANNING")) {
                            // SET BARCODE RESULT TO OLD ONE
                            strOldBarcode = data.getStringExtra("BARCODE");
                            Utils.showToast(getActivity(), "Now scan for New qr-code");
                        } else {
                            // SET BARCODE RESULT TO NEW ONE
                            strNewBarcode = data.getStringExtra("BARCODE");
                        }
                    }
                }
                break;
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
