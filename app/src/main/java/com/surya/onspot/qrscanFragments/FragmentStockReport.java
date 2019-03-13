package com.surya.onspot.qrscanFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Prasanna on 03-Jun-18.
 */

public class FragmentStockReport extends Fragment {

    private View view = null;
    private Activity cntx = null;
    TextView textViewNoDataFound;
    private LinearLayout linearLayoutDateSelection;
    private ListView listViewStockReport;
    private StockReportAdapter listAdapter;
    private LayoutInflater inflater = null;

    private DatabaseHelper objDatabaseHelper = null;
    private ConnectionDetectorActivity cd = null;
    private ArrayList<StockReportModel> arrStockReportModel = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stock_report, container, false);
        this.inflater = inflater;
        cntx = getActivity();
        objDatabaseHelper = new DatabaseHelper(cntx);
        cd = new ConnectionDetectorActivity(getActivity());
        listAdapter = new StockReportAdapter();
        listViewStockReport = view.findViewById(R.id.listView_stockReport);
        listViewStockReport.addHeaderView(inflater.inflate(R.layout.row_stock_report, null));
        listViewStockReport.setAdapter(listAdapter);
        textViewNoDataFound = view.findViewById(R.id.textView_no_data_found_completed);
        arrStockReportModel.clear();
        callAPI();
        return view;
    }


    private void callAPI() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());

            ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE) + API_CONSTANTS.CURRENT_STOCK,
                    jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {
                @Override
                public void myAsyncResponse(String result) {
                    parseData(result);
//                    Toast.makeText(cntx, "Result: " + result, Toast.LENGTH_SHORT).show();
                }
            });
            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void parseData(String result) {
        try {
            JSONObject objJsonObject = new JSONObject(result);
            if (objJsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                textViewNoDataFound.setVisibility(View.GONE);

                JSONArray objListData = objJsonObject.getJSONArray("list");
                JSONArray objJsonArrayData = objListData.getJSONArray(0);

                arrStockReportModel.clear();

                if (objListData.length() > 0)
                    for (int index = 0; index < objListData.length(); index++) {
                        arrStockReportModel.add(new StockReportModel(String.valueOf(index + 1),
                                objJsonArrayData.getString(0),
                                objJsonArrayData.getString(1)
                        ));
                        listViewStockReport.setVisibility(View.VISIBLE);
                    }
                else {
                    textViewNoDataFound.setVisibility(View.VISIBLE);
                }

                listAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public class StockReportAdapter extends BaseAdapter {

        public StockReportAdapter() {
        }

        @Override
        public int getCount() {
            return arrStockReportModel.size();
        }

        @Override
        public StockReportModel getItem(int position) {
            return arrStockReportModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_stock_report, null);
                convertView.setTag(new StockReportAdapter.ViewHolder(convertView));
            }
            initializeViews(getItem(position), (StockReportAdapter.ViewHolder) convertView.getTag());
            return convertView;
        }

        private void initializeViews(StockReportModel object, StockReportAdapter.ViewHolder holder) {
            //TODO implement
            holder.textViewNo.setText(object.getId());
            holder.textViewName.setText(getDisplayDate(object.getName()));
            holder.textViewQuantity.setText(object.getQuantity());
        }

        private String getDisplayDate(String created_at) {
            try {
                return created_at.split("T")[0];
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected class ViewHolder {
            private TextView textViewNo;
            private TextView textViewName;
            private TextView textViewQuantity;

            public ViewHolder(View view) {
                textViewNo = view.findViewById(R.id.textView_no);
                textViewName = view.findViewById(R.id.textView_name);
                textViewQuantity = view.findViewById(R.id.textView_quantity);
            }
        }
    }

    @Override
    public void onResume() {
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle("Stock Report");
        super.onResume();
    }

    class StockReportModel {
        String id = "";
        String name = "";
        String quantity = "";

        public StockReportModel(String id, String name, String quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }
}
