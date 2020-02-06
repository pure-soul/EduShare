package com.example.original.Database.debunk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Items extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "items.db";

    public  Items(Context context){super(context, DATABASE_NAME, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase Message){
        Message.execSQL("create table " + ItemSchema.Item.NAME + "(" +
                ItemSchema.Item.Col.ITEMID + " primary key" + ", " +
                ItemSchema.Item.Col.ITEM + ", " +
                ItemSchema.Item.Col.IMAGE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
