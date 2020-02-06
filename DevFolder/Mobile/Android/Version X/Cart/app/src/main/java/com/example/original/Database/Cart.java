package com.example.original.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.original.Database.debunk.ItemSchema;

public class Cart extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "cart.db";

    public  Cart(Context context){super(context, DATABASE_NAME, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase Message){
        Message.execSQL("create table " + CartSchema.Item.NAME + "(" +
                ItemSchema.Item.Col.ITEMID + " primary key" + ", " +
                ItemSchema.Item.Col.ITEM + ", " +
                ItemSchema.Item.Col.CONTENT + ", " +
                ItemSchema.Item.Col.COST + ", " +
                ItemSchema.Item.Col.QUANTITY + ", " +
                ItemSchema.Item.Col.IMAGE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
