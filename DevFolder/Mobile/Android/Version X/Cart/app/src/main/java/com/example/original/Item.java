package com.example.original;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;
import java.net.URISyntaxException;

public class Item {
    private String iName;
    private String iID;
    private String iContent;
    private String iURL;

    public Item() {
    }

    public Item(String id, String name, String content) {
        iID = id;
        iName = name;
        iContent = content;
    }

    public Item(String id, String name, String content, String image) {
        iID = id;
        iName = name;
        iContent = content;
        iURL = image;
    }

    public void setID(String id) {
        this.iID = id;
    }

    public void setName(String name) {
        this.iName = name;
    }

    public void setContent(String content) {
        this.iContent = content;
    }

    public void setImageURL(String image) {
        this.iURL = image;
    }

    public String getID() {
        return iID;
    }

    public String getName() {
        return iName;
    }

    public Uri getImage() throws URISyntaxException {
        return Uri.parse("iURL");
    }

    public String getContent() {
        return iContent;
    }

    public String getiImageURL(){
        return iURL;
    }
}
