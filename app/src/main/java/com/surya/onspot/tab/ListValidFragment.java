package com.surya.onspot.tab;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.tables.TblOnspotQrResults;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListValidFragment extends Fragment implements ListView.OnItemClickListener, OnClickListener {

    public Button ScanButton;
    public ListView view3;
    public ListView view4;
    String ResponseMessage;
    String ResponseType;
    String QrCode;
    String CPO_NO;
    String BrandName;
    String MillName;
    String ImageUrl;
    String ProcurementAgency;
    String ManufacturingLocation;
    String ProductRefrenceNumber;
    String DispatchDate;
    String DestinationAgency;
    String DestinationPoint;
    String DestinationState;
    String Mrp;
    String Cpo_Date;
    String So_Number_Date;
    String statutory_message;
    String ResponseTime;
    String ResponseData = "";
    String SerialNo;
    List<String> list3;
    View rootView;
    DisplayImageOptions options;
    private Button DeleteButton;
    private ImageView scannedQRImage;
    private int id;
    private ArrayAdapter<String> mListAdapter;
    private StringBuilder intentData;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.valid_list, container, false);
        scannedQRImage = rootView.findViewById(R.id.scannedQRImagevalid);
        scannedQRImage.setVisibility(View.GONE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.imagenot_available)
                .showImageForEmptyUri(R.drawable.imagenot_available)
                .showImageOnFail(R.drawable.imagenot_available)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        list3 = new ArrayList<String>();
        fetchFromDatabase();
        initViews();
        return rootView;
    }

    private void initViews() {
        view4 = rootView.findViewById(R.id.lvExpvalid);
        mListAdapter = (new ArrayAdapter<String>(getActivity(), R.layout.valid_list_xml, R.id.valid_qrcodelist_name, list3));
        view4.setAdapter(mListAdapter);
        Utils.out("URL : " + ImageUrl);
        ImageLoader.getInstance().displayImage(API_CONSTANTS.HOST_URL + ImageUrl, scannedQRImage, options);
        ScanButton = rootView.findViewById(R.id.scanbutton);
        ScanButton.setOnClickListener(this);
        DeleteButton = rootView.findViewById(R.id.deletebutton);
        DeleteButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stu

    }

    public void DownloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        scannedQRImage = rootView.findViewById(R.id.scannedQRImagevalid);
        try {

            URL url = new URL(ImageUrl);//"http://192.xx.xx.xx/mypath/img1.jpg
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

    private void fetchFromDatabase() {
        Bundle mBundle = getArguments();
        id = mBundle.getInt("QrCodeDetails", 0);
        Cursor cr1 = getActivity().getContentResolver().query(TblOnspotQrResults.BASE_CONTENT_URI, null,
                TblOnspotQrResults.KEY_ID + " =? ", new String[]{String.valueOf(id)}, TblOnspotQrResults.KEY_ID + " DESC");

        {

            while (cr1.moveToNext()) {
                QrCode = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Code));
                ResponseTime = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.ResponseTime));
                ResponseData = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.ResponseData));
                ResponseMessage = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.COL_RESPONSE_MESSAGE));
                ResponseType = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.COL_RESPONSE_TYPE));
                CPO_NO = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.CPO_NO));
                BrandName = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Brand_Name));
                MillName = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Mill_Name));
                ProcurementAgency = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Procurement_agency));
                ManufacturingLocation = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Manufacturing_Location));
                ProductRefrenceNumber = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Product_Refrence_Number));
                DispatchDate = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Dispatch_Date));
                DestinationAgency = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_Agency));
                DestinationPoint = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_Point));
                DestinationState = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_State));
                Mrp = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.MRP));
                statutory_message = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Statutory_warning));
                So_Number_Date = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.So_number_date));
                Cpo_Date = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Cpo_Date));
                SerialNo = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Serial_No));
                ImageUrl = cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.ImageUrl));

                list3.clear();

                try {
                    JSONArray objJsonArray = new JSONArray(ResponseData);
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        JSONArray array = objJsonArray.getJSONArray(index);
                        StringBuilder row_data = new StringBuilder();
                        for (int innerIndex = 0; innerIndex < array.length(); innerIndex++) {
                            if (innerIndex == 0) {
                                SpannableString spannableString = new SpannableString(array.get(innerIndex).toString());
                                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, array.get(innerIndex).toString().length(), 0);
                                row_data.append(spannableString + " : \n");
                            } else {
                                row_data.append(array.get(innerIndex).toString());
                            }
                        }
                        list3.add(row_data.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*list3.add("Scan Date : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.ResponseTime)));
                list3.add("Response : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.COL_RESPONSE_MESSAGE)));
                list3.add("Scan Status : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.COL_RESPONSE_TYPE)));
                list3.add("QR Code :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Code)));
                list3.add("So Number Date : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.So_number_date)));
                list3.add("CPO Date :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Cpo_Date)));
                list3.add("CPO Number :    " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.CPO_NO)));
                list3.add("Brand  :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Brand_Name)));
                list3.add("Manufacturer Name  :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Mill_Name)));
                list3.add("Procurement Agency :    " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Procurement_agency)));
                list3.add("Manufacturing Location :     " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Manufacturing_Location)));
                list3.add("Product Reference Number :    " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Product_Refrence_Number)));
                list3.add("Dispatch Date :    " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Dispatch_Date)));
                list3.add("Destination Agency : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_Agency)));
                list3.add("Destination Point : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_Point)));
                list3.add("Destination State :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Destination_State)));
                list3.add("MRP :  " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.MRP)));
                list3.add("Serial Number : " + cr1.getString(cr1.getColumnIndex(TblOnspotQrResults.Serial_No)));*/
                /*if (statutory_message != null && !statutory_message.equals("N/A")) {
                    list3.add("Statutory Warning  :  " + statutory_message);
                }*/
                scannedQRImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanbutton:
                Resources resources = getActivity().getResources();
                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + ResponseMessage + "\n" + "ResponseTime:" + ResponseTime + "\n" + "QrCode:" + QrCode + "\n" + "ResponseType:" + ResponseType + "\n" + "CPO Number:" + CPO_NO + "\n" + "Brand Name:" + BrandName + "\n" + "Manufacturer Name:" + MillName + "\n" + "Procurement Agency:" + ProcurementAgency + "\n" + "Manufacturing Location:" + ManufacturingLocation + "\n" + "ProductRefrenceNumber:" + ProductRefrenceNumber + "\n" + "Dispatch Date:" + DispatchDate + "\n" + "Destination Agency:" + DestinationAgency + "\n" + "Destination Point:" + DestinationPoint + "\n" + "Destination State:" + DestinationState + "\n" + "MRP:" + Mrp + "\n" + "Serial Number:" + SerialNo);
                emailIntent.setType("message/rfc822");
                PackageManager pm = getActivity().getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.app_name));
                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

                try {
                    JSONArray objJsonArray = new JSONArray(ResponseData);
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
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                        }
                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }
                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                getActivity().startActivity(openInChooser);
                break;
            case R.id.deletebutton:
                deleteInDatabase();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
    }

    private void deleteInDatabase() {
        getActivity().getContentResolver().delete(TblOnspotQrResults.BASE_CONTENT_URI, TblOnspotQrResults.KEY_ID + "=?", new String[]{(String.valueOf(id))});
        Toast.makeText(getActivity(), "Record Deleted Successfully", Toast.LENGTH_LONG).show();
        mListAdapter.notifyDataSetChanged();
        view4.setAdapter(mListAdapter);

    }


//	private void switchFragment(Fragment mTarget){
//		getActivity().getSupportFragmentManager().beginTransaction()
//		.add(R.id.fragment_root, mTarget)
//		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//		.addToBackStack("HomeScreen")
//		.commit();
//	}
//
//	private void switchFragmentNew(Fragment mTarget){
//
//		String backStateName = mTarget.getClass().getName();
//		FragmentManager manager = getActivity().getSupportFragmentManager();
//		boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
//		boolean samefrag = false;
//		if(manager.findFragmentByTag(backStateName) != null){
//			samefrag = manager.findFragmentByTag(backStateName).isAdded();
//		}
//
//		if(!fragmentPopped && !samefrag)
//		{
//			FragmentTransaction ft = manager.beginTransaction();
//			ft.replace(R.id.fragment_root, mTarget, backStateName)
//			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//			.addToBakStack(backStateName)
//			.commit();
//		}
//	}

}



