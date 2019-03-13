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
import com.surya.onspot.QRresponse.NewShipmentDetailModel;
import com.surya.onspot.R;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewShipmentRemoveScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private Activity activity = null;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    Button buttonStopScanning;
    private BeepManager beepManager = null;
    private ArrayList<String> BarcodeList = new ArrayList<String>();
    private ArrayList<String> RemoveBarcodeList = new ArrayList<String>();
    private ArrayList<NewShipmentDetailModel> newShipmentDetailModelArrayList = new ArrayList<>();
    private boolean isFlashOn = false;
    private DatabaseHelper objDatabaseHelper = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        buttonStopScanning = findViewById(R.id.button_stop_scanning);
        beepManager = new BeepManager(this);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        activity = NewShipmentRemoveScannerActivity.this;
        objDatabaseHelper = new DatabaseHelper(activity);

        newShipmentDetailModelArrayList = objDatabaseHelper.getShipmentSystemBarcode();

        for (int index = 0; index < newShipmentDetailModelArrayList.size(); index++) {
            BarcodeList.add(String.valueOf(newShipmentDetailModelArrayList.get(index).getShipmentCode()));
        }


        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(final BarcodeResult result) {
                capture.onPause();
                beepManager.playBeepSoundAndVibrate();

                if (result.getText() == null) {
                    capture.onPause();
                    return;
                } else {
                    if (isBarcodeExistsInSystem(result.getText())) {
                        if (isBarcodeExists(result.getText())) {
                            objDatabaseHelper.updateShipment(result.getText(), "1");
                            Utils.showToast(activity, "Removed Successfully");
                            RemoveBarcodeList.add(result.getText());
                        } else if(RemoveBarcodeList.contains(result.getText())){
                            Utils.showAlert(activity, new Utils.InterfaceAlertDissmiss() {
                                @Override
                                public void onPositiveButtonClick() {

                                }
                            }, "Qr-Code Already Removed. zPlease try different Qr-Code. ", "Alert!!");
                        }else {
                            Utils.showAlert(activity, new Utils.InterfaceAlertDissmiss() {
                                @Override
                                public void onPositiveButtonClick() {

                                }
                            }, "Qr-Code not scanned. Please try different Qr-Code. ", "Alert!!");
                        }
                    } else {
                        Utils.showAlert(activity, new Utils.InterfaceAlertDissmiss() {
                            @Override
                            public void onPositiveButtonClick() {

                            }
                        }, "Qr-Code does not exists in system.", "Alert");
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
        return BarcodeList.contains(strBarcode);
    }

    private boolean isBarcodeExists(String barcode) {
        return objDatabaseHelper.getCurrentShipmentDetails(barcode);
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
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

