package com.surya.onspot.qrscanFragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surya.onspot.DirectDispatchDialog;
import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.interfac.Refresh;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPending extends Fragment implements Refresh {

    private View mainView = null;
    private RecyclerView mRecyclerView = null;
    private TextView textViewNoDataFound = null;
    private DatabaseHelper objDatabaseHelper = null;
    private ConnectionDetectorActivity cd = null;
    private LayoutInflater inflater = null;
    private ArrayList<CompletedListModel> arrCompletedListModel = new ArrayList<>();
    String authToken,Type;

    public FragmentPending() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetectorActivity(getActivity());
        objDatabaseHelper = new DatabaseHelper(getActivity());
        Utils.addPreference(getContext(), "authToken", objDatabaseHelper.getAuthToken());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        mainView = inflater.inflate(R.layout.fragment_fragment_pending, container, false);
        initView();
       // dataCall();
        return mainView;
    }

    private void dataCall() {
        objDatabaseHelper = new DatabaseHelper(getActivity());
       // getCompletedListFromServer();
    }

    private void initView() {
        mRecyclerView = mainView.findViewById(R.id.listView_pending);

//        mRecyclerView.addHeaderView(inflater.inflate(R.layout.row_received_history_pending_header, null));
        textViewNoDataFound = mainView.findViewById(R.id.textView_no_data_found_pending);
        authToken = Utils.getPreference(getContext(), "authToken","");
        Type = Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans");
        getCompletedListFromServer(authToken,Type);
    }

    private void getCompletedListFromServer(String authToken,String Type) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth_token", authToken);

            jsonObject.put("completed", false);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("==", "========= FAIL TO CREATE JSON ==========");
        }

        if(Type.equals("fans")) {
            ServiceCalls objServiceCall = new ServiceCalls(getActivity(),  API_CONSTANTS.API_BASE  + API_CONSTANTS.API_RECEIVE_HISTORY, jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {

                @Override
                public void myAsyncResponse(String result) {
                    parseData(result);

                }
            });
            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                showNetworkFailDialog(getString(R.string.no_internet_connection));
            }
               // objServiceCall.execute(1);

        }else {
            ServiceCalls objServiceCall = new ServiceCalls(getActivity(),  API_CONSTANTS.API_BASE_APPLIANCE1 + API_CONSTANTS.API_RECEIVE_HISTORY, jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {

                @Override
                public void myAsyncResponse(String result) {
                    parseData(result);
                }
            });
            if (cd.isConnectingToInternet()) {
                objServiceCall.execute(1);
            } else {
                showNetworkFailDialog(getString(R.string.no_internet_connection));
            }
               // objServiceCall.execute(1);

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
                            String.valueOf(index + 1),
                            objJsonArrayData.getJSONObject(index).optString("id"),
                            objJsonArrayData.getJSONObject(index).optString("quantity"),
                            objJsonArrayData.getJSONObject(index).optString("rec_quantity"),
                            objJsonArrayData.getJSONObject(index).optString("completed"),
                            objJsonArrayData.getJSONObject(index).optString("created_at"),
                            objJsonArrayData.getJSONObject(index).optString("PO_number"),
                            objJsonArrayData.getJSONObject(index).optString("PO_date"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_number"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_date"),
                            objJsonArrayData.getJSONObject(index).optString("shipment_id")
                    ));
                }
                RowReceivedHistoryCompletedAdapter adapter = new RowReceivedHistoryCompletedAdapter();
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(adapter);
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

    @Override
    public void refreshData(String token, String Type) {

      //  getCompletedListFromServer();
        getCompletedListFromServer(token,Type);

    }

    public class RowReceivedHistoryCompletedAdapter extends RecyclerView.Adapter<RowReceivedHistoryCompletedAdapter.ViewHolder> {

        public RowReceivedHistoryCompletedAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_received_history_pending, viewGroup, false); //Inflating the layout
            return new ViewHolder(convertView, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            initializeViews(viewHolder);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return arrCompletedListModel.size();
        }

        private void initializeViews(final ViewHolder holder) {
            //TODO implement

            final CompletedListModel object = arrCompletedListModel.get(holder.getAdapterPosition());

            holder.textViewPoNo.setText(object.getPO_number());
            holder.textViewPoDate.setText(getDisplayDate(object.getPO_date()));
            holder.textViewInNo.setText(object.getInvoice_number());
            holder.textViewInDate.setText(getDisplayDate(object.getInvoice_date()));
            holder.textViewQuantity.setText(object.getQuantity());
            holder.textViewPending.setText(object.getRec_quantity());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ael.setVisibility(holder.ael.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            if (Utils.getPreference(getActivity(), PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_BRANCH))
                holder.textView_direct_dispatch.setVisibility(object.getRec_quantity().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);

            holder.textView_direct_dispatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DirectDispatchDialog(getContext(), "DirectDispatch", object.getShipment_id()).show();
                    //getCompletedListFromServer();
                }
            });

        }

        private String getDisplayDate(String created_at) {
            try {
                return created_at.split("T")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView_direct_dispatch;
            private TextView textViewInNo;
            private TextView textViewPoNo;
            private TextView textViewInDate;
            private TextView textViewPoDate;
            private TextView textViewQuantity;
            private TextView textViewPending;
            private LinearLayout ael;

            public ViewHolder(View view, int ViewType) {
                super(view);
                textView_direct_dispatch = view.findViewById(R.id.textView_direct_dispatch);
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
        private String view_id = "";
        private String id = "";
        private String quantity = "";
        private String rec_quantity = "";
        private String completed = "";
        private String created_at = "";
        private String PO_number = "";
        private String PO_date = "";
        private String Invoice_number = "";
        private String Invoice_date = "";
        private String Shipment_id = "";

        public CompletedListModel(String view_id, String id, String quantity, String rec_quantity, String completed, String created_at, String PO_number, String PO_date, String invoice_number, String invoice_date, String shipment_id) {

            this.view_id = view_id;
            this.id = id;
            this.quantity = quantity;
            this.rec_quantity = rec_quantity;
            this.completed = completed;
            this.created_at = created_at;
            this.PO_number = PO_number;
            this.PO_date = PO_date;
            this.Invoice_number = invoice_number;
            this.Invoice_date = invoice_date;
            this.Shipment_id = shipment_id;

        }

        public String getView_id() {
            return view_id;
        }

        public void setView_id(String view_id) {
            this.view_id = view_id;
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

        public String getShipment_id() {
            return Shipment_id;
        }

        public void setShipment_id(String shipment_id) {
            Shipment_id = shipment_id;
        }
    }

}
