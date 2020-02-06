package com.example.original.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.original.Database.debunk.ItemSchema;
import com.example.original.Item;

import java.text.ParseException;
import java.util.ArrayList;

public class UpdateCart {
    private Item mItem = new Item();
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static UpdateCart sUpdateDatabase;

    public static UpdateCart get(Context context){
        if(sUpdateDatabase == null){
            sUpdateDatabase = new UpdateCart(context);
        }

        return sUpdateDatabase;
    }

    public UpdateCart(Context context) {
        mContext = context;
        mDatabase = new Cart(mContext).getWritableDatabase();
    }

    public void addItem(Item item){
        ContentValues values = getContentValues(item);
        mDatabase.insert(CartSchema.Item.NAME,null,values);
    }

    public ArrayList<Item> getItem(){
        ArrayList<Item> items = new ArrayList<>();
        CartWrapper cursor = (CartWrapper) queryItemInfo(null,null);
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

    public Item getItemInfo(String Name){
        CartWrapper cursor = (CartWrapper) queryItemInfo(CartSchema.Item.NAME + " = ?", new String[]{Name});
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

    public void deleteEntry(Item item){
        String id = item.getID();
        mDatabase.delete(CartSchema.Item.NAME, ItemSchema.Item.Col.ITEMID + " = ?", new String[]{id});
    }

    public void updateItem(Item item){
        String name = item.getName();
        ContentValues values = getContentValues(item);
        mDatabase.update(CartSchema.Item.NAME, values, ItemSchema.Item.Col.ITEM + " = ?", new String[]{name});
    }

    private Cursor queryItemInfo(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(CartSchema.Item.NAME, null, whereClause, whereArgs, null, null, null);
        return new CartWrapper(cursor);
    }

    public static ContentValues getContentValues(Item item){
        ContentValues values = new ContentValues();
        values.put(ItemSchema.Item.Col.ITEMID, item.getID());
        values.put(ItemSchema.Item.Col.ITEM, item.getName());
        values.put(ItemSchema.Item.Col.CONTENT, item.getContent());
        values.put(ItemSchema.Item.Col.IMAGE, item.getiImageURL());
        return values;
    }
}
