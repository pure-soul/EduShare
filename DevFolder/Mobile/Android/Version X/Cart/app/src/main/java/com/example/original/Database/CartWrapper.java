package com.example.original.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.original.Database.debunk.ItemSchema;
import com.example.original.Item;

import java.text.ParseException;

public class CartWrapper extends CursorWrapper {

    public  CartWrapper(Cursor cursor){super(cursor);}

    public Item getItem() throws ParseException {

        Item item;
        String item_id = getString(getColumnIndex(ItemSchema.Item.Col.ITEMID));
        String item_name = getString(getColumnIndex(ItemSchema.Item.Col.ITEM));
        String content = getString(getColumnIndex(ItemSchema.Item.Col.CONTENT));
        String cost = getString(getColumnIndex(ItemSchema.Item.Col.COST));
        String image  =  getString(getColumnIndex(ItemSchema.Item.Col.IMAGE));
        String quantity  =  getString(getColumnIndex(ItemSchema.Item.Col.QUANTITY));

        item = new Item();
        item.setID(item_id);
        item.setName(item_name);
        item.setContent(content);
        item.setCost(Float.parseFloat(cost));
        item.setImageURL(image);
        item.setQuantity(Integer.parseInt(quantity));

        return item;
    }
}
