package com.surya.onspot.tables;

import android.net.Uri;

public class TblInvalidQRresult {

    public static final String TABLE_NAME = "TblOnspotQrInvalid";

    //TABLE_MESSAGES Column names
    public static final String KEY_ID = "id";
    public static final String ResponseTime = "responsetime";
    public static final String ImageURI = "imageuri";
    public static final String COL_RESPONSE_MESSAGE = "responseMessage1";
    public static final String COL_RESPONSE_TYPE = "responseType1";
    public static final String Code = "qrCode1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + OnspotProvider.AUTHORITY
            + "/" + TABLE_NAME);
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ResponseTime + " TEXT,"
            + ImageURI + " TEXT,"
            + COL_RESPONSE_MESSAGE + " TEXT, " + COL_RESPONSE_TYPE + " TEXT, " + Code + " TEXT);";
}
