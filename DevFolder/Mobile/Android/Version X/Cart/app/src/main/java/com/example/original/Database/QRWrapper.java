package com.example.original.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.original.QRCode;

import java.text.ParseException;

public class QRWrapper extends CursorWrapper {

    public  QRWrapper(Cursor cursor){super(cursor);}

    public QRCode getItem() throws ParseException {

        QRCode qrcode = new QRCode();
        String id = getString(getColumnIndex(QRSchema.QR.Col.ID));
        String date = getString(getColumnIndex(QRSchema.QR.Col.DATE));
        String path = getString(getColumnIndex(QRSchema.QR.Col.PATH));

        qrcode.setSavePath(path);
        qrcode.setDate(date);
        qrcode.setID(id);

        return qrcode;
    }
}
