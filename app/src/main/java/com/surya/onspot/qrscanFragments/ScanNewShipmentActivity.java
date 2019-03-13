package com.surya.onspot.qrscanFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.surya.onspot.DirectDispatchDialog;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.NewShipmentDetailModel;
import com.surya.onspot.R;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocationDetails;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;

public class ScanNewShipmentActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISION_CAMERA_STORAGE_LOCATION = 350;
    private Activity activity = null;
    ListView scanShipmentList;
    LinearLayout ly_scan_system;
    private DatabaseHelper objDatabaseHelper = null;
    ArrayList<NewShipmentDetailModel> newShipmentDetailModelArrayList = new ArrayList<>();
    ArrayList<NewShipmentDetailModel> newShipmentScannedArrayList = new ArrayList<>();
    private Toolbar toolbar = null;
    private ConnectionDetectorActivity cd;
    private GoogleApiClient googleApiClient = null;
    private LocationDetails objLocationDetails = null;
    private String strLatitude = "", strLongitude = "", strAccuracy = "";
    ScanShipmentCode scanShipmentCodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new_shipment);
        activity = ScanNewShipmentActivity.this;
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

        toolbar = findViewById(R.id.toolbar);
        scanShipmentList = findViewById(R.id.scanShipmentList);
        ly_scan_system = findViewById(R.id.ly_scan_system);

        scanShipmentCodeAdapter = new ScanShipmentCode();
        scanShipmentList.addHeaderView(LayoutInflater.from(activity).inflate(R.layout.row_scan_shipment, null));
        scanShipmentList.setAdapter(scanShipmentCodeAdapter);

        objDatabaseHelper = new DatabaseHelper(activity);
        cd = new ConnectionDetectorActivity(activity);

        if (objDatabaseHelper.getShipmentSystemBarcode() != null) {
            newShipmentDetailModelArrayList = objDatabaseHelper.getNotScanShipmentDetails();
            newShipmentScannedArrayList = objDatabaseHelper.getListScanedShipment();
        }

        if (newShipmentScannedArrayList.size() > 0) {
//            Utils.showToast(activity, "Scanned Item is: " + String.valueOf(newShipmentScannedArrayList.size()));
            scanShipmentList.setVisibility(View.VISIBLE);
            ly_scan_system.setVisibility(View.VISIBLE);
            findViewById(R.id.button_start_scanning).setVisibility(View.GONE);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Scan QR Codes");

        findViewById(R.id.button_start_scanning).setOnClickListener(this);
        findViewById(R.id.button_addmore).setOnClickListener(this);
        findViewById(R.id.button_done).setOnClickListener(this);
        findViewById(R.id.button_discard_all).setOnClickListener(this);
    }

    private void showLocationEnableDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder2 = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
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
            case R.id.button_addmore:
                startActivityForResult(new Intent(activity, NewShipmentScannerActivity.class), Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN);
                break;
            case R.id.button_done:
                if (newShipmentScannedArrayList.size() > 0) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to submit report to server ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DirectDispatchDialog directDispatchDialog = new DirectDispatchDialog(ScanNewShipmentActivity.this, "NewShipment", "0");
                                    directDispatchDialog.show();
                                    directDispatchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            onResume();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Utils.showToast(activity, "Please scan at least one QR code");
                }

                break;
            case R.id.button_discard_all:
//                new AlertDialog.Builder(activity)
//                        .setTitle("Alert")
//                        .setMessage("Are you sure you want to Discard all ?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                objDatabaseHelper.deleteOfflineShipment();
//                                newShipmentDetailModelArrayList = objDatabaseHelper.getNotScanShipmentDetails();
//                                newShipmentScannedArrayList = objDatabaseHelper.getScanShipmentDetails();
//                                scanShipmentCodeAdapter.notifyDataSetChanged();
//                                onResume();
//                            }
//                        })
//                        .setNegativeButton("No", null)
//                        .show();

                startActivityForResult(new Intent(this, NewShipmentRemoveScannerActivity.class), Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN);
                break;
        }
    }

    @Override
    protected void onResume() {
        if (newShipmentScannedArrayList != null)
            if (newShipmentDetailModelArrayList != null) {
                findViewById(R.id.ly_scan_system).setVisibility(objDatabaseHelper.getScanShipmentDetails().size() > 0 ? View.VISIBLE : View.GONE);
                findViewById(R.id.button_start_scanning).setVisibility(objDatabaseHelper.getScanShipmentDetails().size() > 0 ? View.GONE : View.VISIBLE);
                findViewById(R.id.scanShipmentList).setVisibility(objDatabaseHelper.getScanShipmentDetails().size() > 0 ? View.VISIBLE : View.GONE);
            }
        super.onResume();
    }

    public boolean checkLocationStoragePermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, REQUEST_PERMISION_CAMERA_STORAGE_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_FOR_CONTINUES_BARCODE_SCAN:
                    Utils.out("IN REQUEST CODE SCAN");

                    if (objDatabaseHelper.getShipmentSystemBarcode() != null) {
                        newShipmentScannedArrayList.clear();
                        newShipmentScannedArrayList = objDatabaseHelper.getListScanedShipment();
                    }
                    if (newShipmentScannedArrayList.size() > 0)
                        scanShipmentCodeAdapter.notifyDataSetChanged();
//                        Utils.showToast(activity, "Scanned Item is: " + String.valueOf(newShipmentScannedArrayList.size()));
                    onResume();
                    break;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class ScanShipmentCode extends BaseAdapter {

        public ScanShipmentCode() {

        }

        @Override
        public int getCount() {
            return newShipmentScannedArrayList.size();
        }

        @Override
        public NewShipmentDetailModel getItem(int position) {
            return newShipmentScannedArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_scan_shipment, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag(), position);
            return convertView;
        }

        private void initializeViews(final NewShipmentDetailModel object, ViewHolder holder, int position) {
            holder.textViewId.setText(String.valueOf(position + 1));
            holder.textViewName.setText(object.getProductName());
            holder.textViewQuantity.setText(object.getPackedRatio());
        }

        protected class ViewHolder {
            private TextView textViewId;
            private TextView textViewQuantity;
            private TextView textViewName;

            public ViewHolder(View view) {
                textViewId = view.findViewById(R.id.textView_no);
                textViewQuantity = view.findViewById(R.id.textView_quantity);
                textViewName = view.findViewById(R.id.textView_name);
            }
        }
    }

    @Override
    public void onBackPressed() {
        newShipmentDetailModelArrayList = objDatabaseHelper.getScanShipmentDetails();
        if (newShipmentDetailModelArrayList.size() > 0) {
            new AlertDialog.Builder(activity)
                    .setTitle("Alert")
                    .setMessage("Scan Shipment pending for Submit please submit data")
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objDatabaseHelper.updateShipmentData();
                            finish();
                        }
                    })
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else super.onBackPressed();
    }
}
