package com.example.original;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;
import java.net.URISyntaxException;

public class Item {
    private String iName;
    private String iID;
    private String iContent;
    private float iCost;
    private int iQuantity;
    private int iAvailable;
    private String iImageURL;
    private int iIsleNum;

    public Item() {
        iQuantity = 1;
    }

    public Item(String id, String name, String content, float cost) {
        iID = id;
        iName = name;
        iContent = content;
        iCost = cost;
        iQuantity = 1;
    }

    public Item(String id, String name, String content, float cost, String image) {
        iID = id;
        iName = name;
        iContent = content;
        iCost = cost;
        iImageURL = image;
        iQuantity = 1;
    }

    public Item(String id, String name, String content, float cost, String image, int quan, int isle_num) {
        iID = id;
        iName = name;
        iContent = content;
        iCost = cost;
        iImageURL = image;
        iQuantity = 1;
        iIsleNum = isle_num;
        iAvailable = quan;
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

    public void setCost(float cost) {
        this.iCost = cost;
    }

    public void setImageURL(String image) {
        this.iImageURL = image;
    }

    public String getID() {
        return iID;
    }

    public void setAmount(int quantity){
        iAvailable = quantity;
    }

    public void setQuantity(int quantity){
        iQuantity = quantity;
    }

    public String getName() {
        return iName;
    }

    public int getIsleNum() {
        return iIsleNum;
    }

    public Uri getImage() throws URISyntaxException {
        return Uri.parse("iImageURL");
    }

    public String getContent() {
        return iContent;
    }

    public float getCost() {
        return iCost;
    }

    public int getQuantity(){
        return iQuantity;
    }

    public void setIsleNum(int islenum) {
        iIsleNum = islenum;
    }

    public String getiImageURL(){
        return iImageURL;
    }

    public int getAmount(){
        return iAvailable;
    }

    public String toPay(){
        return Float.toString(iQuantity * iCost);
    }

    public String costAppend(){
        return iName + " x" + Integer.toString(iQuantity);
    }
}
