package com.surya.onspot.qrscanFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.adapters.RowRewardsDashboardAdapter;
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

public class FragmentRewards extends Fragment implements OnClickListener, HomeScreen.SnackbarRetryListener {

    Context cntx;
    View view;

    ArrayList<RewardsDashboardModel> arrRewardsDashboardModel = new ArrayList<RewardsDashboardModel>();
    private ListView listviewDashboard;
    private ConnectionDetectorActivity cd = null;
    private boolean isSecondTime = false;
    private Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page_reward, container, false);
        cntx = getActivity();

        listviewDashboard = view.findViewById(R.id.listView_rewards_dashboard);

        listviewDashboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((HomeScreen) getActivity()).setRedeemData(arrRewardsDashboardModel.get(position));
                Bundle objBundle = new Bundle();
                FragmentRewardList1 fragment = new FragmentRewardList1();
                switchFragment(fragment);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSecondTime == false) {
            isSecondTime = true;
            Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
            resources = context.getResources();
            ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.reward_title));
            ((TextView) view.findViewById(R.id.textView_rewards_content)).setText(resources.getString(R.string.rewards_content_message));

            callService();
        }
    }

    private void callService() {
        JSONObject objInputJSONObject = new JSONObject();
        try {
            objInputJSONObject.put("auth_token", new DatabaseHelper(cntx).getAuthToken());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ServiceCalls serviceCall = new ServiceCalls(getActivity(),  API_CONSTANTS.API_REWARDS_POINTS_LIST, objInputJSONObject.toString(),
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
            //			showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
            showNetworkFailDialog(API_CONSTANTS.NO_INTERNET_MSG);
            Log.d("------->", API_CONSTANTS.NO_INTERNET_MSG);
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
                new OnClickListener() {

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
                JSONArray objJsonArrayPointsList = json.getJSONArray("points_list");
                if (objJsonArrayPointsList != null && objJsonArrayPointsList.length() > 0) {
                    arrRewardsDashboardModel.clear();
                    RowRewardsDashboardAdapter adapter = new RowRewardsDashboardAdapter(cntx, arrRewardsDashboardModel);
                    for (int index = 0; index < objJsonArrayPointsList.length(); index++) {
                        arrRewardsDashboardModel.add(new RewardsDashboardModel(API_CONSTANTS.HOST_URL + objJsonArrayPointsList.getJSONObject(index).getString("logo"), objJsonArrayPointsList.getJSONObject(index).getString("points"), objJsonArrayPointsList.getJSONObject(index).getString("name"), objJsonArrayPointsList.getJSONObject(index).getInt("brand_id")));
                    }
                    listviewDashboard.setAdapter(adapter);
                    view.findViewById(R.id.textView_no_data_found).setVisibility(TextView.GONE);
                } else {
                    // No data found
                    view.findViewById(R.id.textView_no_data_found).setVisibility(TextView.VISIBLE);
                }
            } else if (!json.optBoolean(API_CONSTANTS.SUCCESS) && json.optInt("result_code") == HttpStatus.SC_REQUEST_TIMEOUT) {
                // SERVER TIME OUT EXCEPTION
                ((HomeScreen) getActivity()).showSnackbar(getView(), json.getString("responseMessage"), FragmentRewards.this, API_CONSTANTS.API_REWARDS_POINTS_LIST);
                /*final Snackbar mSnackbar = Snackbar.make(getView(), json.getString("responseMessage"), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    callService();
                                } catch (NullPointerException ne) {
                                    ne.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                mSnackbar.show();*/
            }
        } catch (NullPointerException ne) {
            view.findViewById(R.id.textView_no_data_found).setVisibility(TextView.VISIBLE);
            ((TextView) view.findViewById(R.id.textView_no_data_found)).setText("Something went wrong on server.");
            ne.printStackTrace();
        } catch (Exception e) {
            view.findViewById(R.id.textView_no_data_found).setVisibility(TextView.VISIBLE);
            ((TextView) view.findViewById(R.id.textView_no_data_found)).setText("Something went wrong on server.");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switchFragment(new FragmentRewards());
    }

    private void switchFragment(Fragment mTarget) {
        ((FragmentActivity) cntx).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_root, mTarget)
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRetryClickListener(String apiTag) {
        if (apiTag.equalsIgnoreCase(API_CONSTANTS.API_REWARDS_POINTS_LIST)) {
            callService();
        }
    }
}
