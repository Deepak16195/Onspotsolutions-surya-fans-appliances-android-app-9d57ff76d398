package com.surya.onspot.qrscanFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.surya.onspot.QRresponse.DatabaseHelper;
import com.surya.onspot.QRresponse.NewShipmentDetailModel;
import com.surya.onspot.R;

import java.util.ArrayList;

public class dummyListViewRemoveScanActivity extends AppCompatActivity {
    Button button;
    ListView listView;
    private DatabaseHelper objDatabaseHelper;
    private ArrayList<NewShipmentDetailModel> newShipmentScannedArrayList = new ArrayList<>();
    private Toolbar toolbar = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_scan_activity);
        objDatabaseHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.list);
        button = findViewById(R.id.testbutton);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Remove Scan Shipment");

        newShipmentScannedArrayList = objDatabaseHelper.getScanShipmentDetails();
        final ScanRemoveAdapter listViewDataAdapter = new ScanRemoveAdapter();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(listViewDataAdapter);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    class ScanRemoveAdapter extends BaseAdapter {
        LayoutInflater flater;

        public ScanRemoveAdapter() {
        }

        @Override
        public int getCount() {
            return newShipmentScannedArrayList.size();
        }

        @Override
        public Object getItem(int itemIndex) {
            return newShipmentScannedArrayList.get(itemIndex);
        }

        @Override
        public long getItemId(int itemIndex) {
            return itemIndex;
        }

        @Override
        public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {
            flater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = flater.inflate(R.layout.row_scan_remove_shipment, null,false);
            final ScanRemoveHolder viewHolder = new ScanRemoveHolder(convertView);
            convertView.setTag(viewHolder);

            final NewShipmentDetailModel listViewItemDto = newShipmentScannedArrayList.get(itemIndex);
//            viewHolder.getItemCheckbox().setChecked(listViewItemDto.getUploadToServer());
            viewHolder.textView_name.setText(listViewItemDto.getProductName());
            viewHolder.textView_no.setText(listViewItemDto.getNewShipmentID());
            viewHolder.textView_quantity.setText(listViewItemDto.getPackedRatio());
            viewHolder.itemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        objDatabaseHelper.updateShipment(listViewItemDto.getShipmentCode(), "1");
                    else
                        objDatabaseHelper.updateShipment(listViewItemDto.getShipmentCode(), "0");
                }
            });
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.itemCheckbox.isChecked())
                        viewHolder.itemCheckbox.setChecked(false);
                    else viewHolder.itemCheckbox.setChecked(true);

                }
            });
            return convertView;
        }

        public class ScanRemoveHolder extends RecyclerView.ViewHolder {

            private CheckBox itemCheckbox;

            TextView textView_no;
            TextView textView_name;
            TextView textView_quantity;

            public ScanRemoveHolder(View itemView) {
                super(itemView);
                itemCheckbox = itemView.findViewById(R.id.list_view_item_checkbox);
                textView_no = itemView.findViewById(R.id.textView_no);
                textView_name = itemView.findViewById(R.id.textView_name);
                textView_quantity = itemView.findViewById(R.id.textView_quantity);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}