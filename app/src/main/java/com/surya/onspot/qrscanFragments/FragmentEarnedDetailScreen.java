package com.surya.onspot.qrscanFragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
public class FragmentEarnedDetailScreen extends Fragment implements HomeScreen.SnackbarRetryListener {

    private View view = null;
    private Context cntx = null;
    private Bundle objBundle = null;
    private ListView listView = null;
    private RewardsDashboardModel object = null;
    private ConnectionDetectorActivity cd = null;
    private ArrayList<ListModel> arrListModel = null;
    private boolean isSecondTime = false;
    private Resources resources;
    public FragmentEarnedDetailScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_earned_detail_screen, container, false);
        cntx = getActivity();
        initView();
        return view;
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

        if (isSecondTime == false) {
            isSecondTime = true;
            Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
            resources = context.getResources();
            ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.reward_title));
            object = ((HomeScreen) getActivity()).getRedeemData();
            callAPI();
        }
    }

    private void callAPI() {
        if (object != null) {
            ((TextView) view.findViewById(R.id.textView_title)).setText("EARNED\nHISTORY");
            ((ImageView) view.findViewById(R.id.imageView_left)).setImageResource(R.drawable.earned_history);

            JSONObject objInputJSONObject = new JSONObject();
            try {
                objInputJSONObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
                objInputJSONObject.put("brand_id", object.getBrandId());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ServiceCalls serviceCall = new ServiceCalls(getActivity(),  (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1)+API_CONSTANTS.API_REWARDS_EARNED_LIST, objInputJSONObject.toString(),
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

    private void parseJason(String output) {
        try {
            isSecondTime = false;
            JSONObject json = new JSONObject(output);
            if (json != null && json.optBoolean(API_CONSTANTS.SUCCESS)) {
                arrListModel = new ArrayList<>();
                JSONArray objJsonArrayOfferList = json.getJSONArray("earn_history");
                if (objJsonArrayOfferList.length() > 0) {
                    arrListModel.clear();
                    RowEarnedHistoryListAdapter adapter = new RowEarnedHistoryListAdapter(cntx);
                    for (int index = 0; index < objJsonArrayOfferList.length(); index++) {
                        arrListModel.add(new ListModel(objJsonArrayOfferList.getJSONObject(index).toString()));
                    }
                    listView.setAdapter(adapter);
                } else {
                    // No data found
                }
            } else if (json != null && json.optBoolean(API_CONSTANTS.SUCCESS) == false && json.optInt("result_code") == HttpStatus.SC_REQUEST_TIMEOUT) {
                // SERVER TIME OUT EXCEPTION
                ((HomeScreen) getActivity()).showSnackbar(getView(), json.getString("responseMessage"), FragmentEarnedDetailScreen.this, API_CONSTANTS.API_REWARDS_COMPLETE_REDEEM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        listView = view.findViewById(R.id.listView_list);
    }

    @Override
    public void onRetryClickListener(String apiTag) {
        if (apiTag.equalsIgnoreCase( (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1)+API_CONSTANTS.API_REWARDS_EARNED_LIST)) {
            try {
                callAPI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class RowEarnedHistoryListAdapter extends BaseAdapter {


        private Context context;
        private LayoutInflater layoutInflater;

        public RowEarnedHistoryListAdapter(Context context) {
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
                convertView = layoutInflater.inflate(R.layout.row_earned_history_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag());
            return convertView;
        }

        private void initializeViews(ListModel object, ViewHolder holder) {
            //TODO implement
            try {
                JSONObject jsonObject = new JSONObject(object.getData());
                holder.textViewCode.setText(jsonObject.optString("code"));
                String date = jsonObject.optString("created_at");
                holder.textViewDate.setText(date.length() > 0 ? date.split("T")[0] : "");
                holder.textViewPoint.setText(jsonObject.optString("point"));
                holder.textViewProductName.setText(jsonObject.optString("product_name"));
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected class ViewHolder {
            private LinearLayout linearLayoutUpperLayout;
            private TextView textViewPoint;
            private TextView textViewProductName;
            private TextView textViewCode;
            private TextView textViewDate;

            public ViewHolder(View view) {
                linearLayoutUpperLayout = view.findViewById(R.id.linearLayout_upper_layout);
                textViewPoint = view.findViewById(R.id.textView_point);
                textViewProductName = view.findViewById(R.id.textView_product_name);
                textViewCode = view.findViewById(R.id.textView_code);
                textViewDate = view.findViewById(R.id.textView_date);
            }
        }
    }

}
