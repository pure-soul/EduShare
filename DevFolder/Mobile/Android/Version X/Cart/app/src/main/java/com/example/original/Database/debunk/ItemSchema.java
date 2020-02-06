package com.example.original.Database.debunk;

public class ItemSchema {

    public static final class Item{
        public static final String NAME = "items";

        public static final class Col{
            public static final String ITEMID = "id";
            public static final String ITEM = "item";
            public static final String COST = "cost";
            public static final String CONTENT = "content";
            public static final String IMAGE = "image";
            public static final String QUANTITY = "quantity";
            public static final String ISLE_NUM = "isle_number";//might have to expand
        }
    }
}
