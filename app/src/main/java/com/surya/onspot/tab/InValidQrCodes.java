package com.surya.onspot.tab;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.surya.onspot.R;
import com.surya.onspot.adapters.MyInvalidCardAdapter;
import com.surya.onspot.tables.TblInvalidQRresult;
import com.surya.onspot.utils.CustomeQRcode;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;


public class InValidQrCodes extends Fragment {

    public CardView view3;
    String QrCode;
    String ResponseTime;
    int KeyId;
    String ImageURl;
    ArrayList<CustomeQRcode> list2 = new ArrayList<>();
    View rootView;
    Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView textViewNoDataAvailable;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.valid_list_group, container, false);
        textViewNoDataAvailable = rootView.findViewById(R.id.textView_no_data);
        textViewNoDataAvailable.setVisibility(View.VISIBLE);
        mContext = getActivity();
        fetchFromDatabase();
        initViews();
        return rootView;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Context context = LocaleHelper.setLocale(mContext, Utils.getPreference(mContext, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        textViewNoDataAvailable.setText(resources.getString(R.string.no_data_available));
        fetchFromDatabase();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private void initViews() {
        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        list2.clear();
        mAdapter = new MyInvalidCardAdapter(mContext, getActivity(), list2);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private void fetchFromDatabase() {
        list2.clear();
        Cursor cr1 = getActivity().getContentResolver().query(TblInvalidQRresult.BASE_CONTENT_URI, null,
                null, null, TblInvalidQRresult.KEY_ID + " DESC");
        {
            while (cr1.moveToNext()) {
                {
                    QrCode = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.Code));
                    ResponseTime = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.ResponseTime));
                    KeyId = cr1.getInt(cr1.getColumnIndex(TblInvalidQRresult.KEY_ID));
                    ImageURl = cr1.getString(cr1.getColumnIndex(TblInvalidQRresult.ImageURI));
                    CustomeQRcode customeQRcode = new CustomeQRcode();
                    customeQRcode.setCode(QrCode);
                    customeQRcode.setTime(ResponseTime);
                    customeQRcode.setKeyId(KeyId);
                    customeQRcode.setImageURl(ImageURl);
                    list2.add(customeQRcode);
                    textViewNoDataAvailable.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}



