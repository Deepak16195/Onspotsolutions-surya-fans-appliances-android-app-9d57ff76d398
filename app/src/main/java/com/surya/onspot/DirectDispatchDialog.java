package com.surya.onspot;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.ServiceCalls;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.model.AgentListModel;
import com.surya.onspot.qrscanFragments.FragmentPending;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DirectDispatchDialog extends AlertDialog {

    private Context context;
    private ConnectionDetectorActivity cd;
    private String ActName = "", PO_date = "", Invoice_date = "", ShipmentId = "", shipment_type = "", myFormat = "dd-MM-YYYY";
    private EditText textViewInNo, textViewPoNo;
    private TextView textViewInDate, textViewPoDate;
    private Spinner spinnerAgentList;
    private Spinner spinnerSendList;
    private DatabaseHelper objDatabaseHelper;
    private ArrayList<AgentListModel> arrayAgent = new ArrayList<>();
    private final Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private ArrayList<String> SacnnedBarcodeList = new ArrayList<String>();
    SpinnerAgentAdapter spinnerAgentAdapter;
    FragmentPending fragmentPending;

    public DirectDispatchDialog(Context context, String ActName, String ShipmentId) {
        super(context);
        cd = new ConnectionDetectorActivity(context);
        this.context = context;
        this.ShipmentId = ShipmentId;
        this.ActName = ActName;
        objDatabaseHelper = new DatabaseHelper(context);
        initView();
    }


    private void initView() {

        LayoutInflater objLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = objLayoutInflater.inflate(R.layout.diarect_dispatch_dialog, null, false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        textViewInNo = view.findViewById(R.id.textView_in_no);
        textViewInDate = view.findViewById(R.id.textView_in_date);
        textViewPoNo = view.findViewById(R.id.textView_po_no);
        textViewPoDate = view.findViewById(R.id.textView_po_date);
        spinnerAgentList = view.findViewById(R.id.spinnerAgentList);
        spinnerSendList = view.findViewById(R.id.spinnerSendList);

        SacnnedBarcodeList = objDatabaseHelper.getScanShipmentBarcode();
        fragmentPending =new FragmentPending();

        try {
            JSONArray jsonArray = new JSONArray(Utils.getPreference(context, PreferenceKeys.USER_SHIPMENT_TYPE, ""));
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
            spinnerSendList.setAdapter(spinnerMenu);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerSendList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shipment_type = spinnerSendList.getItemAtPosition(spinnerSendList.getSelectedItemPosition()).toString();
                setupAgencyList(shipment_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (ActName.equalsIgnoreCase("NewShipment")) {
            view.findViewById(R.id.ly_send_to).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ly_recipient).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.ly_send_to).setVisibility(View.GONE);
            view.findViewById(R.id.ly_recipient).setVisibility(View.VISIBLE);
            setupAgencyList("Distributor");
        }


        spinnerAgentAdapter = new SpinnerAgentAdapter(getContext(), android.R.layout.simple_spinner_item, arrayAgent); //selected item will look like a spinner set from XML
        spinnerAgentAdapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinnerAgentList.setAdapter(spinnerAgentAdapter);

        final DatePickerDialog.OnDateSetListener poDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textViewPoDate.setText(sdf.format(myCalendar.getTime()));
                PO_date = textViewPoDate.getText().toString();
            }

        };

        textViewPoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, poDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener InDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textViewInDate.setText(sdf.format(myCalendar.getTime()));
                Invoice_date = textViewInDate.getText().toString();
            }

        };

        textViewInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, InDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        view.findViewById(R.id.btnDispatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Invoice_date.trim().isEmpty()) {
                    Utils.showToast(context, "Enter Invoice Date");
                    return;
                }
                if (PO_date.trim().isEmpty()) {
                    Utils.showToast(context, "Enter PO Date");
                    return;
                }
                if (!textViewPoNo.getText().toString().trim().isEmpty()) {
                    if (!textViewInNo.getText().toString().trim().isEmpty()) {
                            JSONObject jsonObject = new JSONObject();
                            int position = spinnerAgentList.getSelectedItemPosition();
                            try {
                                if (ActName.equalsIgnoreCase("NewShipment")) {

                                    JSONObject objNewShipmentDetails = new JSONObject();
                                    try {
                                        objNewShipmentDetails.put("AgencyId", arrayAgent.get(position).getId());
                                        objNewShipmentDetails.put("Invoice_number", textViewInNo.getText().toString().trim());
                                        objNewShipmentDetails.put("Invoice_date", Invoice_date);
                                        objNewShipmentDetails.put("PO_number", textViewPoNo.getText().toString().trim());
                                        objNewShipmentDetails.put("PO_date", PO_date);
                                        objNewShipmentDetails.put("quantity", objDatabaseHelper.getTotalShipmentCount());
                                        objNewShipmentDetails.put("shipment_type", shipment_type);
                                        objNewShipmentDetails.put("MasterCartonCode", new JSONArray(SacnnedBarcodeList));

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    JSONArray jsonArray = new JSONArray();

                                    jsonArray.put(objNewShipmentDetails);

                                    jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                                    jsonObject.put("list", jsonArray);
                                } else {
                                    jsonObject.put("auth_token", objDatabaseHelper.getAuthToken());
                                    jsonObject.put("shipment_id", ShipmentId);
                                    jsonObject.put("AgencyId", arrayAgent.get(position).getId());
                                    jsonObject.put("Invoice_number", textViewInNo.getText().toString().trim().replace("\\", ""));
                                    jsonObject.put("Invoice_date", Invoice_date);
                                    jsonObject.put("PO_number", textViewPoNo.getText().toString());
                                    jsonObject.put("PO_date", PO_date);
                                }

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d("==", "========= FAIL TO CREATE JSON ==========");
                            }

                            ServiceCalls objServiceCall = new ServiceCalls(context, ActName.equalsIgnoreCase("NewShipment") ? (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE) + API_CONSTANTS.API_SHIPMENT_DONE : (Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1) + API_CONSTANTS.API_DIRECT_SHIPMENT,
                                    jsonObject.toString(), "Requesting...", 0, new AsyncResponseActivity() {
                                @Override
                                public void myAsyncResponse(String result) {
                                    try {
                                        JSONObject objJsonObject = new JSONObject(result);
                                        if (objJsonObject.getString("success").equalsIgnoreCase("true")) {
                                            Utils.showToast(getContext(), "Dispatch Successfully");
                                            if (ActName.equalsIgnoreCase("NewShipment")) {
                                                objDatabaseHelper.deleteOfflineShipment();
                                            }else {
                                                //fragmentPending.refreshData(objDatabaseHelper.getAuthToken(),Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans"));
                                            }
                                            dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dismiss();
                                    }
                                }
                            });
                            if (cd.isConnectingToInternet()) {
                                objServiceCall.execute(1);
                            } else {
                                if (ActName.equalsIgnoreCase("NewShipment")) {
                                    Utils.addPreference(getContext(), PreferenceKeys.OFFLINE_SHIPMENT_DATA, jsonObject.toString());
                                    objDatabaseHelper.deleteOfflineShipment();
                                } else
                                    showNetworkFailDialog(context.getResources().getString(R.string.no_internet_connection));
                            }
                    } else {
                        Utils.AlertDialog(context, "", "Enter Invoice No");
                        textViewInNo.setError("Enter Invoice No");
                        textViewInNo.setSelection(textViewInNo.getText().length());
                        textViewInNo.requestFocus();
                    }
                } else {
                    Utils.AlertDialog(context, "", "Enter PoNo");
                    textViewPoNo.setError("Enter PoNo");
                    textViewPoNo.setSelection(textViewPoNo.getText().length());
                    textViewPoNo.requestFocus();
                }
            }
        });

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        this.setView(view);
    }

    public void setupAgencyList(final String shipment_type) {
        arrayAgent.clear();
        final ProgressDialog progressBar = new ProgressDialog(context);
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arrayAgent = objDatabaseHelper.getAgentList(shipment_type);
                spinnerAgentAdapter = new SpinnerAgentAdapter(context, R.layout.my_simple_spinner_item, arrayAgent);
                spinnerAgentList.setAdapter(spinnerAgentAdapter);
                spinnerAgentAdapter.notifyDataSetChanged();
                progressBar.dismiss();
            }
        }, 1000);
    }

    class SpinnerAgentAdapter extends ArrayAdapter<AgentListModel> {

        LayoutInflater flater;

        public SpinnerAgentAdapter(Context context, int simple_spinner_item, ArrayList<AgentListModel> arrayAgent) {
            super(context, simple_spinner_item, arrayAgent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return rowview(convertView, position);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return rowview(convertView, position);
        }

        private View rowview(View convertView, int position) {

            AgentListModel rowItem = getItem(position);

            viewHolder holder;
            View rowview = convertView;
            if (rowview == null) {

                holder = new viewHolder();
                flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowview = flater.inflate(R.layout.my_simple_spinner_item, null, false);
                holder.txtTitle = (TextView) rowview.findViewById(R.id.spin_item);
                rowview.setTag(holder);
            } else {
                holder = (viewHolder) rowview.getTag();
            }
            holder.txtTitle.setText(rowItem.getName());

            return rowview;
        }

        private class viewHolder {
            TextView txtTitle;
        }
    }

    private void showNetworkFailDialog(String msg) {
        // TODO Auto-generated method stub
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View content = inflater.inflate(R.layout.network_failure_dialog, null);

        builder.setView(content);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView tvMsg = content.findViewById(R.id.networkFailMsg);
        tvMsg.setText(msg);
        TextView tvoo = content.findViewById(R.id.oops);
        tvoo.setVisibility(View.GONE);
        content.findViewById(R.id.btnNetworkFailureOK).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        alertDialog.dismiss();
                    }

                });
    }
}