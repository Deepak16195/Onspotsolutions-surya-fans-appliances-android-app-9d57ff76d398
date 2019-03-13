package com.surya.onspot.QRresponse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.surya.onspot.model.AgentListModel;
import com.surya.onspot.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "scaned_qr";

    // ------ QR TABLE
    private final static String TABLE_NAME = "scaned_qr_codes";
    private final static String COL_PRODUCT_IMAGE_URL = "product_image_url";
    private final static String COL_BRAND_IMAGE_URL = "brand_image_url";
    private final static String COL_QR_CODE = "qrCode";
    private final static String COL_RESPONSE_MESSAGE = "responseMessage";
    private final static String COL_MOBILE_NUMBER = "mobile_number";
    private final static String COL_RESPONSE_TYPE = "responseType";
    private final static String COL_CURRENT_TIME = "currentTime";
    private final static String COL_RESPONSE_HASH = "responseHash";

    // ------ Agent TABLE
   private
   final static String TABLE_NAME_AGENT = "agent_list_data";
   private
   final static String AGENT_ID = "id";
   private
   final static String AGENT_CITY = "city";
   private
   final static String AGENT_STATE = "state";
   private
   final static String AGENT_MOBILE = "mobile";
   private
   final static String AGENT_NAME = "name";
   private
   final static String AGENT_TYPE = "type";

   private
   static final String CREATE_TABLE_AGENT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_AGENT
            + "("
            + AGENT_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AGENT_CITY
            + " TEXT, "
            + AGENT_STATE
            + " TEXT, "
            + AGENT_MOBILE
            + " TEXT, "
            + AGENT_NAME
            + " TEXT, "
            + AGENT_TYPE
            + " TEXT"
            + ")";

    // ------ USER TABLE
    private final static String TABLE_NAME_USER_INFO = "registerd_user_table_0695x1";
    private final static String USER_COL_EMAIL = "user_email";
    private final static String USER_COL_NAME = "user_name";
    private final static String USER_COL_NUMBER = "user_number";
    private final static String USER_COL_COUNTRY = "user_country";
    private final static String USER_COL_COUNTRY_CODE = "user_country_code";
    private final static String USER_COL_AUTH_TOKEN = "user_auth_token";
    private final static String USER_COL_LOYALTY_POINT = "loyalty_point";

    // ------ SYSTEM BARCODE
    private static final String TABLE_SYSTEM_BARCODE = "system_barcode";
    private final static String SYSTEM_BARCODE_LIST_ID = "id";
    private final static String SYSTEM_BARCODE_LIST = "list";

    private static final String CREATE_TABLE_SYSTEM_BARCODE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SYSTEM_BARCODE
            + "("
            + SYSTEM_BARCODE_LIST_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SYSTEM_BARCODE_LIST
            + " TEXT"
            + ")";

    //  --- LOGS
    private static final String TABLE_NAME_LOGS = "logs";
    private final static String LOG_ID = "id";
    private final static String LOG_NUMBER_OF_CARTONS = "no_of_cartons";
    private final static String LOG_DATA = "log_data";
    private final static String LOG_DATE = "log_date";
    private final static String LOG_TIME = "log_time";
    private final static String LOG_COMPLETED = "log_completed";
    private final static String LOG_UPLOAD_TO_SERVER = "log_upload_to_server";

    private static final String CREATE_TABLE_LOGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_LOGS
            + "("
            + LOG_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LOG_NUMBER_OF_CARTONS
            + " INTEGER DEFAULT 0,"
            + LOG_DATA
            + " TEXT,"
            + LOG_DATE
            + " TEXT,"
            + LOG_TIME
            + " TEXT,"
            + LOG_COMPLETED
            + " INTEGER DEFAULT 0,"
            + LOG_UPLOAD_TO_SERVER
            + " INTEGER DEFAULT 0"
            + ")";

    // ------ NewShipment
    private static final String TABLE_NAME_SHIPMENT = "newshipment";
    private final static String SHIPMENT_ID = "shipment_id";
    private final static String SHIPMENT_PACKEDRATIO = "packed_ratio";
    private final static String SHIPMENT_CODE = "shipment_code";
    private final static String SHIPMENT_PRODUCTNAME = "product_name";
    private final static String SHIPMENT_UPLOAD_TO_SERVER = "shipment_upload_to_server";

    private static final String CREATE_TABLE_NEWSIPMENT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_SHIPMENT
            + "("
            + SHIPMENT_ID
            + " TEXT,"
            + SHIPMENT_PACKEDRATIO
            + " TEXT,"
            + SHIPMENT_CODE
            + " TEXT,"
            + SHIPMENT_PRODUCTNAME
            + " TEXT,"
            + SHIPMENT_UPLOAD_TO_SERVER
            + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + " ("
                + COL_PRODUCT_IMAGE_URL
                + " TEXT"
                + ","
                + COL_BRAND_IMAGE_URL
                + " TEXT" //
                + ","//
                + COL_QR_CODE
                + " TEXT"
                + ","//
                + COL_RESPONSE_MESSAGE
                + " TEXT" //
                + ","//
                + COL_MOBILE_NUMBER //
                + " NUMERIC" //
                + ","//
                + COL_RESPONSE_TYPE //
                + " TEXT" //
                + ","//
                + COL_CURRENT_TIME //
                + " TEXT" //
                + ","//
                + COL_RESPONSE_HASH //
                + " TEXT" //
                + ")" //
        );
        createUserTable(db);
        createSystemTable(db);
        db.execSQL(CREATE_TABLE_NEWSIPMENT);
        db.execSQL(CREATE_TABLE_LOGS);
        db.execSQL(CREATE_TABLE_AGENT);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_LOGS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_USER_INFO);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_SYSTEM_BARCODE);
        Log.d("-------->", "Table Upgraded");
        onCreate(db);
    }

    private void createSystemTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SYSTEM_BARCODE);
    }

    private void createUserTable(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_USER_INFO
                + " ("
                + USER_COL_EMAIL
                + " TEXT"
                + ","
                + USER_COL_NUMBER
                + " TEXT"
                + ","
                + USER_COL_NAME
                + " TEXT"
                + ","
                + USER_COL_COUNTRY
                + " TEXT"
                + ","
                + USER_COL_COUNTRY_CODE
                + " NUMERIC"
                + ","
                + USER_COL_AUTH_TOKEN
                + " TEXT"
                + ","
                + USER_COL_LOYALTY_POINT
                + " TEXT"
                + ")"
        );
    }

    public List<Hashtable<String, String>> fetchHistoryView() {

        List<Hashtable<String, String>> listHash = new ArrayList<Hashtable<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put(COL_QR_CODE,
                    res.getString(res.getColumnIndex(COL_QR_CODE)));


            hashtable.put(COL_RESPONSE_MESSAGE,
                    res.getString(res.getColumnIndex(COL_RESPONSE_MESSAGE)));

            hashtable.put(COL_RESPONSE_TYPE,
                    res.getString(res.getColumnIndex(COL_RESPONSE_TYPE)));


            hashtable.put(COL_CURRENT_TIME,
                    res.getString(res.getColumnIndex(COL_CURRENT_TIME)));

            listHash.add(hashtable);

            res.moveToNext();

        }

        for (int i = 0; i < listHash.size(); i++) {
//			Set<String> hKeys2 = listHash.get(i).keySet();
//			for (String string : hKeys2) {
////				Log.d("====>", string + " ==>" + listHash.get(i).get(string));
//			}
        }
        return listHash;
    }

    public void insertRegisteredUser(String userEmailValue,
                                     String userMobValue, String userNameValue, String userCountryValue,
                                     String userCountryCodeValue, String auth_token, String loyalty_point) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_EMAIL, userEmailValue);
        contentValues.put(USER_COL_NUMBER, userMobValue);
        contentValues.put(USER_COL_NAME, userNameValue);
        contentValues.put(USER_COL_COUNTRY, userCountryValue);
        contentValues.put(USER_COL_COUNTRY_CODE, userCountryCodeValue);
        contentValues.put(USER_COL_AUTH_TOKEN, auth_token);
        contentValues.put(USER_COL_LOYALTY_POINT, loyalty_point);
        db.insert(TABLE_NAME_USER_INFO, null, contentValues);
    }

    public void updateMobileNO(String userNewNumber, String userOldNumber) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME_USER_INFO + " SET " + USER_COL_NUMBER
                + "='" + userNewNumber + "'");
        Log.d("-----", "UPDATE NO SUCCESSFULLY");
    }

    public void updateEmail(String userNewEmail, String userOldEmail) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME_USER_INFO + " SET " + USER_COL_EMAIL
                + "='" + userNewEmail + "'");
        Log.d("-----", "UPDATE EMAIL SUCCESSFULLY");

    }

    public void updateNAME(String newName, String oldName) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME_USER_INFO + " SET " + USER_COL_NAME + "='"
                + newName + "'");
        Log.d("-----", "UPDATE NAME SUCCESSFULLY");

    }

    public String getAuthToken() {
        String auth_token = "";
        String query = "SELECT * FROM " + TABLE_NAME_USER_INFO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery(query, null);
        objCursor.moveToFirst();
        if (objCursor.getCount() > 0) {
            auth_token = objCursor.getString(objCursor.getColumnIndex(USER_COL_AUTH_TOKEN));
        }
        db.close();
        return auth_token;
    }

    public SystemBarcodeModel getSystemBarcode() {
        SystemBarcodeModel model = null;
        String strQuery = "SELECT * FROM " + TABLE_SYSTEM_BARCODE;
        Utils.out("QUERY : " + strQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery(strQuery, null);
        objCursor.moveToFirst();
        if (objCursor.getCount() > 0) {
            try {
                model = new SystemBarcodeModel(new JSONArray(objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SYSTEM_BARCODE_LIST))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
        return model;
    }

    public long insertSystemBarcode(SystemBarcodeModel object) {
        long result = 0;
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(SYSTEM_BARCODE_LIST, String.valueOf(object.getJsonArray()));
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.insert(TABLE_SYSTEM_BARCODE, null, objContentValues);
        db.close();
        return result;
    }

    public void deleteServerDataTableData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SYSTEM_BARCODE);
        Utils.out("DB" + TABLE_SYSTEM_BARCODE + " : TABLE DELETION FAILED");
        db.close();
    }

    // TABLE NAME LOGS

    public long insertLog(LogDetailModel object) {
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(LOG_NUMBER_OF_CARTONS, object.getNumberOfCartons());
        objContentValues.put(LOG_DATA, object.getScannedData());
        objContentValues.put(LOG_DATE, object.getLogDate());
        objContentValues.put(LOG_TIME, object.getLogTime());
        objContentValues.put(LOG_COMPLETED, object.getLogCompleted());
        objContentValues.put(LOG_UPLOAD_TO_SERVER, object.getLogCompleted());
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME_LOGS, null, objContentValues);
        db.close();
        return result;
    }

    public long updateLog(LogDetailModel object) {
        long result = 0;
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(LOG_NUMBER_OF_CARTONS, object.getNumberOfCartons());
        objContentValues.put(LOG_DATA, object.getScannedData());
        objContentValues.put(LOG_DATE, object.getLogDate());
        objContentValues.put(LOG_TIME, object.getLogTime());
        objContentValues.put(LOG_COMPLETED, object.getLogCompleted());
        objContentValues.put(LOG_UPLOAD_TO_SERVER, object.getLogUploadToServerStatus());
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = LOG_ID + "=?";
        String whereArgs[] = {String.valueOf(object.getLogID())};
        result = db.update(TABLE_NAME_LOGS, objContentValues, whereClause, whereArgs);
        db.close();
        return result;
    }

    public LogDetailModel getCurrentLogDetails(LogDetailModel object) {
        LogDetailModel logDetails = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_LOGS + " WHERE " + LOG_DATE + " = '" + object.getLogDate() + "'", null);
        objCursor.moveToFirst();
        logDetails = new LogDetailModel(
                objCursor.getInt(objCursor.getColumnIndex(LOG_ID)),
                objCursor.getInt(objCursor.getColumnIndex(LOG_NUMBER_OF_CARTONS)),
                objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_DATA)),
                objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_DATE)),
                objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_TIME)),
                objCursor.getInt(objCursor.getColumnIndex(DatabaseHelper.LOG_COMPLETED)),
                objCursor.getInt(objCursor.getColumnIndex(DatabaseHelper.LOG_UPLOAD_TO_SERVER))
        );
        db.close();
        return logDetails;
    }

    public boolean discardEntry(LogDetailModel object) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME_LOGS, LOG_ID + "=" + object.getLogID(), null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateSystemBarcodeTable(LogDetailModel object) {
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(SYSTEM_BARCODE_LIST, object.getScannedData());
        int recordID = getRecordId();
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = SYSTEM_BARCODE_LIST_ID + "=?";
        String whereArgs[] = {String.valueOf(recordID)};
        db.update(TABLE_SYSTEM_BARCODE, objContentValues, whereClause, whereArgs);
        db.close();
    }

    public void updateSystemBarcodeTable(SystemBarcodeModel object) {
        Utils.out("DB : DISCARD ENTRY SYSTEM BARCODE : " + String.valueOf(object.getJsonArray()));
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(SYSTEM_BARCODE_LIST, String.valueOf(object.getJsonArray()));
        int recordID = getRecordId();
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = SYSTEM_BARCODE_LIST_ID + "=?";
        String whereArgs[] = {String.valueOf(recordID)};
        db.update(TABLE_SYSTEM_BARCODE, objContentValues, whereClause, whereArgs);
        db.close();
    }

    private int getRecordId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_SYSTEM_BARCODE, null);
        objCursor.moveToFirst();
        int id = objCursor.getInt(objCursor.getColumnIndex(SYSTEM_BARCODE_LIST_ID));
        objCursor.close();
        db.close();
        return id;
    }

    public ArrayList<LogDetailModel> getNotUploadedLogDetails() {
        ArrayList<LogDetailModel> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_LOGS + " WHERE " + DatabaseHelper.LOG_UPLOAD_TO_SERVER + " = " + 0, null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(new LogDetailModel(
                        objCursor.getInt(objCursor.getColumnIndex(LOG_ID)),
                        objCursor.getInt(objCursor.getColumnIndex(LOG_NUMBER_OF_CARTONS)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_DATA)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_DATE)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.LOG_TIME)),
                        objCursor.getInt(objCursor.getColumnIndex(DatabaseHelper.LOG_COMPLETED)),
                        objCursor.getInt(objCursor.getColumnIndex(DatabaseHelper.LOG_UPLOAD_TO_SERVER))
                ));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

    public void deleteOfflineLogs() {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        count = db.delete(TABLE_NAME_LOGS, null, null);
//        db.execSQL("DELETE FROM " + TABLE_NAME_LOGS);
        if (count > 0) {
            Utils.out("DB" + TABLE_NAME_LOGS + " : TABLE DELETED SUCCESSFULLY");
        } else {
            Utils.out("DB" + TABLE_NAME_LOGS + " : TABLE DELETION FAILED");
        }
        db.close();
    }


