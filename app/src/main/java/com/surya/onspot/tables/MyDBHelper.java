package com.surya.onspot.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "scaned_qr";

    // ------ QR TABLE
    public final static String TABLE_NAME = "scaned_qr_codes";
    public final static String COL_PRODUCT_IMAGE_URL = "product_image_url";
    public final static String COL_BRAND_IMAGE_URL = "brand_image_url";
    public final static String COL_QR_CODE = "qrCode";
    public final static String COL_RESPONSE_MESSAGE = "responseMessage";
    public final static String COL_MOBILE_NUMBER = "mobile_number";
    public final static String COL_RESPONSE_TYPE = "responseType";
    public final static String COL_CURRENT_TIME = "currentTime";
    public final static String COL_RESPONSE_HASH = "responseHash";

    // ------ USER TABLE
    public final static String USER_TABLE_NAME = "registerd_user_table_0695x1";
    public final static String USER_COL_EMAIL = "user_email";
    public final static String USER_COL_NAME = "user_name";
    public final static String USER_COL_NUMBER = "user_number";
    public final static String USER_COL_COUNTRY = "user_country";
    public final static String USER_COL_COUNTRY_CODE = "user_country_code";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
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
    }

    private void createUserTable(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE "//
                + USER_TABLE_NAME //
                + " (" //

                + USER_COL_EMAIL //
                + " TEXT" //
                + ","//

                + USER_COL_NUMBER //
                + " TEXT" //
                + ","//

                + USER_COL_NAME //
                + " TEXT" //
                + ","//

                + USER_COL_COUNTRY //
                + " TEXT" //
                + ","//

                + USER_COL_COUNTRY_CODE //
                + " NUMERIC" //

                + ")" //
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        Log.d("-------->", "Table Upgraded");
        onCreate(db);
    }

    //	public void fetchData() {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
//		res.moveToFirst();
//		while (res.isAfterLast() == false) {
//			Log.d("-------->",
//					COL_PRODUCT_IMAGE_URL
//							+ "="
//							+ res.getString(res
//									.getColumnIndex(COL_PRODUCT_IMAGE_URL)));
//			Log.d("-------->",
//					COL_BRAND_IMAGE_URL
//							+ "="
//							+ res.getString(res
//									.getColumnIndex(COL_BRAND_IMAGE_URL)));
//			Log.d("-------->",
//					COL_QR_CODE + "="
//							+ res.getString(res.getColumnIndex(COL_QR_CODE)));
//			Log.d("-------->",
//					COL_RESPONSE_MESSAGE
//							+ "="
//							+ res.getString(res
//									.getColumnIndex(COL_RESPONSE_MESSAGE)));
//			Log.d("-------->",
//					COL_MOBILE_NUMBER
//							+ "="
//							+ res.getString(res
//									.getColumnIndex(COL_MOBILE_NUMBER)));
//			Log.d("-------->",
//					COL_RESPONSE_TYPE
//							+ "="
//							+ res.getString(res
//									.getColumnIndex(COL_RESPONSE_TYPE)));
//			res.moveToNext();
//		}
//	}
//
//	public void insertResponse(Hashtable<String, String> responseKeyValue,
//			String responseHashToSave) {
//		// TODO Auto-generated method stub
//		SQLiteDatabase db = this.getWritableDatabase();
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(COL_PRODUCT_IMAGE_URL,
//				responseKeyValue.get(COL_PRODUCT_IMAGE_URL));
//		contentValues.put(COL_BRAND_IMAGE_URL,
//				responseKeyValue.get(COL_BRAND_IMAGE_URL));
//		contentValues.put(COL_QR_CODE, responseKeyValue.get(COL_QR_CODE));
//		contentValues.put(COL_RESPONSE_MESSAGE,
//				responseKeyValue.get(COL_RESPONSE_MESSAGE));
//		contentValues.put(COL_MOBILE_NUMBER,
//				responseKeyValue.get(COL_MOBILE_NUMBER));
//		contentValues.put(COL_RESPONSE_TYPE,
//				responseKeyValue.get(COL_RESPONSE_TYPE));
//		contentValues.put(COL_CURRENT_TIME,
//				responseKeyValue.get(COL_CURRENT_TIME));
//		contentValues.put(COL_RESPONSE_HASH, responseHashToSave);
//		db.insert(TABLE_NAME, null, contentValues);
//
//		Log.d("-------->", "Row Inserted");
//
//	}
//
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
//				Log.d("====>", string + " ==>" + listHash.get(i).get(string));
//			}
        }


        return listHash;

    }

    public void insertRegisteredUser(String userEmailValue,
                                     String userMobValue, String userNameValue, String userCountryValue,
                                     String userCountryCodeValue) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_EMAIL, userEmailValue);
        contentValues.put(USER_COL_NUMBER, userMobValue);
        contentValues.put(USER_COL_NAME, userNameValue);
        contentValues.put(USER_COL_COUNTRY, userCountryValue);
        contentValues.put(USER_COL_COUNTRY_CODE, userCountryCodeValue);
        db.insert(USER_TABLE_NAME, null, contentValues);

    }

