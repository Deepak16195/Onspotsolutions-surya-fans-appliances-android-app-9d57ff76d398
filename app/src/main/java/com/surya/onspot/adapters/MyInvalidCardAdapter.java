package com.surya.onspot.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.surya.onspot.R;
import com.surya.onspot.tab.ListInvalidFragment;
import com.surya.onspot.utils.CustomeQRcode;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyInvalidCardAdapter extends RecyclerView.Adapter<MyInvalidCardAdapter.ViewHolder> {

    int KeyId;
    private ArrayList<CustomeQRcode> mList;
    private FragmentActivity mFragment;
    private Context mContext;


    public MyInvalidCardAdapter(Context context, FragmentActivity fragment, ArrayList<CustomeQRcode> list) {
        mFragment = fragment;
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explicitmai_activity, parent, false);
        ViewHolder vhItem = new ViewHolder(view);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = LocaleHelper.setLocale(mContext, Utils.getPreference(mContext, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();

        final SpannableString ssQrCode = new SpannableString(resources.getString(R.string.qr_code) + " : ");
        ssQrCode.setSpan(new StyleSpan(Typeface.BOLD), 0, ssQrCode.length(), 0);
        holder.scan_name.setText(ssQrCode);
        holder.scan_name.append(mList.get(position).getCode());
        final SpannableString ssScanDate = new SpannableString(resources.getString(R.string.scan_date) + " : ");
        ssScanDate.setSpan(new StyleSpan(Typeface.BOLD), 0, ssScanDate.length(), 0);
        holder.scan_date.setText(ssScanDate);
        holder.scan_date.append(mList.get(position).getTime());
        KeyId = mList.get(position).getKeyId();
/*        if (mList.get(position).getImageURl() != null) {
            Picasso.with(mContext).load(mList.get(position).getImageURl().replaceAll(" ", "%20")).error(R.drawable.invalid_image).placeholder(R.drawable.invalid_image).into(holder.thumbnail);
        }*/
        Picasso.with(mContext).load(R.drawable.invalid_image).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void switchFragment(Fragment mTarget, int pos, boolean animate) {

        Bundle mBundle = new Bundle();
        mBundle.putInt("QrCodeDetails", mList.get(pos).getKeyId());
        mTarget.setArguments(mBundle);
        String backStateName = mTarget.getClass().getName();
        FragmentManager manager = mFragment.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        boolean isSameFragment = false;
        if (manager.findFragmentByTag(backStateName) != null) {
            isSameFragment = manager.findFragmentByTag(backStateName).isAdded();
        }

        if (!fragmentPopped && !isSameFragment) {
            FragmentTransaction mFragmentTransaction = manager.beginTransaction();
            if (animate) {
                mFragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
            mFragmentTransaction.add(R.id.fragment_root, mTarget, backStateName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(backStateName)
                    .commit();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView scan_name, scan_date;
        public CircleImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            scan_name = itemView.findViewById(R.id.lblListHeader1);
            scan_date = itemView.findViewById(R.id.scandate);
            thumbnail = itemView.findViewById(R.id.thumnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(new ListInvalidFragment(), getAdapterPosition(), true);
                }
            });
        }
    }
}