package com.example.original.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QR extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "qr.db";

    public  QR(Context context){super(context, DATABASE_NAME, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase Message) {
        Message.execSQL("create table " + QRSchema.QR.NAME + "(" +
                QRSchema.QR.Col.ID + " primary key" + ", " +
                QRSchema.QR.Col.DATE + ", " +
                QRSchema.QR.Col.PATH + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
