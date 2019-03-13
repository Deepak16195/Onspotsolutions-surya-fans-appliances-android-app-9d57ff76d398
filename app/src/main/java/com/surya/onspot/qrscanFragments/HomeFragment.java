package com.surya.onspot.qrscanFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.andexert.library.RippleView;
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
import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.LogDetailModel;
import com.surya.onspot.QRresponse.NewShipmentDetailModel;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRresponse.SystemBarcodeModel;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.locationprovider.LocationMainActivity;
import com.surya.onspot.tab.FragmentReceivedHistoryBase;
import com.surya.onspot.tab.FragmentShipmentHistoryBase;
import com.surya.onspot.tab.ScanMainFragment;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.LocationDetails;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.surya.onspot.utils.Constants.REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN;


public class HomeFragment extends Fragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISION_CAMERA_STORAGE_LOCATION = 350;
    protected LocationManager locationManager;
    Button ScannerButton;
    ImageView Rewards;
    ImageView History;
    Context cntx;
    boolean isGPSEnabled = false;
    Parameters params;
    View view;
    ArrayList<LogDetailModel> arrLogDetailModels = new ArrayList<>();
    ArrayList<NewShipmentDetailModel> arrNewShipment = new ArrayList<>();
    private TextView switchStatus;
    private Switch btnSwitch;
    private ConnectionDetectorActivity cd;
    private Resources resources;
    private TextView textViewHistory, textViewRewards;
    private GoogleApiClient googleApiClient;
    private TextView textViewOwnerName = null;
    private DatabaseHelper objDatabaseHelper = null;
    private LocationDetails objLocationDetails = null;
    private String strLatitude = "", strLongitude = "", strAccuracy = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cntx = getActivity();
        Utils.addPreferenceBoolean(cntx, PreferenceKeys.ISFORMSUBMITTED, true);
        Utils.addPreference(getActivity(), PreferenceKeys.HOME_FRAG, "home_frg");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cd = new ConnectionDetectorActivity(getActivity());
        objDatabaseHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        btnSwitch = view.findViewById(R.id.btnswitch);
        switchStatus = view.findViewById(R.id.switchStatus);

        textViewHistory = view.findViewById(R.id.textView_history);
        textViewRewards = view.findViewById(R.id.textView_rewards);

        objLocationDetails = new LocationDetails(getActivity());

        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        btnSwitch.setChecked(false);
        switchStatus.setText(resources.getString(R.string.torch_is_off));

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
        }
        btnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
                resources = context.getResources();
                if (btnSwitch.isChecked()) {
                    switchStatus.setText(resources.getString(R.string.torch_is_on));
                } else {
                    switchStatus.setText(resources.getString(R.string.torch_is_off));
                }
            }
        });
        Utils.addPreference(getActivity(), PreferenceKeys.HOME_FRAG, "home_frg");

        initViews();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            case REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN:
                locationManager = (LocationManager) cntx.getSystemService(Context.LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (provider.contains("gps") || provider.contains("network")) {
                    Utils.out("PRASANNA PRINT : LOCATION IS ON");
                    if (cd.isConnectingToInternet()) {
                        if (resultCode == Activity.RESULT_OK) {

         /*               String qr = intent.getStringExtra("SCAN_RESULT");
                        Intent location = new Intent(getActivity(), LocationMainActivity.class);
                        location.putExtra("QR", qr);
                        startActivity(location);*/
                            Utils.out("SCANNED QR CODE : " + intent.getExtras().getString("BARCODE"));
                            Intent location = new Intent(getActivity(), LocationMainActivity.class);
                            location.putExtra("QR", intent.getExtras().getString("BARCODE"));
                            startActivity(location);
                            if (resultCode == Activity.RESULT_CANCELED) {
                                // Handle cancel
                                Log.i("App", "Scan unsuccessful");
                            }
                        }
                    } else {
                        showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
                    }
                } else {
                    Utils.out("PRASANNA PRINT : LOCATION IS OFF");
                    showNetworkFailDialog1(resources.getString(R.string.location_setting_not_on));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.onpost_title));
        Utils.out("LANGUAGE SELECTED : " + Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
//        Utils.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        ((HomeScreen) getActivity()).refreshDrawer();

        textViewHistory.setText(resources.getString(R.string.history));
        textViewRewards.setText(resources.getString(R.string.rewards));
        updateViews();
//		SpannableString s = new SpannableString(getString(R.string.Home));
//		s.setSpan(new CustomTypefaceSpan(cntx, "Raleway-Bold.ttf"), 0, s.length(),
//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.onResume();
    }

    private void updateViews() {
        ScannerButton.setText(resources.getString(R.string.scan_barcode));
        if (btnSwitch.isChecked()) {
            switchStatus.setText(resources.getString(R.string.torch_is_on));
        } else {
            switchStatus.setText(resources.getString(R.string.torch_is_off));
        }
    }

    private void showNetworkFailDialog(String msg) {
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
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private void showNetworkFailDialog1(String msg) {
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
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        cntx.startActivity(intent);
                        alertDialog.dismiss();
                    }
                });
    }

    private void initViews() {

        googleApiClient = new GoogleApiClient.Builder(getActivity())
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

        ScannerButton = view.findViewById(R.id.ScannerButton);
        ScannerButton.setOnClickListener(this);
        Rewards = view.findViewById(R.id.img_rewards);
        Rewards.setOnClickListener(this);
        History = view.findViewById(R.id.img_history);
        History.setOnClickListener(this);
        RippleView viewHistory = view.findViewById(R.id.ripleView_history);
        viewHistory.setOnClickListener(this);
        RippleView viewRewards = view.findViewById(R.id.ripleView_rewards);
        viewRewards.setOnClickListener(this);

        textViewOwnerName = view.findViewById(R.id.textView_owner_name);
        view.findViewById(R.id.button_sync_server_data).setOnClickListener(this);
        view.findViewById(R.id.button_receiving_new_shipment).setOnClickListener(this);
        view.findViewById(R.id.button_sending_new_shipment).setOnClickListener(this);
        view.findViewById(R.id.button_receiving_history).setOnClickListener(this);
        view.findViewById(R.id.button_shipment_history).setOnClickListener(this);
        view.findViewById(R.id.button_sale_scan).setOnClickListener(this);

        view.findViewById(R.id.button_sending_new_shipment).setVisibility(Utils.getPreference(getActivity(), PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER) ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.button_receiving_history).setVisibility(Utils.getPreference(getActivity(), PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER) ? View.GONE : View.VISIBLE);


        if (Utils.getPreference(getActivity(), PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER)) {
            view.findViewById(R.id.button_shipment_history).setVisibility(Button.GONE);
            view.findViewById(R.id.button_sale_scan).setVisibility(Button.VISIBLE);
        } else {
            view.findViewById(R.id.button_shipment_history).setVisibility(Button.VISIBLE);
            view.findViewById(R.id.button_sale_scan).setVisibility(Button.GONE);
        }
        textViewOwnerName.setText(Utils.getPreference(getActivity(), PreferenceKeys.USERNAMEID, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sync_server_data:
                if (cd.isConnectingToInternet()) {
                    if (googleApiClient.isConnected()) {
                        arrLogDetailModels = objDatabaseHelper.getNotUploadedLogDetails();
                        if (arrLogDetailModels.size() > 0) {
                            uploadOfflineDataFirst();
                        } else {
                            objDatabaseHelper.deleteServerDataTableData();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d("==", "========= FAIL TO CREATE JSON ==========");
                            }

                            ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_GET_SYSTEM_BARCODE_LIST, jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {
                                @Override
                                public void myAsyncResponse(String result) {
                                    /**
                                     * PARSE RESPONSE AND SAVE SYSTEM BARCODE TO LOCAL DATABASE
                                     * */
                                    parseLogData(result);
                                }
                            });
                            if (cd.isConnectingToInternet()) {
                                objServiceCall.execute(1);
                            } else {
                                showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
                            }
                        }

                        arrNewShipment = objDatabaseHelper.getScanShipmentDetails();
                        if (arrNewShipment.size() > 0) {
                            uploadOfflineShipmentDataFirst();
                        } else {
                            objDatabaseHelper.deleteOfflineShipment();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d("==", "========= FAIL TO CREATE JSON ==========");
                            }
                            ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE) + API_CONSTANTS.API_PECKED_CARTOON, jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {

                                @Override
                                public void myAsyncResponse(String result) {
                                    parseShipmentData(result);
                                }
                            });
                            if (cd.isConnectingToInternet()) {
                                objServiceCall.execute(1);
                            } else {
                                showNetworkFailDialog(resources.getString(R.string.no_internet_connection));
                            }
                        }
                    } else {
                        googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                    }
                } else {
                    showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
                }
                break;
            case R.id.button_receiving_new_shipment:
                /**
                 * FIRST CHECK FOR SYSTEM BARCODE STORED IN LOCAL DATABASE & THEN GO FURTHER
                 * */
                if (objDatabaseHelper.getSystemBarcode() != null) {
                    if (objDatabaseHelper.getSystemBarcode().getJsonArray().length() > 0) {
                        Intent intent = new Intent(getActivity(), ScanShipmentActivity.class);
                        startActivity(intent);
                    } else {
                        Utils.showToast(getActivity(), "No data available");
                    }
                } else {
                    /**
                     * SYSTEM BARCODE NOT EXISTS. SHOW ALERT OF SYNC SERVER DATA
                     * */
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Alert")
                            .setMessage("Please sync server data first to proceed further.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                break;
            case R.id.button_sending_new_shipment:
                /**
                 * FIRST CHECK FOR SYSTEM BARCODE STORED IN LOCAL DATABASE & THEN GO FURTHER
                 if (cd.isConnectingToInternet()) {
                 * */
                if (objDatabaseHelper.getShipmentSystemBarcode() != null) {
                    if (objDatabaseHelper.getShipmentSystemBarcode().size() > 0) {
                        Intent intent = new Intent(getActivity(), ScanNewShipmentActivity.class);
                        startActivity(intent);
                    } else {
                        Utils.AlertDialog(getActivity(), "", "No data available. We have not received any shipments till now");
                    }
                } else {
                    /**
                     * SYSTEM BARCODE NOT EXISTS. SHOW ALERT OF SYNC SERVER DATA
                     * */
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Alert")
                            .setMessage("Please sync server data first to proceed further.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                break;
            case R.id.button_receiving_history:
                if (cd.isConnectingToInternet()) {
                    switchFragment(new FragmentReceivedHistoryBase());
                } else {
                    showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
                }
                break;
            case R.id.button_shipment_history:
                if (cd.isConnectingToInternet()) {
                    switchFragment(new FragmentShipmentHistoryBase());
                } else {
                    showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
                }
                break;
            case R.id.button_sale_scan:
                /*Intent intent = new Intent(getActivity(), FragmentRetailerReplacement.class);
                startActivity(intent);*/
                /**
                 * OPEN SCANNER AND SCAN THE SALING PRODUCT TO ACTIVATE WARRANTY
                 * */
                Intent objIntentScanNewBarcode = new Intent(getActivity(), SingleScanActivity.class);
                objIntentScanNewBarcode.putExtra("IS_OLD_BARCODE_SCANNING", false);
                startActivityForResult(objIntentScanNewBarcode, REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN);
                break;
            case R.id.ScannerButton:
                googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                break;
            case R.id.ripleView_rewards:
                switchFragment(new FragmentRewards());
                break;
            case R.id.ripleView_history:
                switchFragment(new ScanMainFragment());
                break;
        }
    }

    private void uploadOfflineShipmentDataFirst() {
        if (!Utils.getPreference(getContext(), PreferenceKeys.OFFLINE_SHIPMENT_DATA, "").trim().isEmpty()) {
            ServiceCalls objServiceCall = new ServiceCalls(cntx, (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE) + API_CONSTANTS.API_SHIPMENT_DONE,
                    Utils.getPreference(getContext(), PreferenceKeys.OFFLINE_SHIPMENT_DATA, ""), "Requesting...", 0, new AsyncResponseActivity() {
                @Override
                public void myAsyncResponse(String result) {
                    try {
                        JSONObject objJsonObject = new JSONObject(result);
                        if (objJsonObject.getString("success").equalsIgnoreCase("true")) {
                            Utils.showToast(getContext(), "Dispatch Successfully");
                            objDatabaseHelper.deleteOfflineShipment();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                showNetworkFailDialog(cntx.getResources().getString(R.string.no_internet_connection));
            }

        }
    }

    private void uploadOfflineDataFirst() {
        LogDetailModel logDetailModel = arrLogDetailModels.get(0);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
            jsonObject.put("code_list", new JSONArray(String.valueOf(logDetailModel.getScannedData().length() > 0 ? logDetailModel.getScannedData() : "[]")));
            jsonObject.put(API_CONSTANTS.LATITUDE_KEY, strLatitude);
            jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, strLongitude);
            jsonObject.put(API_CONSTANTS.ACCURACY_KEY, strAccuracy);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("==", "========= FAIL TO CREATE JSON ==========");
        }

        ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_UPLOAD_DATA_TO_SERVER,
                jsonObject.toString(), "Uploading offline shipment to server. Please wait...", 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                        LogDetailModel model = arrLogDetailModels.get(0);
                        model.setLogUploadToServerStatus(1);
                        objDatabaseHelper.updateLog(model);
                        arrLogDetailModels.remove(0);
                        if (arrLogDetailModels.size() > 0) {
                            uploadOfflineDataFirst();
                        } else {
//                            objDatabaseHelper.deleteOfflineLogs();
                            Utils.showToast(getActivity(), "Data Uploaded Successfully");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (cd.isConnectingToInternet()) {
            objServiceCall.execute(1);
        } else {
            showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void parseLogData(String result) {
        try {
            JSONObject objJsonObject = new JSONObject(result);
            if (objJsonObject.optBoolean(API_CONSTANTS.SUCCESS)) {
                if (objJsonObject.getJSONArray("list").length() > 0) {
                    Utils.showToast(getActivity(), "Data Synced Successfully");
                } else {
                    Utils.showToast(getActivity(), "No data available");
                }
                objDatabaseHelper.insertSystemBarcode(new SystemBarcodeModel(new JSONArray(objJsonObject.optString("list"))));
            } else {
                Utils.showToast(getActivity(), objJsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseShipmentData(String result) {
        try {
            JSONObject objJsonObject = new JSONObject(result);
            if (objJsonObject.optBoolean(API_CONSTANTS.SUCCESS)) {
                JSONArray jsonElements = new JSONArray(objJsonObject.optString("master_carton_codes"));
                if (jsonElements.length() > 0) {
                    Utils.showToast(getActivity(), "Data Synced Successfully");
                } else {
                    Utils.showToast(getActivity(), "No data available");
                }
                for (int i = 0; i < jsonElements.length(); i++) {
                    objDatabaseHelper.insertShipment(new NewShipmentDetailModel(
                            String.valueOf(jsonElements.getJSONObject(i).get("id")),
                            String.valueOf(jsonElements.getJSONObject(i).getInt("packing_ratio")),
                            jsonElements.getJSONObject(i).getString("code"),
                            String.valueOf(jsonElements.getJSONObject(i).getString("product_name")),
                            "1"));
                }

            } else {
                Utils.showToast(getActivity(), objJsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public boolean checkLocationStoragePermission() {
        return ContextCompat.checkSelfPermission(cntx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, REQUEST_PERMISION_CAMERA_STORAGE_LOCATION);
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
//						finish();
                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

    private void switchFragment(Fragment mTarget) {
        String backStateName = mTarget.getClass().getName();
        android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        boolean samefrag = false;
        if (manager.findFragmentByTag(backStateName) != null) {
            samefrag = manager.findFragmentByTag(backStateName).isAdded();
        }
        if (!fragmentPopped && !samefrag) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_root, mTarget, backStateName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(backStateName)
                    .commit();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION_CAMERA_STORAGE_LOCATION: {
                int count = 0;
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        count = count + 1;
                    }
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
                        Utils.out("SHOW RATIONAL PERMISSION : " + showRationale);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            Utils.out("REQUEST DENIED");
                        }
                    }
                }
                if (count == permissions.length) {
                    LocationRequest locationRequest2 = LocationRequest.create();
                    locationRequest2.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest2.setInterval(30 * 1000);
                    locationRequest2.setFastestInterval(1000);
                    LocationSettingsRequest.Builder builder2 = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest2);
                    builder2.setAlwaysShow(true); //this is the key ingredient

                    PendingResult<LocationSettingsResult> result2 = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder2.build());
                    result2.setResultCallback(new ResultCallback<LocationSettingsResult>() {
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
                                        status.startResolutionForResult(getActivity(), 11);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("App needs to access Location, Camera & Storage permissions. Would you like to allow it?");
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
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("App needs to access Location, Camera & Storage permissions. Would you like to allow it?");
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
}
