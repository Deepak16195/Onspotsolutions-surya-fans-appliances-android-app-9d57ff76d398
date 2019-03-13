package com.surya.onspot;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.surya.onspot.login.SignInActivity;
import com.surya.onspot.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends Activity {
    Context cntx;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String TAG = "SplashActivity";
    int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cntx = this;
        Fabric.with(this, new Crashlytics());

        ((ImageView) findViewById(R.id.logo)).setImageResource(R.drawable.surya_logo);
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        ((TextView) findViewById(R.id.textView_splash)).setText(resources.getString(R.string.drawer_header_text));
        ((TextView) findViewById(R.id.textView_splash_government)).setText(resources.getString(R.string.drawer_header_text_gov));
        if (checkAndRequestPermissions()) {
            launchHomeScreen();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private boolean checkAndRequestPermissions() {
        int WritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int GpsFindLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int GpsCrossLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (WritePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (GpsFindLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (GpsCrossLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "permission granted");
                        // process the normal flow
                        if (checkAndRequestPermissions()) {
                            launchHomeScreen();
                        }
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to Grant permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(cntx);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: " + getPackageName())).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(cntx)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void launchHomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.getPreferenceBoolean(cntx, PreferenceKeys.ISFORMSUBMITTED, false)) {
                    startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                  //  Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, "Jignesh");
                  //  Utils.addPreference(cntx, PreferenceKeys.USER_TYPE, Constants.USER_TYPE_BRANCH);
                   // Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf("[Distributor,Branch]"));

                } else {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                  //  Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, "Jignesh");
                 //   Utils.addPreference(cntx, PreferenceKeys.USER_TYPE, Constants.USER_TYPE_BRANCH);
                   // Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf("[Distributor,Branch]"));
                }
                SplashActivity.this.finish();
                // close this activity
                finish();

              /*  if (Utils.getPreferenceBoolean(cntx, PreferenceKeys.ISFORMSUBMITTED, false)) {
                    startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                    Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, "Jignesh");
                    Utils.addPreference(cntx, PreferenceKeys.USER_TYPE, Constants.USER_TYPE_BRANCH);
                    Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf("[Distributor,Branch]"));

                } else {
                   startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                   // startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                    Utils.addPreference(cntx, PreferenceKeys.USERNAMEID, "Jignesh");
                    Utils.addPreference(cntx, PreferenceKeys.USER_TYPE, Constants.USER_TYPE_BRANCH);
                    Utils.addPreference(cntx, PreferenceKeys.USER_SHIPMENT_TYPE, String.valueOf("[Distributor,Branch]"));
                }
                SplashActivity.this.finish();
                finish();*/
            }
        }, SPLASH_TIME_OUT);
    }
}