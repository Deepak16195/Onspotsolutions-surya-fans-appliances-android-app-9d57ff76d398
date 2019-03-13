package com.surya.onspot.tables;

import android.net.Uri;

public class TblOnspotQrResults {

    public static final String TABLE_NAME = "TblOnspotQrResults";

    //TABLE_MESSAGES Column names
    public static final String KEY_ID = "_id";
    public static final String ImageUrl = "imageurl";
    public static final String ResponseTime = "responsetime";
    public static final String ResponseData = "responsedata";
    public static final String Statutory_warning = "statutory_warning";
    public static final String COL_RESPONSE_MESSAGE = "responseMessage";
    public static final String COL_RESPONSE_TYPE = "responseType";
    public static final String Code = "qrCode";
    public static final String CPO_NO = "Cpo_No";
    public static final String Cpo_Date = "Cpo_Date";
    public static final String So_number_date = "So_number_Date";
    public static final String Brand_Name = "Brand_name";
    public static final String Mill_Name = "mill_name";
    public static final String Procurement_agency = "procurement_agency";
    public static final String Manufacturing_Location = "Manufacturing_Location";
    public static final String Product_Refrence_Number = "Product_Refrence_Number";
    public static final String Dispatch_Date = "Dispatch_Date";
    public static final String Destination_Agency = "Destination_Agency";
    public static final String Destination_State = "Destination_State";
    public static final String Destination_Point = "Destination_Point";
    public static final String MRP = "MRP";
    public static final String Serial_No = "Serial_No";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + OnspotProvider.AUTHORITY
            + "/" + TABLE_NAME);


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ImageUrl + " TEXT,"
            + ResponseTime + " TEXT,"
            + ResponseData + " TEXT,"
            + COL_RESPONSE_MESSAGE + " TEXT, " + COL_RESPONSE_TYPE + " TEXT, " + Code + " TEXT, "
            + CPO_NO + " TEXT, " + Cpo_Date + " TEXT, " + So_number_date + " TEXT," + Brand_Name + " TEXT, "
            + Mill_Name + " TEXT, "
            + Statutory_warning + " TEXT, "
            + Procurement_agency + " TEXT, " + Manufacturing_Location + " TEXT, "
            + Product_Refrence_Number + " TEXT, " + Dispatch_Date + " TEXT, "
            + Destination_Agency + " TEXT, " + Destination_State + " TEXT ,"
            + Destination_Point + " TEXT, " + MRP + " TEXT , " + Serial_No + " TEXT);";
}
