package com.surya.onspot.tab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surya.onspot.R;
import com.surya.onspot.tables.TblInvalidQRresult;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListInvalidFragment extends Fragment implements ListView.OnItemClickListener, OnClickListener {

    public Button ScanButton;
    public Button Report;
    public ListView view4;
    String ResponseMessage;
    String ResponseType;
    String QrCode;
    String ResponseTime;
    List<String> list3;
    View rootView;
    List<Integer> x = new ArrayList<Integer>();
    Context cntx;
    private Button DeleteButton;
    private ImageView scannedQRImage;
    private int id;
    private ArrayAdapter<String> mListAdapter1;
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.invalidlist, container, false);
        scannedQRImage = rootView.findViewById(R.id.scannedQRImagevalid);
        scannedQRImage.setVisibility(View.GONE);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        cntx = getActivity();

        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();

        list3 = new ArrayList<String>();
        fetchFromDatabase();
        initViews();

        return rootView;
    }

    private void initViews() {
        view4 = rootView.findViewById(R.id.lvExpvalid);
        //		view4.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.valid_list_xml, R.id.valid_qrcodelist_name,list3));
        mListAdapter1 = (new ArrayAdapter<String>(getActivity(), R.layout.valid_list_xml, R.id.valid_qrcodelist_name, list3));
        view4.setAdapter(mListAdapter1);
        ScanButton = rootView.findViewById(R.id.scanbutton);
        ScanButton.setOnClickListener(this);
        DeleteButton = rootView.findViewById(R.id.deletebutton);
        DeleteButton.setOnClickListener(this);
//		Report =(Button)rootView.findViewById(R.id.report);
//		Report.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
//		Fragment mfrag = new ReportQrcode();
//		Bundle bundle = new Bundle();
//		bundle.putInt("Id", x.get(position));
//		mfrag.setArguments(bundle);
//		switchFragment(mfrag);

    }


    private void fetchFromDatabase() {
        Bundle mBundle = getArguments();
        id = mBundle.getInt("QrCodeDetails", 0);

        Cursor cr1 = getActivity().getContentResolver().query(TblInvalidQRresult.BASE_CONTENT_URI, null,
                TblInvalidQRresult.KEY_ID + " =? ", new String[]{String.valueOf(id)}, TblInvalidQRresult.KEY_ID + " DESC");

        {

            while (cr1.moveToNext()) {
                QrCode = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.Code));
                ResponseTime = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.ResponseTime));
                ResponseMessage = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.COL_RESPONSE_MESSAGE));
                ResponseType = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.COL_RESPONSE_TYPE));
                x.add(cr1.getInt(cr1.getColumnIndex(TblInvalidQRresult.KEY_ID)));

                list3.clear();
                list3.add(resources.getString(R.string.scan_date) + " : " + cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.ResponseTime)));
                list3.add(resources.getString(R.string.scan_response) + " : " + cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.COL_RESPONSE_MESSAGE)));
                list3.add(resources.getString(R.string.scan_status) + " : " + resources.getString(R.string.scan_invalid));
                list3.add(resources.getString(R.string.qr_code) + " :  " + cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.Code)));
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
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + ResponseMessage + "\n" + "ResponseTime:" + ResponseTime + "\n" + "QrCode:" + QrCode + "\n" + "ResponseType:" + ResponseType);
                // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.setType("message/rfc822");
                PackageManager pm = getActivity().getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.app_name));
                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++) {

                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if (packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                    } else if (packageName.contains("com.whatsapp") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if (packageName.contains("com.whatsapp")) {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                            intent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + ResponseMessage + "\n" + "ResponseTime:" + ResponseTime + "\n" + "QrCode:" + QrCode + "\n" + "ResponseType:" + ResponseType);
                        } else if (packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Details");
                            intent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + ResponseMessage + "\n" + "ResponseTime:" + ResponseTime + "\n" + "QrCode:" + QrCode + "\n" + "ResponseType:" + ResponseType);
                        } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                            intent.putExtra(Intent.EXTRA_TEXT, "Response Message:" + ResponseMessage + "\n" + "ResponseTime:" + ResponseTime + "\n" + "QrCode:" + QrCode + "\n" + "ResponseType:" + ResponseType);
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
        getActivity().getContentResolver().delete(TblInvalidQRresult.BASE_CONTENT_URI, TblInvalidQRresult.KEY_ID + "=?", new String[]{(String.valueOf(id))});
        Toast.makeText(getActivity(), "Record Deleted Successfully", Toast.LENGTH_LONG).show();
        mListAdapter1.notifyDataSetChanged();
        view4.setAdapter(mListAdapter1);

    }
}



