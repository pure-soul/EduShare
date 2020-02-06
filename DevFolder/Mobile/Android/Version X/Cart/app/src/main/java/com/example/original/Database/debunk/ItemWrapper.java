package com.example.original.Database.debunk;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.original.Item;
import java.text.ParseException;

public class ItemWrapper extends CursorWrapper {

    public  ItemWrapper(Cursor cursor){super(cursor);}

    public Item getItem() throws ParseException {

        Item item;
        String item_id = getString(getColumnIndex(ItemSchema.Item.Col.ITEMID));
        String item_name = getString(getColumnIndex(ItemSchema.Item.Col.ITEM));
        String content = getString(getColumnIndex(ItemSchema.Item.Col.CONTENT));
        String cost = getString(getColumnIndex(ItemSchema.Item.Col.COST));
        String image  =  getString(getColumnIndex(ItemSchema.Item.Col.IMAGE));
        String quantity  =  getString(getColumnIndex(ItemSchema.Item.Col.QUANTITY));
        String isle_num  =  getString(getColumnIndex(ItemSchema.Item.Col.ISLE_NUM));

        item = new Item(item_id, item_name, content, Float.parseFloat(cost), image, Integer.parseInt(quantity), Integer.parseInt(isle_num));

        return item;
    }
}
