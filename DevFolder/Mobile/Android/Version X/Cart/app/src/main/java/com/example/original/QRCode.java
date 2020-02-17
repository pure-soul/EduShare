package com.example.original;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class QRCode {
    private UUID quuid;
    private Date qDate;
    private String qDateString;
    private String qSavePath;
    private Context qContext;
    private DateFormat qFormat = new SimpleDateFormat("MMMM d, yyyy HH,MM", Locale.ENGLISH);
    private Bitmap qBitmap;

    public QRCode(){
    }

    public QRCode(Context context){
        qContext = context;
        quuid = UUID.randomUUID();
        Calendar cal = Calendar.getInstance();
        qDate = cal.getTime();
        qDateString = qFormat.format(qDate);
    }

    public DateFormat getFormat() {
        return qFormat;
    }

    public void setBitmap(Bitmap qBitmap) {
        this.qBitmap = qBitmap;
    }

    public Bitmap getBitmap() {
        return qBitmap;
    }

    public String getDateString() {
        return qDateString;
    }

    public String getSavePath() {
        return qSavePath;
    }

    public String getUUID() {
        return quuid.toString();
    }

    public void setSavePath(String path) {
        this.qSavePath = path;
    }

    public void setDate(String date){
        try {
            qDate = qFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setID(String uuid) {
        this.quuid = UUID.fromString(uuid);
    }

    public String saveToInternalStorage(){//TODO: DELETE
        ContextWrapper cw = new ContextWrapper(qContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("PickUp", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,getDateString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            qBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
