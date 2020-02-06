package com.example.original.Server;

import android.os.AsyncTask;
import android.util.Log;

import com.example.original.Item;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Post extends AsyncTask<ArrayList<Item>, Void, Void> {

    private static String POST_URL = "http://10.3.0.54:5000/supermercado/api/v1.0/postorder/";
    private JSONObject postobject;

    public Post(String code){
        POST_URL = POST_URL + code;
    }

    @Override
    protected Void doInBackground(ArrayList<Item>... arrayLists) {

        try {
            URL PostURL = new URL(POST_URL);


            for (Item item: arrayLists[0]){
                HttpURLConnection connection = (HttpURLConnection) PostURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                postobject = new JSONObject();
                postobject.put("id", item.getID());
                postobject.put("title", item.getName());
                postobject.put("quantity", item.getQuantity());
                Log.i("JSON", postobject.toString());
                DataOutputStream post = new DataOutputStream(connection.getOutputStream());
                post.writeBytes(postobject.toString());
                post.flush();
                post.close();
                Log.i("STATUS", String.valueOf(connection.getResponseCode()));
                Log.i("MSG" , connection.getResponseMessage());
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
