package com.surya.onspot.qrscanFragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.model.RewardsDashboardModel;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRedeemDetailScreen extends Fragment implements HomeScreen.SnackbarRetryListener {

    private View view = null;
    private Context cntx = null;
    private ListView listView;
    private TextView textViewTitle;

    private ArrayList<ListModel> arrListModel = null;
    private RewardsDashboardModel object = null;
    private ConnectionDetectorActivity cd = null;
    private boolean isSecondTime = false;
    private Resources resources = null;
    private int redeemedPoints = 0;
    private int totalCount = 0;
    private RowRedeemList2Adapter adapter = null;
    private JSONArray objJSONArrayOfSelectedIds = null;


    public FragmentRedeemDetailScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_redeem_detail_listing_screen, container, false);
        cntx = getActivity();
        initializeView();
        return view;
    }

    private void initializeView() {
        listView = view.findViewById(R.id.listView_list2);
        ImageView imageViewTitle = view.findViewById(R.id.imageView_left);
        textViewTitle = view.findViewById(R.id.textView_title);

        view.findViewById(R.id.relativeLayout_bottom_list2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objJSONArrayOfSelectedIds = new JSONArray();
                redeemedPoints = 0;
                for (int index = 0; index < arrListModel.size(); index++) {
                    try {
                        JSONObject json = new JSONObject(arrListModel.get(index).getData());
                        if (json.length() > 0 && arrListModel.get(index).isCheckBoxChecked()) {
                            redeemedPoints = redeemedPoints + json.optInt("point");
                            objJSONArrayOfSelectedIds.put(json.optInt("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (objJSONArrayOfSelectedIds.length() == 0) {
                    Utils.showToast(cntx, "No data to redeem. Please select checkbox to redeem");
                } else if (objJSONArrayOfSelectedIds.length() > 0) {
                    callRedeemAPI(objJSONArrayOfSelectedIds);
                }
                /*Bundle objBundle = new Bundle();
                objBundle.putSerializable("OBJECT", "");
                FragmentRewardsOtp1 fragment = new FragmentRewardsOtp1(objBundle);
                switchFragment(fragment);*/
            }
        });
    }

    private void callRedeemAPI(final JSONArray objJSONArray) {
        JSONObject objInputJSONObject = new JSONObject();
        try {
            objInputJSONObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
            objInputJSONObject.put("brand_id", object.getBrandId());
            objInputJSONObject.put("offer_ids", objJSONArray);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ServiceCalls serviceCall = new ServiceCalls(getActivity(), API_CONSTANTS.API_REWARDS_COMPLETE_REDEEM, objInputJSONObject.toString(),
                resources.getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (jsonObject.length() > 0) {
                        if (jsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                            object.setTitleCount(String.valueOf(totalCount - redeemedPoints));
                            ((HomeScreen) getActivity()).setRedeemData(object);
                            totalCount = totalCount - redeemedPoints;
                            Utils.showToast(cntx, "You have successfully redeemed " + String.valueOf(redeemedPoints) + " points");
                            unCheckAllItems();
                        } else if (!jsonObject.optBoolean(API_CONSTANTS.SUCCESS) && jsonObject.optInt("result_code") == HttpStatus.SC_REQUEST_TIMEOUT) {
                            // SERVER TIME OUT EXCEPTION
                            ((HomeScreen) getActivity()).showSnackbar(getView(), jsonObject.getString("responseMessage"), FragmentRedeemDetailScreen.this, API_CONSTANTS.API_REWARDS_COMPLETE_REDEEM);
                        }
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cd = new ConnectionDetectorActivity(getActivity());
        if (cd.isConnectingToInternet()) {
            serviceCall.execute(1);
        } else {
            showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
        }
    }

    private void unCheckAllItems() {
        for (int index = 0; index < arrListModel.size(); index++) {
            ListModel object = arrListModel.get(index);
            object.setCheckBoxChecked(false);
            adapter.notifyDataSetChanged();
        }
    }

    private void switchFragment(Fragment mTarget) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_root, mTarget)
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isSecondTime) {
            isSecondTime = true;
            Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
            resources = context.getResources();
            ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.reward_title));
            object = ((HomeScreen) getActivity()).getRedeemData();
            if (object != null) {
                if (object.getTitleCount().length() > 0) {
                    totalCount = Integer.parseInt(object.getTitleCount());
                    updateTitleAndCount(Integer.parseInt(object.getTitleCount()));
                }
                if (object.getImageUrl().length() > 0) {
                    Picasso.with(getActivity()).load(object.getImageUrl()).into(((ImageView) view.findViewById(R.id.imageView_left)));
                }
                callListAPI();
            }
        }
    }

    private void updateTitleAndCount(int count) {
        if (object != null) {
            textViewTitle.setText(count + "\n" + object.getTitle());
            String data = (String) textViewTitle.getText();
            SpannableString spannable = new SpannableString(data);
//            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.log_colorPrimary)), 0, String.valueOf(count).length(), 0);
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, String.valueOf(count).length(), 0);
            textViewTitle.setText(spannable);
        }
    }

    private void callListAPI() {
        JSONObject objInputJSONObject = new JSONObject();
        try {
            objInputJSONObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
            objInputJSONObject.put("brand_id", object.getBrandId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ServiceCalls serviceCall = new ServiceCalls(getActivity(), API_CONSTANTS.API_REWARDS_OFFER_LIST, objInputJSONObject.toString(),
                resources.getString(R.string.api_loading_dialog_message_requesting), 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String output) {
                // TODO Auto-generated method stub
                parseJason(output);
            }
        });
        cd = new ConnectionDetectorActivity(getActivity());
        if (cd.isConnectingToInternet()) {
            serviceCall.execute(1);
        } else {
            showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
        }
    }

    //Network fail Dialog
    @SuppressLint("InflateParams")
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
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }

                });
    }

    public boolean isPointValid() {
        int pointCounter = 0;
        for (int index = 0; index < arrListModel.size(); index++) {
            try {
                JSONObject json = new JSONObject(arrListModel.get(index).getData());
                if (json.length() > 0) {
                    if (arrListModel.get(index).isCheckBoxChecked()) {
                        pointCounter = pointCounter + json.optInt("point");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Utils.out("VALIDITY CHECK POINTS COUNT : " + pointCounter);
        Utils.out("VALIDITY CHECK POINTS COUNT : " + totalCount);
        if (pointCounter <= totalCount) {
            updateTitleAndCount(totalCount - pointCounter);
            return true;
        } else {
            return false;
        }
    }

    private void parseJason(String output) {
        try {
            isSecondTime = false;
            JSONObject json = new JSONObject(output);
            if (json.optBoolean(API_CONSTANTS.SUCCESS)) {
                arrListModel = new ArrayList<>();
                JSONArray objJsonArrayOfferList = json.getJSONArray("offer_list");
                if (objJsonArrayOfferList.length() > 0) {
                    arrListModel.clear();
                    adapter = new RowRedeemList2Adapter(cntx);
                    for (int index = 0; index < objJsonArrayOfferList.length(); index++) {
                        arrListModel.add(new ListModel(objJsonArrayOfferList.getJSONObject(index).toString()));
                    }
                    listView.setAdapter(adapter);
                }  // No data found

            } else if (!json.optBoolean(API_CONSTANTS.SUCCESS) && json.optInt("result_code") == HttpStatus.SC_REQUEST_TIMEOUT) {
                // SERVER TIME OUT EXCEPTION
                ((HomeScreen) getActivity()).showSnackbar(getView(), json.getString("responseMessage"), FragmentRedeemDetailScreen.this, API_CONSTANTS.API_REWARDS_OFFER_LIST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRetryClickListener(String apiTag) {
        if (apiTag.equalsIgnoreCase(API_CONSTANTS.API_REWARDS_OFFER_LIST)) {
            try {
                callListAPI();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (apiTag.equalsIgnoreCase(API_CONSTANTS.API_REWARDS_COMPLETE_REDEEM)) {
            try {
                if (objJSONArrayOfSelectedIds != null && objJSONArrayOfSelectedIds.length() > 0)
                    callRedeemAPI(objJSONArrayOfSelectedIds);
                else
                    ((HomeScreen) getActivity()).dismissSnackbar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class RowRedeemList2Adapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        public RowRedeemList2Adapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrListModel.size();
        }

        @Override
        public ListModel getItem(int position) {
            return arrListModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.row_redeem_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag());
            return convertView;
        }

        private void initializeViews(final ListModel object, ViewHolder holder) {
            //TODO implement
            try {
                JSONObject objJsonObject = new JSONObject(object.getData());
                if (objJsonObject != null && objJsonObject.length() > 0) {
                    holder.textViewPaytmText.setText("Rs. " + objJsonObject.optInt("cashback") + " Cashback");
                    holder.textViewRedeemText.setText("Points. " + objJsonObject.optInt("point"));
                    holder.textViewNote.setText(objJsonObject.optString("description"));
                    holder.relativeLayoutSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isPointValid()) {
                                object.setCheckBoxChecked(!object.isCheckBoxChecked());
                                notifyDataSetChanged();
                                if (isPointValid() == false) {
                                    object.setCheckBoxChecked(!object.isCheckBoxChecked());
                                    notifyDataSetChanged();
                                    Utils.showToast(cntx, "You have " + totalCount + " points to redeem");
                                }
                            }
                        }
                    });
                    holder.checkBoxSelection.setChecked(object.isCheckBoxChecked());
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String strPaytmText = holder.textViewPaytmText.getText().toString();
            SpannableString spannablePaytm = new SpannableString(strPaytmText);
            spannablePaytm.setSpan(new ForegroundColorSpan(getResources()
                    .getColor(R.color.log_colorPrimary)), "Rs. ".length(), strPaytmText.length() - 8, 0);
            spannablePaytm.setSpan(new RelativeSizeSpan(1.5f), "Rs. ".length(), strPaytmText.length() - 8, 0);
            holder.textViewPaytmText.setText(spannablePaytm);

            String strRedeemText = holder.textViewRedeemText.getText().toString();
            SpannableString spannableRedeem = new SpannableString(strRedeemText);
            spannableRedeem.setSpan(new ForegroundColorSpan(getResources()
                    .getColor(R.color.log_colorPrimary)), 8, spannableRedeem.length(), 0);
            spannableRedeem.setSpan(new RelativeSizeSpan(1.5f), 8, strRedeemText.length(), 0);
            holder.textViewRedeemText.setText(spannableRedeem);

            holder.textViewPaytm.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.textViewRedeem.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

        protected class ViewHolder {
            private LinearLayout linearLayoutUpperLayout;
            private TextView textViewPaytmText;
            private TextView textViewRedeemText;
            private TextView textViewNote;
            private AppCompatCheckBox checkBoxSelection;
            private RelativeLayout relativeLayoutSelection;
            private TextView textViewPaytm, textViewRedeem;

            public ViewHolder(View view) {
                linearLayoutUpperLayout = view.findViewById(R.id.linearLayout_upper_layout);
                textViewPaytmText = view.findViewById(R.id.textView_paytm_text);
                relativeLayoutSelection = view.findViewById(R.id.relativeLayout_selection);
                checkBoxSelection = view.findViewById(R.id.checkBox_redeem_selection);
                textViewRedeemText = view.findViewById(R.id.textView_redeem_text);
                textViewNote = view.findViewById(R.id.textView_note);
                textViewPaytm = view.findViewById(R.id.textView_paytm);
                textViewRedeem = view.findViewById(R.id.textView_redeem);
            }
        }
    }
}
