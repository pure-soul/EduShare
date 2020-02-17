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
        String image  =  getString(getColumnIndex(ItemSchema.Item.Col.IMAGE));

        item = new Item(item_id, item_name, content, image);

        return item;
    }
}