//  D work *******************************************************************************************

    public ArrayList<NewShipmentDetailModel> getShipmentSystemBarcode() {
        ArrayList<NewShipmentDetailModel> newShipmentDetailModelArrayList = new ArrayList<>();
        NewShipmentDetailModel model = null;

        String strQuery = "SELECT * FROM " + TABLE_NAME_SHIPMENT;
        Utils.out("QUERY : " + strQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery(strQuery, null);
        objCursor.moveToFirst();
        if (objCursor.getCount() > 0) {
            do {
                newShipmentDetailModelArrayList.add(new NewShipmentDetailModel(objCursor.getString(
                        objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_ID)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PACKEDRATIO)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_CODE)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PRODUCTNAME)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_UPLOAD_TO_SERVER))));
            } while (objCursor.moveToNext());
        }
        db.close();
        return newShipmentDetailModelArrayList;
    }


    public void insertShipment(NewShipmentDetailModel object) {
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(SHIPMENT_ID, object.getNewShipmentID());
        objContentValues.put(SHIPMENT_PACKEDRATIO, object.getPackedRatio());
        objContentValues.put(SHIPMENT_CODE, object.getShipmentCode());
        objContentValues.put(SHIPMENT_PRODUCTNAME, object.getProductName());
        objContentValues.put(SHIPMENT_UPLOAD_TO_SERVER, object.getUploadToServer());
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME_SHIPMENT, null, objContentValues);
        db.close();
    }

    public long updateShipment(String qrcode, String object) {
        long result = 0;
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(SHIPMENT_UPLOAD_TO_SERVER, object);
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = SHIPMENT_CODE + "=?";
        String whereArgs[] = {qrcode};
        result = db.update(TABLE_NAME_SHIPMENT, objContentValues, whereClause, whereArgs);
        db.close();
        return result;
    }

    public boolean getCurrentShipmentDetails(String object) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_SHIPMENT + " WHERE " + SHIPMENT_CODE + " = '" + object + "'", null);
        objCursor.moveToFirst();
        boolean isScan = objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_UPLOAD_TO_SERVER)).equalsIgnoreCase("0");
        db.close();
        return isScan;
    }

    public boolean discardShipmentEntry(NewShipmentDetailModel object) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME_SHIPMENT, SHIPMENT_ID + "=" + object.getNewShipmentID(), null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<NewShipmentDetailModel> getNotScanShipmentDetails() {
        ArrayList<NewShipmentDetailModel> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_SHIPMENT + " WHERE " + DatabaseHelper.SHIPMENT_UPLOAD_TO_SERVER + " = " + 1, null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(new NewShipmentDetailModel(
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_ID)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PACKEDRATIO)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_CODE)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PRODUCTNAME)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_UPLOAD_TO_SERVER))
                ));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

    public void updateShipmentData() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME_SHIPMENT + " SET " + SHIPMENT_UPLOAD_TO_SERVER + " = '1'");

        Log.d("-----", "UPDATE EMAIL SUCCESSFULLY");
        db.close();
    }

    public ArrayList<NewShipmentDetailModel> getScanShipmentDetails() {
        ArrayList<NewShipmentDetailModel> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_SHIPMENT + " WHERE " + SHIPMENT_UPLOAD_TO_SERVER + " = " + 0, null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(new NewShipmentDetailModel(
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_ID)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PACKEDRATIO)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_CODE)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_PRODUCTNAME)),
                        objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_UPLOAD_TO_SERVER))
                ));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

    public ArrayList<String> getScanShipmentBarcode() {
        ArrayList<String> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_SHIPMENT + " WHERE " + SHIPMENT_UPLOAD_TO_SERVER + " = " + 0, null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(objCursor.getString(objCursor.getColumnIndex(DatabaseHelper.SHIPMENT_CODE)));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

    public ArrayList<NewShipmentDetailModel> getListScanedShipment() {
        ArrayList<NewShipmentDetailModel> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        SELECT *, SUM(packed_ratio) as packed_ratio FROM newshipment where shipment_upload_to_server = 1 GROUP BY product_name
        Cursor objCursor = db.rawQuery("SELECT " + SHIPMENT_ID + "," + SHIPMENT_UPLOAD_TO_SERVER + "," + SHIPMENT_CODE + "," + SHIPMENT_PRODUCTNAME + ",SUM(" + SHIPMENT_PACKEDRATIO + ") as " + SHIPMENT_PACKEDRATIO + " FROM " + TABLE_NAME_SHIPMENT + " WHERE " + SHIPMENT_UPLOAD_TO_SERVER + " = 0" + " GROUP BY " + SHIPMENT_PRODUCTNAME, null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(new NewShipmentDetailModel(
                        objCursor.getString(objCursor.getColumnIndex(SHIPMENT_ID)),
                        objCursor.getString(objCursor.getColumnIndex(SHIPMENT_PACKEDRATIO)),
                        objCursor.getString(objCursor.getColumnIndex(SHIPMENT_CODE)),
                        objCursor.getString(objCursor.getColumnIndex(SHIPMENT_PRODUCTNAME)),
                        objCursor.getString(objCursor.getColumnIndex(SHIPMENT_UPLOAD_TO_SERVER))
                ));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

    public String getTotalShipmentCount() {
        String ScanCount = "0";
        SQLiteDatabase db = this.getReadableDatabase();
//        SELECT *, SUM(packed_ratio) as packed_ratio FROM newshipment where shipment_upload_to_server = 1 GROUP BY product_name
        Cursor objCursor = db.rawQuery("SELECT SUM(" + SHIPMENT_PACKEDRATIO + ") as " + SHIPMENT_PACKEDRATIO + " FROM " + TABLE_NAME_SHIPMENT +" WHERE "+SHIPMENT_UPLOAD_TO_SERVER + " == '0'" , null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                ScanCount = objCursor.getString(objCursor.getColumnIndex(SHIPMENT_PACKEDRATIO));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + ScanCount);
        return ScanCount;
    }


    public void deleteOfflineShipment() {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        count = db.delete(TABLE_NAME_SHIPMENT, null, null);
//        db.execSQL("DELETE FROM " + TABLE_NAME_SHIPMENT);
        if (count > 0) {
            Utils.out("DB" + TABLE_NAME_SHIPMENT + " : TABLE DELETED SUCCESSFULLY");
        } else {
            Utils.out("DB" + TABLE_NAME_SHIPMENT + " : TABLE DELETION FAILED");
        }
        db.close();
    }


//    ************************************************************************************************************


    public long insertAgentList(AgentListModel object) {
        ContentValues objContentValues = new ContentValues();
        objContentValues.put(AGENT_ID, object.getId());
        objContentValues.put(AGENT_CITY, object.getCity());
        objContentValues.put(AGENT_STATE, object.getState());
        objContentValues.put(AGENT_MOBILE, object.getMobile());
        objContentValues.put(AGENT_NAME, object.getName());
        objContentValues.put(AGENT_TYPE, object.getType());
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME_AGENT, null, objContentValues);
        db.close();
        return result;
    }

    public ArrayList<AgentListModel> getAgentList(String object) {
        ArrayList<AgentListModel> arrLogDetailModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor objCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_AGENT + " WHERE " + AGENT_TYPE + " = '" + object+"'", null);
        if (objCursor.getCount() > 0) {
            objCursor.moveToFirst();
            do {
                arrLogDetailModels.add(new AgentListModel(
                        objCursor.getString(objCursor.getColumnIndex(AGENT_ID)),
                        objCursor.getString(objCursor.getColumnIndex(AGENT_CITY)),
                        objCursor.getString(objCursor.getColumnIndex(AGENT_STATE)),
                        objCursor.getString(objCursor.getColumnIndex(AGENT_MOBILE)),
                        objCursor.getString(objCursor.getColumnIndex(AGENT_NAME)),
                        objCursor.getString(objCursor.getColumnIndex(AGENT_TYPE))
                ));
            } while (objCursor.moveToNext());
        }
        objCursor.close();
        db.close();
        Utils.out("DB : OFFLINE LOGS : " + arrLogDetailModels.size());
        return arrLogDetailModels;
    }

}
