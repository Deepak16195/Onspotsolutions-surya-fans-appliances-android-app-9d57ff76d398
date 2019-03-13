package com.surya.onspot.locationprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.services.GPSTracker;
import com.surya.onspot.services.InterfaceServiceCall;
import com.surya.onspot.services.ServiceCalls;
import com.surya.onspot.tab.ReportQrcode;
import com.surya.onspot.tables.TblInvalidQRresult;
import com.surya.onspot.tables.TblOnspotQrResults;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LocationMainActivity extends Activity implements InterfaceServiceCall, OnClickListener {
    static private final String URL = "http://www.google.com";
    //Strings
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    public String serverURL;
    public ProgressDialog mbar;
    public JSONObject jsonObject;
    protected LocationManager locationManager;
    Button btnFusedLocation;
    TextView tvLocation;
    Context cntx;
    ListView listViewDocuments;
    ListView listViewSessionDocs;
    List<SpannableString> list;
    List<SpannableString> list1;
    String So_Number_Date;
    String CPO_No = "";
    String Cpo_Date = "";
    String Brand_Name = "";
    String procurement_agency = "";
    String Mill_Name = "";
    String Manufacturing_Location = "";
    String Product_Refrence_Number = "";
    String Dispatch_Date = "";
    String Destination_Agency = "";
    String Destination_State = "";
    String Destination_Point = "";
    String Statutory_warning = "";
    String MRP = "";
    String AppUserId = "";
    String Serial_No = "";
    String prod_img_url = "";
    String created_at = "";
    String brand_image_url = "";
    String qrCode = "";
    String responseMessage = "";
    String mobile_number = "";
    String statutory_message = "";
    String responseType = "";
    String[] DateTime;
    String url = "";
    String SerialNo = "";
    Boolean flag4 = false;
    GPSTracker gpsTracker = null;
    boolean isGPSEnabled = false;
    DisplayImageOptions options;
    private Button ScanButton;
    private Button Report;
    private Button Search;
    private ProgressBar progressBar;
    private String flag;
    private String flag1;
    private ImageView scannedQRImage1;
    private String QRresult;
    private Location loca;
    private ConnectionDetectorActivity cd;
    private ImageView scannedQRImage;
    private TextView onspotTag;
    private TextView invalidTAG;
    private Resources resources;
    private String responseData = "[]";
    private StringBuilder intentData;
    private TextView textViewCongratsMessage = null;
    private String earnedPoint = "0";
    private DatabaseHelper objDatabaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        objDatabaseHelper = new DatabaseHelper(LocationMainActivity.this);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        setContentView(R.layout.success_qr_code);
        cntx = this;

        final Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();

        if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.onspot_logo_for_app_footer);
        } else {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.onspot_logo_for_app_footer);
        }


        //		invalidTAG = (TextView) findViewById(R.id.invalidTAG);
        //		invalidTAG.setVisibility(View.GONE);

        textViewCongratsMessage = findViewById(R.id.textView_congrats_message);

        scannedQRImage = findViewById(R.id.scannedQRImage);
        scannedQRImage.setVisibility(View.GONE);
        scannedQRImage1 = findViewById(R.id.scannedQRImage1);
        scannedQRImage1.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        ScanButton = findViewById(R.id.scanbuttonloc);
        ScanButton.setVisibility(View.GONE);
        Report = findViewById(R.id.reportbutton);
        Report.setVisibility(View.GONE);
        Search = findViewById(R.id.urllink);
        Search.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mbar = new ProgressDialog(cntx);
        mbar.setMessage("Verifying your scan");
        mbar.show();
        mbar.setCanceledOnTouchOutside(false);
        mbar.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        list = new ArrayList<SpannableString>();
        list1 = new ArrayList<SpannableString>();
        cd = new ConnectionDetectorActivity(this);
        ScanButton = findViewById(R.id.scanbuttonloc);
        ScanButton.setOnClickListener(this);
        Report = findViewById(R.id.reportbutton);
        Report.setOnClickListener(this);
        Search = findViewById(R.id.urllink);
        Search.setOnClickListener(this);
        QRresult = getIntent().getStringExtra("QR");
        tvLocation = findViewById(R.id.tvLocation);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        locationManager = (LocationManager) cntx.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final GetCurrentLocation lListener = new GetCurrentLocation(LocationMainActivity.this);
        lListener.startGettingLocation(new GetCurrentLocation.getLocation() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    loca = location;
                    lListener.stopGettingLocation();
                    jsonObject = new JSONObject();
                    try {
                        Utils.out("LAT LONG : " + loca.getLatitude() + " : " + loca.getLongitude());
//                        jsonObject.put(API_CONSTANTS.USER_Id_KEY, Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
                        jsonObject.put(API_CONSTANTS.QR_CODE_KEY, QRresult);
                        jsonObject.put(API_CONSTANTS.LATITUDE_KEY, String.valueOf(loca.getLatitude()));
//                        jsonObject.put(API_CONSTANTS.LATITUDE_KEY, String.valueOf("19.0215338"));
                        jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, String.valueOf(loca.getLongitude()));
//                        jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, String.valueOf("73.0163664"));
                        jsonObject.put(API_CONSTANTS.ACCURACY_KEY, String.valueOf(loca.getAccuracy()));
//                        jsonObject.put(API_CONSTANTS.ACCURACY_KEY, String.valueOf("15"));
                        jsonObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
//                        jsonObject.put("language", Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en"));
                        new ServiceCalls(cntx, LocationMainActivity.this, (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_SCAN_QR, jsonObject.toString(),
                                resources.getString(R.string.loadertext), 1).execute(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    jsonObject = new JSONObject();
                    try {
//                        jsonObject.put(API_CONSTANTS.USER_Id_KEY, Utils.getPreference(cntx, PreferenceKeys.USERNAMEID, ""));
                        jsonObject.put(API_CONSTANTS.QR_CODE_KEY, QRresult);
                        jsonObject.put(API_CONSTANTS.LATITUDE_KEY, "0");
                        jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, "0");
                        jsonObject.put(API_CONSTANTS.ACCURACY_KEY, "0");
                        jsonObject.put("auth_token", new DatabaseHelper(context).getAuthToken());
                      //  jsonObject.put("language", Utils.getPreference(context, PreferenceKeys.KEY_LANGUAGE, "en"));

                        new ServiceCalls(cntx, LocationMainActivity.this, (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_SCAN_QR, jsonObject.toString(),
                                getString(R.string.loadertext), 1).execute(1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void DownloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        scannedQRImage = findViewById(R.id.scannedQRImage);
        try {

            URL url = new URL(prod_img_url);//"http://192.xx.xx.xx/mypath/img1.jpg
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();

            in = con.getInputStream();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
            scannedQRImage.setImageBitmap(bmp);


        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    @Override
    public void onResponseReceived(Bundle result) {
        // TODO Auto-generated method stub
        Log.d("resp", String.valueOf(result));
        if (result == null)
            return;
        if (cd.isConnectingToInternet()) {

            if (!result.getString("Message").equalsIgnoreCase("OK")) {
                Utils.showToast(LocationMainActivity.this, "Check your internet connection strength");
                /*Intent i = new Intent(LocationMainActivity.this, HomeScreen.class);
                startActivity(i);*/
                finish();
                //				listViewDocuments.setVisibility(View.GONE);

                return;
            }
            if (result.getString("Response").equals("")) {
                Utils.showToast(LocationMainActivity.this, "Check your internet connection strength");
                /*Intent i = new Intent(LocationMainActivity.this, HomeScreen.class);
                startActivity(i);*/
                finish();
                //				listViewDocuments.setVisibility(View.GONE);
                //						txtViewNoDocuments.setText(getString(R.string.noData));
                return;
            }

            try {

                JSONObject scanQr = new JSONObject(result.getString("Response"));
                boolean success = scanQr.getBoolean("success");

                if (success) {
                    prod_img_url = scanQr.getString("product_image");
                    responseMessage = scanQr.getString("responseMessage");
                    JSONArray objJsonArray = new JSONArray(scanQr.getString("responseHash"));

                    responseData = objJsonArray.toString();

                    Utils.out("RESPONSE :::" + objJsonArray);


                    for (int index = 0; index < objJsonArray.length(); index++) {
                        JSONArray array = objJsonArray.getJSONArray(index);
                        Brand_Name = objJsonArray.getJSONArray(0).get(1).toString();
                        StringBuilder row_data = new StringBuilder();
                        for (int innerIndex = 0; innerIndex < array.length(); innerIndex++) {
                            if (innerIndex == 0) {
                                SpannableString spannableString = new SpannableString(array.get(innerIndex).toString());
                                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, array.get(innerIndex).toString().length(), 0);
                                row_data.append(spannableString + " : \n");
                                if (array.get(innerIndex).toString().contains("Point")) {
                                    updateCongratsTextView(array.get(innerIndex + 1).toString());
                                }
                            } else {
                                row_data.append(array.get(innerIndex).toString());
                            }
                        }
                        list.add(SpannableString.valueOf(row_data));
                    }
                    scannedQRImage1.setVisibility(View.GONE);
                    scannedQRImage.setVisibility(View.VISIBLE);
                    Search.setVisibility(View.INVISIBLE);
                    Report.setVisibility(View.INVISIBLE);
                    Search.setVisibility(View.INVISIBLE);
                    ScanButton.setVisibility(View.VISIBLE);

                    ListView view = findViewById(R.id.scanedQRListView);
                    if (scanQr.optString("warranty_expired").equals("null")) {
                        View activateWarrantyView = getLayoutInflater().inflate(R.layout.row_activate_warrenty_layout, null);
                        activateWarrantyView.findViewById(R.id.button_activate_warranty).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ACTIVATE WARRANTY API CALL HERE
                                callStartWarranty(QRresult);
                            }
                        });
                        view.addFooterView(activateWarrantyView);
                    } else {
                        textViewCongratsMessage.setVisibility(TextView.VISIBLE);
                        textViewCongratsMessage.setText(scanQr.optString("warranty_expired"));
//                        Utils.showToast(LocationMainActivity.this, "Warranty Expires On " + scanQr.optString("warranty_expired"));
                    }
                    view.setAdapter(new ArrayAdapter<SpannableString>(this, R.layout.list_xml, R.id.qrcodelist_name, list));
                    ImageLoader.getInstance().displayImage(API_CONSTANTS.HOST_URL + prod_img_url, scannedQRImage, options);
                    addToDatabase();
                } else {
                    scannedQRImage.setVisibility(View.GONE);
                    responseMessage = scanQr.getString("responseMessage");
                    list1.add(SpannableString.valueOf(responseMessage));
                    list1.add(SpannableString.valueOf(resources.getString(R.string.scan_status) + " : " + resources.getString(R.string.scan_invalid)));
                    list1.add(SpannableString.valueOf(resources.getString(R.string.qr_code) + " :  " + QRresult));
                    list1.add(SpannableString.valueOf(resources.getString(R.string.scan_date) + " :  " + Utils.getCurrentDate()));
                    ListView view1 = findViewById(R.id.scanedQRListView);

                   /* if (scanQr.optString("warranty_expired").equals("null")) {
                        View activateWarrantyView = getLayoutInflater().inflate(R.layout.row_activate_warrenty_layout, null);
                        activateWarrantyView.findViewById(R.id.button_activate_warranty).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ACTIVATE WARRANTY API CALL HERE
                                callStartWarranty(QRresult);
                            }
                        });
                        view1.addFooterView(activateWarrantyView);
                    } else {
                        Utils.showToast(LocationMainActivity.this, "Warranty Expires On" + scanQr.optString("warranty_expired"));
                    }*/
                    view1.setAdapter(new ArrayAdapter<SpannableString>(this, R.layout.list_xml, R.id.qrcodelist_name, list1));
                    url = QRresult;
//					if (url.startsWith(HTTP)  || url.startsWith(HTTPS))
//					{
                    flag4 = true;
                    Search.setVisibility(View.GONE);
//					}
                    ScanButton.setVisibility(View.GONE);
                    Report.setVisibility(View.GONE);
                    //					Search.setVisibility(View.VISIBLE);
                    scannedQRImage.setVisibility(View.GONE);
                    scannedQRImage1.setVisibility(View.VISIBLE);
//                    addToDatabase1();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(LocationMainActivity.this, "Please check your internet connection",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(LocationMainActivity.this, HomeScreen.class);
            startActivity(i);

            setResult(10);
            finish();
        }
        mbar.dismiss();
    }

    private void callStartWarranty(String qr_code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
            jsonObject.put("qr_code", qr_code);
            jsonObject.put(API_CONSTANTS.LATITUDE_KEY, String.valueOf(loca.getLatitude()));
            jsonObject.put(API_CONSTANTS.LONGITUDE_KEY, String.valueOf(loca.getLongitude()));
            jsonObject.put(API_CONSTANTS.ACCURACY_KEY, String.valueOf(loca.getAccuracy()));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("==", "========= FAIL TO CREATE JSON ==========");
        }

        ServiceCalls objServiceCall = new ServiceCalls(cntx, new InterfaceServiceCall() {
            @Override
            public void onResponseReceived(Bundle result) {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString("Response"));
                    if (jsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                        Utils.showToast(LocationMainActivity.this, jsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
                        finish();
                    } else {
                        Utils.showToast(LocationMainActivity.this, jsonObject.optString(API_CONSTANTS.RESPONSE_MESSAGE));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, (Utils.getPreference(this, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_START_WARRANTY, jsonObject.toString(),
                "Activating warranty. Please wait...", 0);

       /* ServiceCalls objServiceCall = new ServiceCalls(LocationMainActivity.this, API_CONSTANTS.API_START_WARRANTY,
                jsonObject.toString(), "Activating warranty. Please wait...", 0, new AsyncResponseActivity() {

            @Override
            public void myAsyncResponse(String result) {

            }
        });*/
        if (cd.isConnectingToInternet()) {
            objServiceCall.execute(1);
        } else {
            showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void showNetworkFailDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LocationMainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

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

    private void updateCongratsTextView(String strEarnedPoints) {
        textViewCongratsMessage.setVisibility(TextView.GONE);
        if (strEarnedPoints.length() > 0) {
            textViewCongratsMessage.setVisibility(TextView.VISIBLE);
            textViewCongratsMessage.setText("Congratulations you have earned " + strEarnedPoints + " points");
        }
    }

    private void addToDatabase() {
        ContentValues cv = new ContentValues();
        cv.put(TblOnspotQrResults.ResponseTime, Utils.getCurrentDate());
        cv.put(TblOnspotQrResults.ImageUrl, prod_img_url);
        cv.put(TblOnspotQrResults.ResponseData, responseData);
        cv.put(TblOnspotQrResults.COL_RESPONSE_MESSAGE, responseMessage);
        cv.put(TblOnspotQrResults.COL_RESPONSE_TYPE, "");
        cv.put(TblOnspotQrResults.Code, QRresult);
        cv.put(TblOnspotQrResults.CPO_NO, CPO_No);
        cv.put(TblOnspotQrResults.Cpo_Date, Cpo_Date);
        cv.put(TblOnspotQrResults.So_number_date, So_Number_Date);
        cv.put(TblOnspotQrResults.Brand_Name, Brand_Name);
        cv.put(TblOnspotQrResults.Mill_Name, Mill_Name);
        cv.put(TblOnspotQrResults.Statutory_warning, statutory_message);
        cv.put(TblOnspotQrResults.Procurement_agency, procurement_agency);
        cv.put(TblOnspotQrResults.Manufacturing_Location, Manufacturing_Location);
        cv.put(TblOnspotQrResults.Product_Refrence_Number, Product_Refrence_Number);
        cv.put(TblOnspotQrResults.Dispatch_Date, Dispatch_Date);
        cv.put(TblOnspotQrResults.Destination_Agency, Destination_Agency);
        cv.put(TblOnspotQrResults.Destination_State, Destination_State);
        cv.put(TblOnspotQrResults.Destination_Point, Destination_Point);
        cv.put(TblOnspotQrResults.MRP, MRP);
        cv.put(TblOnspotQrResults.Serial_No, Serial_No);
        getContentResolver().insert(TblOnspotQrResults.BASE_CONTENT_URI, cv);
        cv.clear();
    }

    private void addToDatabase1() {
        ContentValues cv1 = new ContentValues();
        cv1.put(TblInvalidQRresult.ResponseTime, Utils.getCurrentDate());
        cv1.put(TblInvalidQRresult.COL_RESPONSE_MESSAGE, responseMessage);
        cv1.put(TblInvalidQRresult.COL_RESPONSE_TYPE, responseType);
        cv1.put(TblInvalidQRresult.Code, QRresult);
        cv1.put(TblInvalidQRresult.ImageURI, "");
        getContentResolver().insert(TblInvalidQRresult.BASE_CONTENT_URI, cv1);
        cv1.clear();
    }

    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.scanbuttonloc:
                Resources resources = getResources();
                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
//			 Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + responseMessage + "\n" + "ResponseTime:" + Arrays.toString(DateTime) + "\n" + "QrCode:" + qrCode + "\n" + "ResponseType:" + responseType + "\n" + "CPO Number:" + CPO_No + "\n" + "So_number_Date:" + So_Number_Date + "\n" + "Brand Name:" + Brand_Name + "\n" + "Manufacturer Name:" + Mill_Name + "\n" + "Procurement Agency:" + procurement_agency + "\n" + "Manufacturing Location:" + Manufacturing_Location + "\n" + "ProductRefrenceNumber:" + Product_Refrence_Number + "\n" + "Dispatch Date:" + Dispatch_Date + "\n" + "Destination Agency:" + Destination_Agency + "\n" + "Destination Point:" + Destination_Point + "\n" + "Destination State:" + Destination_State + "\n" + "MRP:" + MRP + "\n" + "Serial Number:" + Serial_No);
                emailIntent.setType("message/rfc822");
                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.app_name));
                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

                try {
                    JSONArray objJsonArray = new JSONArray(responseData);
                    intentData = new StringBuilder();
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        JSONArray array = objJsonArray.getJSONArray(index);

                        for (int innerIndex = 0; innerIndex < array.length(); innerIndex++) {
                            if (innerIndex == 0) {
                                SpannableString spannableString = new SpannableString(array.get(innerIndex).toString());
                                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, array.get(innerIndex).toString().length(), 0);
                                intentData.append(spannableString + " : \n");
                            } else {
                                intentData.append(array.get(innerIndex).toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < resInfo.size(); i++) {

                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if (packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if (packageName.contains("com.whatsapp") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if (packageName.contains("com.whatsapp")) {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                            intent.putExtra(Intent.EXTRA_TEXT, intentData.toString());
                        } else if (packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                            intent.putExtra(Intent.EXTRA_TEXT, intentData.toString());
                        } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                            intent.putExtra(Intent.EXTRA_TEXT, intentData.toString());
                            intent.setType("message/rfc822");
                        }

                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }
                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
                break;
            case R.id.reportbutton:
                Intent i = new Intent(LocationMainActivity.this, ReportQrcode.class);
                startActivity(i);
                break;
            case R.id.urllink:
                if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(Intent.createChooser(intent, "Chose browser"));
                } else {
                    String uri = "http://www.google.com/search?q=" + url;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}