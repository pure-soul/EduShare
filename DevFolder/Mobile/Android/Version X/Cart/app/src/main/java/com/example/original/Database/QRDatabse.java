package com.example.original.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.original.QRCode;

import java.text.ParseException;
import java.util.ArrayList;

public class QRDatabse {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static QRDatabse sQRDatabase;

    public static QRDatabse get(Context context){
        if(sQRDatabase == null){
            sQRDatabase = new QRDatabse(context);
        }

        return sQRDatabase;
    }

    public QRDatabse(Context context) {
        mContext = context;
        mDatabase = new QR(mContext).getWritableDatabase();
    }

    private Cursor queryItemInfo(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(QRSchema.QR.NAME, null, whereClause, whereArgs, null, null, null);
        return new QRWrapper(cursor);
    }

    public ArrayList<QRCode> getItem(){
        ArrayList<QRCode> items = new ArrayList<>();
        QRWrapper cursor = (QRWrapper) queryItemInfo(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return items;
    }

    public static ContentValues getContentValues(QRCode qrcode){
        ContentValues values = new ContentValues();
        values.put(QRSchema.QR.Col.ID, qrcode.getUUID());
        values.put(QRSchema.QR.Col.DATE, qrcode.getDateString());
        values.put(QRSchema.QR.Col.PATH, qrcode.getSavePath());
        return values;
    }

    public void addItem(QRCode item){
        ContentValues values = getContentValues(item);
        mDatabase.insert(QRSchema.QR.NAME,null,values);
    }
}
