package com.example.original.Database.debunk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.original.Item;

import java.text.ParseException;
import java.util.ArrayList;

public class UpdateDatabase {

    private Item mItem = new Item();
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static UpdateDatabase sUpdateDatabase;

    public static UpdateDatabase get(Context context){
        if(sUpdateDatabase == null){
            sUpdateDatabase = new UpdateDatabase(context);
        }

        return sUpdateDatabase;
    }

    public UpdateDatabase(Context context) {
        mContext = context;
        mDatabase = new Items(mContext).getWritableDatabase();
    }

    public void addItem(Item item){
        ContentValues values = getContentValues(item);
        mDatabase.insert(ItemSchema.Item.NAME,null,values);
    }

    public ArrayList<Item> getItem(){
        ArrayList<Item> items = new ArrayList<>();
        ItemWrapper cursor = (ItemWrapper) queryItemInfo(null,null);
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

    public Item getItemInfo(String ID){
        ItemWrapper cursor = (ItemWrapper) queryItemInfo(ItemSchema.Item.NAME + " = ?", new String[]{ID});
        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getItem();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    public void updateItem(Item item){
        String id = item.getID();
        ContentValues values = getContentValues(item);
        mDatabase.update(ItemSchema.Item.NAME, values, ItemSchema.Item.Col.ITEMID + " = ?", new String[]{id});
        //mDatabase.update(ItemSchema.Item.NAME, null, whereClause, whereArgs, null, null,null);
    }

    private Cursor queryItemInfo(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(ItemSchema.Item.NAME, null, whereClause, whereArgs, null, null, null);
        return new ItemWrapper(cursor);
    }

    public static ContentValues getContentValues(Item item){
        ContentValues values = new ContentValues();
        values.put(ItemSchema.Item.Col.ITEMID, item.getID());
        values.put(ItemSchema.Item.Col.ITEM, item.getName());
        //values.put(ItemSchema.Item.Col.CONTENT, item.getContent());
        values.put(ItemSchema.Item.Col.CONTENT, "FUQERY");//for testing
        values.put(ItemSchema.Item.Col.COST, item.getCost());
        //values.put(ItemSchema.Item.Col.QUANTITY, item.getQuantity());
        values.put(ItemSchema.Item.Col.QUANTITY, "100");//for testing
        //values.put(ItemSchema.Item.Col.ISLE_NUM, item.getIsleNum());
        values.put(ItemSchema.Item.Col.ISLE_NUM, "10");// for testing
        values.put(ItemSchema.Item.Col.IMAGE, item.getiImageURL());
        return values;
    }
}
