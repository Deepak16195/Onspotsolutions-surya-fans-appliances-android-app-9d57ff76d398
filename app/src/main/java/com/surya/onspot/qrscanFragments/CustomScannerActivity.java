package com.surya.onspot.qrscanFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.LogDetailModel;
import com.surya.onspot.R;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CustomScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private Activity activity = null;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private Button buttonStopScanning;

    private BeepManager beepManager = null;
    private ArrayList<String> scannedBarcodeHistory = new ArrayList<String>();
    private ArrayList<String> systemBarcode = new ArrayList<String>();
    private int intLogID = 0;
    private int numberOfCartons = 0;
    private boolean isFlashOn = false;
    private JSONArray jsonArray = new JSONArray();
    private DatabaseHelper objDatabaseHelper = null;
    private LogDetailModel logDetails = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_scanner);
        activity = CustomScannerActivity.this;
        objDatabaseHelper = new DatabaseHelper(activity);
        numberOfCartons = getIntent().getExtras().getInt("NUMBER_OF_CARTONS");
        scannedBarcodeHistory = getIntent().getExtras().getStringArrayList("SCANNED_BARCODE");
        systemBarcode = getIntent().getExtras().getStringArrayList("SYSTEM_BARCODE");
        logDetails = (LogDetailModel) getIntent().getExtras().getSerializable("LOG_MODEL");
        if (logDetails != null) {
            try {
                jsonArray = new JSONArray(logDetails.getScannedData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        buttonStopScanning = findViewById(R.id.button_stop_scanning);
        beepManager = new BeepManager(this);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                capture.onPause();
                beepManager.playBeepSoundAndVibrate();

                if (result.getText() == null) {
                    capture.onPause();
                    return;
                } else {
                    if (isBarcodeExistsInSystem(result.getText()) == true) {
                        if (isBarcodeExists(result.getText()) == false) {
                            // NEW BARCODE DETECTED
                            if (scannedBarcodeHistory.size() <= numberOfCartons) {
                                jsonArray.put(result.getText());
                                scannedBarcodeHistory.add(result.getText());
                                /**
                                 * UPDATE LOG TO ADD NEW SCANNED BARCODE
                                 * */
                                logDetails.setScannedData(String.valueOf(jsonArray));
                                objDatabaseHelper.updateLog(logDetails);
                                if (scannedBarcodeHistory.size() == numberOfCartons) {
                                    Utils.out("BARCODE : CARTONS SCANNED : " + scannedBarcodeHistory);
                                    Utils.out("BARCODE : CARTONS SCANNED : " + jsonArray);
                                    logDetails.setLogCompleted(1);
                                    objDatabaseHelper.updateLog(logDetails);
                                    capture.onPause();
                                    new AlertDialog.Builder(activity)
                                            .setTitle("Success")
                                            .setMessage("You have successfully uploaded " + numberOfCartons + " master cartons.")
                                            .setCancelable(false)
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    /**
                                                     * SCANNED BARCODE DONE
                                                     * */
                                                    Intent intent = new Intent();
                                                    intent.putStringArrayListExtra("SCANNED_BARCODE_LIST", scannedBarcodeHistory);
                                                    intent.putExtra("LOG_MODEL", logDetails);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                            }).show();
                                }
                            } else {
                                Utils.showToast(CustomScannerActivity.this, "Max scan carton limit reached.");
                            }
                        } else {
                            // BARCODE ALREADY EXISTS
                            Utils.showToast(CustomScannerActivity.this, "barcode already scanned. Please try different barcode. ");
                            Utils.out("BARCODE : EXISTS");
                        }
                    } else {
                        /**
                         * BARCODE DOES NOT EXISTS IN SYSTEM
                         * */
                        Utils.showToast(activity, "Barcode does not exists in system.");
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        capture.onResume();
                    }
                }, 3000);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });

    }

    private boolean isBarcodeExistsInSystem(String strBarcode) {
        return systemBarcode.contains(strBarcode);
    }

    private boolean isBarcodeExists(String barcode) {
        return systemBarcode.contains(barcode) && scannedBarcodeHistory.contains(barcode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (isFlashOn) {
            isFlashOn = false;
            barcodeScannerView.setTorchOff();
        } else {
            isFlashOn = true;
            barcodeScannerView.setTorchOn();
        }
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_on);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_off);
    }

    public void stopScanning(View view) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("SCANNED_BARCODE_LIST", scannedBarcodeHistory);
        intent.putExtra("LOG_MODEL", logDetails);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("SCANNED_BARCODE_LIST", scannedBarcodeHistory);
        intent.putExtra("LOG_MODEL", logDetails);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