//	public List<Hashtable<String, String>> fetchRegisterdData() {
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		Hashtable<String, String> hashtable = new Hashtable<String, String>();
//		List<Hashtable<String, String>> listHash = new ArrayList<Hashtable<String, String>>();
//
//		Cursor res = db.rawQuery("select * from " + TABLE_NAME_USER_INFO, null);
//		res.moveToFirst();
//		while (res.isAfterLast() == false) {
//
//			hashtable.put(USER_COL_EMAIL,
//					res.getString(res.getColumnIndex(USER_COL_EMAIL)));
//			hashtable.put(USER_COL_NUMBER,
//					res.getString(res.getColumnIndex(USER_COL_NUMBER)));
//			hashtable.put(USER_COL_NAME,
//					res.getString(res.getColumnIndex(USER_COL_NAME)));
//			hashtable.put(USER_COL_COUNTRY,
//					res.getString(res.getColumnIndex(USER_COL_COUNTRY)));
//			hashtable.put(USER_COL_COUNTRY_CODE,
//					res.getString(res.getColumnIndex(USER_COL_COUNTRY_CODE)));
//			listHash.add(hashtable);
//			res.moveToNext();
//
//		}
//		return listHash;
//	}
//
//	public void clearAllHistory() {
//		// TODO Auto-generated method stub
//		SQLiteDatabase db = this.getWritableDatabase();
//		db.execSQL("delete from " + TABLE_NAME);
//	}
//
//	public Hashtable<String, String> fetchFullHistory(String qrCode,
//			String qrDate) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Hashtable<String, String> hashtable = new Hashtable<String, String>();
//		Cursor res = db.rawQuery("select * from "//
//				+ TABLE_NAME //
//				+ " where "//
//				+ COL_QR_CODE //
//				+ " = " //
//				+ "'" + qrCode + "'"//
//				+ " and " //
//				+ COL_CURRENT_TIME //
//				+ " = " //
//				+ "'" + qrDate + "'", null);
//		res.moveToFirst();
//		while (res.isAfterLast() == false) {
//
//			hashtable.put(COL_PRODUCT_IMAGE_URL,
//					res.getString(res.getColumnIndex(COL_PRODUCT_IMAGE_URL)));
//			hashtable.put(COL_RESPONSE_MESSAGE,
//					res.getString(res.getColumnIndex(COL_RESPONSE_MESSAGE)));
//			hashtable.put(COL_CURRENT_TIME,
//					res.getString(res.getColumnIndex(COL_CURRENT_TIME)));
//			hashtable.put(COL_RESPONSE_HASH,
//					res.getString(res.getColumnIndex(COL_RESPONSE_HASH)));
//			res.moveToNext();
//
//		}
//
//		return hashtable;
//	}


    public void updateMobileNO(String userNewNumber, String userOldNumber) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + USER_TABLE_NAME + " SET " + USER_COL_NUMBER
                + "='" + userNewNumber + "'");
        Log.d("-----", "UPDATE NO SUCCESSFULLY");
    }

    public void updateEmail(String userNewEmail, String userOldEmail) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + USER_TABLE_NAME + " SET " + USER_COL_EMAIL
                + "='" + userNewEmail + "'");
        Log.d("-----", "UPDATE EMAIL SUCCESSFULLY");

    }

    public void updateNAME(String newName, String oldName) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + USER_TABLE_NAME + " SET " + USER_COL_NAME + "='"
                + newName + "'");
        Log.d("-----", "UPDATE NAME SUCCESSFULLY");

    }
}
