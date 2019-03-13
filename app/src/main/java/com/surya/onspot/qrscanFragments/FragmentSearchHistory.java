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

public class FragmentSearchHistory extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View view = null;
    private Activity cntx = null;

    private LinearLayout linearLayoutDateSelection;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private AppCompatSpinner spinnerMode;
    private Button buttonGo;
    private ListView listViewSearchedHistory;
    private LayoutInflater inflater = null;

    private List<SearchHistoryModel> arrSearchHistoryModel = new ArrayList<SearchHistoryModel>();
    private DatabaseHelper objDatabaseHelper = null;
    private ConnectionDetectorActivity cd = null;
    private RowSearchHistoryItemAdapter listAdapter = null;
    private ArrayAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_search_history, container, false);
        this.inflater = inflater;
        cntx = getActivity();
        objDatabaseHelper = new DatabaseHelper(cntx);
        cd = new ConnectionDetectorActivity(getActivity());
        initViews();
        return view;
    }

    private void initViews() {
        linearLayoutDateSelection = view.findViewById(R.id.linearLayout_date_selection);
        textViewStartDate = view.findViewById(R.id.textView_start_date);
        textViewEndDate = view.findViewById(R.id.textView_end_date);
        spinnerMode = view.findViewById(R.id.spinner_mode);
        buttonGo = view.findViewById(R.id.button_go);
        listViewSearchedHistory = view.findViewById(R.id.listView_searched_history);

        if (Utils.getPreference(getActivity(), PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER)) {
            adapter = new ArrayAdapter<>(cntx, android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.search_history_mode_array_retailer));
            adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
            spinnerMode.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<>(cntx, android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.search_history_mode_array));
            adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
            spinnerMode.setAdapter(adapter);
        }

        listAdapter = new RowSearchHistoryItemAdapter(getActivity());
        listViewSearchedHistory.setAdapter(listAdapter);

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSearchHistoryModel.clear();
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewStartDate.getText().length() > 0 && textViewEndDate.getText().length() > 0) {
                    callAPI();
                } else {
                    Utils.showToast(getActivity(), "Please select From & To Date");
                }
            }
        });

        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar objfromCalander = Calendar.getInstance();
                    DatePickerDialog objFromDatePicker = DatePickerDialog.newInstance(
                            FragmentSearchHistory.this,
                            objfromCalander.get(Calendar.YEAR),
                            objfromCalander.get(Calendar.MONTH),
                            objfromCalander.get(Calendar.DAY_OF_MONTH));
                    objFromDatePicker.setMaxDate(objfromCalander);
                    objFromDatePicker.show(getActivity().getFragmentManager(), "start_date");
                    textViewEndDate.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (textViewStartDate.getText().toString().trim().length() > 0) {
                        Calendar objfromCalander = Calendar.getInstance();
                        DatePickerDialog objFromDatePicker = DatePickerDialog.newInstance(
                                FragmentSearchHistory.this,
                                objfromCalander.get(Calendar.YEAR),
                                objfromCalander.get(Calendar.MONTH),
                                objfromCalander.get(Calendar.DAY_OF_MONTH));
                        objFromDatePicker.setMaxDate(objfromCalander);

                        Calendar fromDateforTo = Calendar.getInstance();
                        if (!textViewStartDate.getText().toString().trim().isEmpty()) {
                            String[] arrayDatefrom = textViewStartDate.getText().toString().trim().split("-");
                            fromDateforTo.set(Calendar.YEAR, Integer.parseInt(arrayDatefrom[0]));
                            fromDateforTo.set(Calendar.MONTH, Integer.parseInt(arrayDatefrom[1]) - 1);
                            fromDateforTo.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arrayDatefrom[2]));
                        }
                        objFromDatePicker.setMinDate(fromDateforTo);
                        objFromDatePicker.show(getActivity().getFragmentManager(), "end_date");
                    } else {
                        Utils.showToast(getActivity(), "Please select from date");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void callAPI() {
        String mode = spinnerMode.getSelectedItemPosition() == 0 ? "des" : "rec";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
            jsonObject.put("type", mode);
            jsonObject.put("s_date", textViewStartDate.getText().toString().trim());
            jsonObject.put("e_date", textViewEndDate.getText().toString().trim());

//          API_CONSTANTS.API_SEARCH_HISTORY + "auth_token=" + objDatabaseHelper.getAuthToken() + "&type=" + mode + "&s_date=" + textViewStartDate.getText().toString().trim() + "&e_date=" + textViewEndDate.getText().toString().trim()

            ServiceCalls objServiceCall = new ServiceCalls(getActivity(), (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_SEARCH_HISTORY,
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
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void parseData(String result) {
        try {
            JSONObject objJsonObject = new JSONObject(result);
            if (objJsonObject.getBoolean(API_CONSTANTS.SUCCESS)) {
                JSONArray objJsonArrayData = objJsonObject.getJSONArray("list");
                arrSearchHistoryModel.clear();
                for (int index = 0; index < objJsonArrayData.length(); index++) {
                    arrSearchHistoryModel.add(new SearchHistoryModel(
                            objJsonArrayData.getJSONObject(index).optString("id"),
                            objJsonArrayData.getJSONObject(index).optString("quantity"),
                            objJsonArrayData.getJSONObject(index).optString("rec_quantity"),
                            objJsonArrayData.getJSONObject(index).optString("created_at"),
                            objJsonArrayData.getJSONObject(index).optString("completed"),
                            objJsonArrayData.getJSONObject(index).optString("PO_number"),
                            objJsonArrayData.getJSONObject(index).optString("PO_date"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_number"),
                            objJsonArrayData.getJSONObject(index).optString("Invoice_date"),
                            objJsonArrayData.getJSONObject(index).optString("client"),
                            String.valueOf(spinnerMode.getSelectedItem())
                    ));
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

    @Override
    public void onResume() {
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle("Search History");
        super.onResume();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        switch (view.getTag()) {
            case "start_date":
                textViewStartDate.setText("  " + year + "-" + Utils.getFormatedMonth((monthOfYear + 1)) + "-" + Utils.getFormatedDay(dayOfMonth));
                break;
            case "end_date":
                textViewEndDate.setText("  " + year + "-" + Utils.getFormatedMonth((monthOfYear + 1)) + "-" + Utils.getFormatedDay(dayOfMonth));
                break;
        }
    }

    public class SearchHistoryModel {
        private String id = "";
        private String quantity = "";
        private String rec_quantity = "";
        private String created_at = "";
        private String completed = "";
        private String PO_number = "";
        private String PO_date = "";
        private String Invoice_number = "";
        private String Invoice_date = "";
        private String client = "";
        private String Mode = "";

        public SearchHistoryModel(String id, String quantity, String rec_quantity, String created_at, String completed, String PO_number, String PO_date, String invoice_number, String invoice_date, String client, String mode) {
            this.id = id;
            this.quantity = quantity;
            this.rec_quantity = rec_quantity;
            this.created_at = created_at;
            this.completed = completed;
            this.PO_number = PO_number;
            this.PO_date = PO_date;
            Invoice_number = invoice_number;
            Invoice_date = invoice_date;
            this.client = client;
            Mode = mode;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCompleted() {
            return completed;
        }

        public void setCompleted(String completed) {
            this.completed = completed;
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

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getMode() {
            return Mode;
        }

        public void setMode(String mode) {
            Mode = mode;
        }
    }

    public class RowSearchHistoryItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        public RowSearchHistoryItemAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrSearchHistoryModel.size();
        }

        @Override
        public SearchHistoryModel getItem(int position) {
            return arrSearchHistoryModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.row_search_history_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag());
            return convertView;
        }

        private void initializeViews(SearchHistoryModel object, final ViewHolder holder) {
            //TODO implement
            holder.textViewPoNo.setText(object.getPO_number());
            holder.textViewPoDate.setText(getDisplayDate(object.getPO_date()));
            holder.textViewInNo.setText(object.getInvoice_number());
            holder.textViewInDate.setText(getDisplayDate(object.getInvoice_date()));
            holder.textViewQuantity.setText(object.getQuantity());
            holder.textViewPending.setText(object.getRec_quantity());
            holder.textViewClientCell.setText(object.getClient());

            holder.main_ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ael.setVisibility(holder.ael.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            Utils.out("MODE : " + object.getMode());

            holder.textViewClientCell.setVisibility(object.getMode().equals("Shipment Received")?TextView.GONE:TextView.VISIBLE);
            holder.textView_client.setVisibility(object.getMode().equals("Shipment Received")?TextView.GONE:TextView.VISIBLE);
        }

        protected class ViewHolder {
            private TextView textViewInNo;
            private TextView textViewPoNo;
            private TextView textViewInDate;
            private TextView textViewPoDate;
            private TextView textViewQuantity;
            private TextView textViewClientCell;
            private TextView textView_client;
            private TextView textViewPending;
            private LinearLayout ael;
            private LinearLayout main_ly;

            public ViewHolder(View view) {
                textViewInNo = view.findViewById(R.id.textView_in_no);
                textViewInDate = view.findViewById(R.id.textView_in_date);
                textViewQuantity = view.findViewById(R.id.textView_quantity);
                textViewPending = view.findViewById(R.id.textView_pending);
                textViewPoNo = view.findViewById(R.id.textView_po_no);
                textViewPoDate = view.findViewById(R.id.textView_po_date);
                ael = view.findViewById(R.id.expandable_ans);
                main_ly = view.findViewById(R.id.main_ly);
                textViewClientCell = view.findViewById(R.id.textViewClientCell);
                textView_client = view.findViewById(R.id.textView_client);
            }
        }
    }

    private String getDisplayDate(String created_at) {
        try {
            return created_at.split("T")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
