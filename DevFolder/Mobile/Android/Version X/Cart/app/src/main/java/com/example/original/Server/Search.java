package com.example.original.Server;

import android.os.AsyncTask;
import android.util.Log;

import com.example.original.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Search extends AsyncTask<Void, String, ArrayList<Item>> {

    private final String SEARCH_URL = "http://10.3.0.54:5000/supermercado/api/v1.0/search/"; //connecting
    private String mStringData = "";
    private String mSearchString;
    public ArrayList<Item> RESULTS = new ArrayList<>();

    public Search(String search) {
        mSearchString = search.toLowerCase();
    }

    @Override
    protected ArrayList<Item> doInBackground(Void... params) {
        String JTAG = "SupermercadoJSON";
        String search = SEARCH_URL + mSearchString;
        ArrayList<Item> list = new ArrayList<>();

        try {
            URL searchURL = new URL(search);
            HttpURLConnection connection = (HttpURLConnection) searchURL.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line != null) {
                line = reader.readLine();
                mStringData += line;
            }

            JSONObject jsnobject = new JSONObject(mStringData);
            JSONArray resultARRAY = jsnobject.getJSONArray(mSearchString);
            Log.i(JTAG, resultARRAY.toString());
            for (int i = 0; i < resultARRAY.length(); i++) {
                Item item = new Item();
                JSONObject searchItem = (JSONObject) resultARRAY.get(i);
                Log.i(JTAG, searchItem.toString());
                item.setName(searchItem.getString("title"));
                Log.i(JTAG, searchItem.getString("title"));
                item.setImageURL(searchItem.getString("image"));
                Log.i(JTAG, searchItem.getString("image"));
                item.setID(searchItem.getString("id"));
                Log.i(JTAG, searchItem.getString("id"));
                item.setContent(searchItem.getString("content"));
                Log.i(JTAG, searchItem.getString("content"));
                Log.i(JTAG, item.toString());
                list.add(item);
                Log.i(JTAG, list.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<Item> items) {
        super.onPostExecute(items);
        RESULTS = items;
    }

    public ArrayList<Item> getRESULTS() {
        return RESULTS;
    }
}