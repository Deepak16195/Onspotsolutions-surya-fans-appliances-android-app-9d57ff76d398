package com.surya.onspot.tables;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class OnspotProvider extends ContentProvider {

    //	public static final String AUTHORITY = "mca.onspot.com.tables.OnspotProvider";
//	public static final String AUTHORITY = "com.loyalty.onspot.tables.OnspotProvider";
    public static final String AUTHORITY = "com.surya.onspot.tables.OnspotProvider";
    public static final int Tbl_OnspotQrResults = 0;
    public static final int Tbl_Onspotinvalid = 1;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, TblOnspotQrResults.TABLE_NAME, Tbl_OnspotQrResults);
        sURIMatcher.addURI(AUTHORITY, TblInvalidQRresult.TABLE_NAME, Tbl_Onspotinvalid);
    }

    DatabaseHandler mDb;

    @Override
    public boolean onCreate() {
        mDb = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db1 = mDb.getWritableDatabase();
        int count = 0;
        switch (sURIMatcher.match(uri)) {

            case Tbl_OnspotQrResults:
                count = db1.delete(TblOnspotQrResults.TABLE_NAME, selection,
                        selectionArgs);
                break;

            case Tbl_Onspotinvalid:
                count = db1.delete(TblInvalidQRresult.TABLE_NAME, selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db1 = mDb.getWritableDatabase();
        Uri newUri = null;
        try {
            db1.beginTransaction();
            long row;
            int uriType = sURIMatcher.match(uri);
            switch (uriType) {

                case Tbl_OnspotQrResults:
                    row = db1.insert(TblOnspotQrResults.TABLE_NAME, null, values);
                    break;

                case Tbl_Onspotinvalid:
                    row = db1.insert(TblInvalidQRresult.TABLE_NAME, null, values);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI");
            }
            db1.setTransactionSuccessful();
            newUri = ContentUris.withAppendedId(uri, row);
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db1.endTransaction();
        }
        return newUri;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        try {
            int uriType = sURIMatcher.match(uri);
            switch (uriType) {
                case Tbl_OnspotQrResults:
                    queryBuilder.setTables(TblOnspotQrResults.TABLE_NAME);
                    break;
                case Tbl_Onspotinvalid:
                    queryBuilder.setTables(TblInvalidQRresult.TABLE_NAME);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI");
            }
            cursor = queryBuilder.query(mDb.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db1 = mDb.getWritableDatabase();
        int count = 0;
        try {
            //   db.beginTransaction();
            switch (sURIMatcher.match(uri)) {

                case Tbl_OnspotQrResults:
                    count = db1.update(TblOnspotQrResults.TABLE_NAME, values, selection, selectionArgs);
                    break;

                case Tbl_Onspotinvalid:
                    count = db1.update(TblInvalidQRresult.TABLE_NAME, values, selection, selectionArgs);
                    break;


                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
