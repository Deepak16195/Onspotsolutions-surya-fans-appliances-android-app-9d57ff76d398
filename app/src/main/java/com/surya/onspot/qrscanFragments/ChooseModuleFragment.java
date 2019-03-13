package com.surya.onspot.qrscanFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.NewShipmentDetailModel;
import com.surya.onspot.R;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.locationprovider.LocationMainActivity;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.LocationDetails;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.surya.onspot.utils.Constants.REQUEST_CODE_FOR_SINGLE_BARCODE_SCAN;


public class ChooseModuleFragment extends Fragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected LocationManager locationManager;
    Context cntx;
    boolean isGPSEnabled = false;
    View view;
    private ConnectionDetectorActivity cd;
    private Resources resources;
    private GoogleApiClient googleApiClient;
    private TextView textViewOwnerName = null;
    private Button buttonFan = null;
    private Button buttonAppliance = null;
    private LocationDetails objLocationDetails = null;
    private String strLatitude = "", strLongitude = "", strAccuracy = "";
    private DatabaseHelper objDatabaseHelper;

    private static List<String> list(String... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cntx = getActivity();
        objDatabaseHelper = new DatabaseHelper(cntx);
        Utils.addPreferenceBoolean(cntx, PreferenceKeys.ISFORMSUBMITTED, true);
        Utils.addPreference(getActivity(), PreferenceKeys.HOME_FRAG, "homeS");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cd = new ConnectionDetectorActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_module, container, false);

        objLocationDetails = new LocationDetails(getActivity());

        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
        }

        Utils.addPreference(getActivity(), PreferenceKeys.HOME_FRAG, "homeS");

        initViews();


        ArrayList<NewShipmentDetailModel> newShipmentDetailModelArrayList = objDatabaseHelper.getScanShipmentDetails();
        if (newShipmentDetailModelArrayList.size() > 0) {
            Intent intent = new Intent(getActivity(), ScanNewShipmentActivity.class);
            startActivity(intent);
        }
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
                            Utils.out("SCANNED QR CODE : " + intent.getExtras().getString("BARCODE"));
                            Intent location = new Intent(getActivity(), LocationMainActivity.class);
                            location.putExtra("QR", intent.getExtras().getString("BARCODE"));
                            startActivity(location);
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
        ((HomeScreen) getActivity()).refreshDrawer();

        super.onResume();
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
            showLocationEnableDialog();
        } else {
            showLocationEnableDialog();
        }

        textViewOwnerName = view.findViewById(R.id.textView_owner_name);
        buttonFan = view.findViewById(R.id.button_fan);
        buttonAppliance = view.findViewById(R.id.button_appliance);

        if (Utils.getPreference(getActivity(), PreferenceKeys.USER_ACCESS_LEVEL, "").equalsIgnoreCase("1")) {
            buttonFan.setVisibility(View.VISIBLE);
            buttonAppliance.setVisibility(View.GONE);
        } else if (Utils.getPreference(getActivity(), PreferenceKeys.USER_ACCESS_LEVEL, "").equalsIgnoreCase("2")) {
            buttonFan.setVisibility(View.GONE);
            buttonAppliance.setVisibility(View.VISIBLE);
        } else if (Utils.getPreference(getActivity(), PreferenceKeys.USER_ACCESS_LEVEL, "").equalsIgnoreCase("3")) {
            buttonFan.setVisibility(View.VISIBLE);
            buttonAppliance.setVisibility(View.VISIBLE);
        }

        buttonFan.setOnClickListener(this);
        buttonAppliance.setOnClickListener(this);
        textViewOwnerName.setText(Utils.getPreference(getActivity(), PreferenceKeys.USERNAMEID, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_fan:
                Utils.addPreference(getActivity(), PreferenceKeys.MODULE_TYPE, "fans");
                switchFragment(new HomeFragment());
                break;
            case R.id.button_appliance:
                Utils.addPreference(getActivity(), PreferenceKeys.MODULE_TYPE, "appliance");
                switchFragment(new HomeFragment());
                break;
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

    private void switchFragment(Fragment mTarget) {
        String backStateName = mTarget.getClass().getName();
        FragmentManager manager = getActivity().getSupportFragmentManager();
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
}
