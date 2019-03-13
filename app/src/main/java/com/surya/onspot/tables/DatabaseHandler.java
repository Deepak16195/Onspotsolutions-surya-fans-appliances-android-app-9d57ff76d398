package com.surya.onspot.tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scaned_qr.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db1) {
        try {
            db1.beginTransaction();
            db1.execSQL(TblOnspotQrResults.CREATE_TABLE);
            db1.execSQL(TblInvalidQRresult.CREATE_TABLE);
            db1.setTransactionSuccessful();
            db1.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		/*switch (oldVersion) {
		case 1:
			break;

		default:
			break;
		}*/
        onCreate(db);
    }


}
