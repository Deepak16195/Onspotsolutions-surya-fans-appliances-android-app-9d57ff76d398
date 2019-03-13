package com.surya.onspot.qrscanFragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCompleted extends Fragment {


    private View mainView = null;
    private ListView listView = null;
    private TextView textViewNoDataFound = null;
    private DatabaseHelper objDatabaseHelper = null;
    private ConnectionDetectorActivity cd = null;
    private LayoutInflater inflater = null;

    private ArrayList<CompletedListModel> arrCompletedListModel = new ArrayList<>();

    public FragmentCompleted() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetectorActivity(getActivity());
        objDatabaseHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        mainView = inflater.inflate(R.layout.fragment_fragment_completed, container, false);
        initView();
        return mainView;
    }

    private void initView() {
        listView = mainView.findViewById(R.id.listView_completed);
        textViewNoDataFound = mainView.findViewById(R.id.textView_no_data_found_completed);
        getCompletedListFromServer();
    }

    private void getCompletedListFromServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
            jsonObject.put("completed", true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("==", "========= FAIL TO CREATE JSON ==========");
        }

        ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1)+API_CONSTANTS.API_RECEIVE_HISTORY,
                jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {
            @Override
            public void myAsyncResponse(String result) {
                parseData(result);
            }
        });
        if (cd.isConnectingToInternet()) {
            objServiceCall.execute(1);
        } else {
            showNetworkFailDialog(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void parseData(String result) {
        try {
            JSONObject objJsonObject = new JSONObject(result);
            if (objJsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                textViewNoDataFound.setVisibility(TextView.GONE);
                JSONArray objJsonArrayData = objJsonObject.getJSONArray("list");
                arrCompletedListModel.clear();
                for (int index = 0; index < objJsonArrayData.length(); index++) {
                    arrCompletedListModel.add(new CompletedListModel(
                            objJsonArrayData.getJSONObject(index).optString("id"),
                            objJsonArrayData.getJSONObject(index).optString("quantity"),
                            objJsonArrayData.getJSONObject(index).optString("rec_quantity"),
                            objJsonArrayData.getJSONObject(index).optString("completed"),
                            objJsonArrayData.getJSONObject(index).optString("created_at"),
                            objJsonArrayData.getJSONObject(index).optString("PO_number"),
                            objJsonArrayData.getJSONObject(index).optString("PO_date"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_number"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_date")
                    ));
                }
                RowReceivedHistoryCompletedAdapter adapter = new RowReceivedHistoryCompletedAdapter();
                listView.setAdapter(adapter);
            } else {
                textViewNoDataFound.setVisibility(TextView.VISIBLE);
                Utils.showToast(getActivity(), objJsonObject.getString(API_CONSTANTS.RESPONSE_MESSAGE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fragmentBecameVisible() {
        Utils.out("FRAGMENT COMPLETED VISIBLE");
//        getCompletedListFromServer();
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

    public class RowReceivedHistoryCompletedAdapter extends BaseAdapter {

        public RowReceivedHistoryCompletedAdapter() {
        }

        @Override
        public int getCount() {
            return arrCompletedListModel.size();
        }

        @Override
        public CompletedListModel getItem(int position) {
            return arrCompletedListModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_received_history_pending, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag());
            return convertView;
        }

        private void initializeViews(CompletedListModel object, final ViewHolder holder) {
            //TODO implement
            holder.textViewPoNo.setText(object.getPO_number());
            holder.textViewPoDate.setText(getDisplayDate(object.getPO_date()));
            holder.textViewInNo.setText(object.getInvoice_number());
            holder.textViewInDate.setText(getDisplayDate(object.getInvoice_date()));
            holder.textViewQuantity.setText(object.getQuantity());
            holder.textViewPending.setText(object.getRec_quantity());

            holder.main_ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ael.setVisibility(holder.ael.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });
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
            private TextView textViewInNo;
            private TextView textViewPoNo;
            private TextView textViewInDate;
            private TextView textViewPoDate;
            private TextView textViewQuantity;
            private TextView textViewPending;
            private LinearLayout ael;
            private LinearLayout main_ly;

            public ViewHolder(View view) {
                main_ly= view.findViewById(R.id.main_ly);
                textViewInNo = view.findViewById(R.id.textView_in_no);
                textViewInDate = view.findViewById(R.id.textView_in_date);
                textViewQuantity = view.findViewById(R.id.textView_quantity);
                textViewPending = view.findViewById(R.id.textView_pending);
                textViewPoNo = view.findViewById(R.id.textView_po_no);
                textViewPoDate = view.findViewById(R.id.textView_po_date);
                ael = view.findViewById(R.id.expandable_ans);
            }
        }
    }

    class CompletedListModel {
        private String id = "";
        private String quantity = "";
        private String rec_quantity = "";
        private String completed = "";
        private String created_at = "";
        private String PO_number = "";
        private String PO_date = "";
        private String Invoice_number = "";
        private String Invoice_date = "";

        public CompletedListModel(String id, String quantity, String rec_quantity, String completed, String created_at, String PO_number, String PO_date, String invoice_number, String invoice_date) {

            this.id = id;
            this.quantity = quantity;
            this.rec_quantity = rec_quantity;
            this.completed = completed;
            this.created_at = created_at;
            this.PO_number = PO_number;
            this.PO_date = PO_date;
            this.Invoice_number = invoice_number;
            this.Invoice_date = invoice_date;

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getRec_quantity() {
            return rec_quantity;
        }

        public void setRec_quantity(String rec_quantity) {
            this.rec_quantity = rec_quantity;
        }

        public String getCompleted() {
            return completed;
        }

        public void setCompleted(String completed) {
            this.completed = completed;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getPO_number() {
            return PO_number;
        }

        public void setPO_number(String PO_number) {
            this.PO_number = PO_number;
        }

        public String getPO_date() {
            return PO_date;
        }

        public void setPO_date(String PO_date) {
            this.PO_date = PO_date;
        }

        public String getInvoice_number() {
            return Invoice_number;
        }

        public void setInvoice_number(String invoice_number) {
            Invoice_number = invoice_number;
        }

        public String getInvoice_date() {
            return Invoice_date;
        }

        public void setInvoice_date(String invoice_date) {
            Invoice_date = invoice_date;
        }
    }

}
