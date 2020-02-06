package com.example.original;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class LocateFragment extends Fragment {

    private ArrayList<Item> mLocateList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LocateAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView mSearch;
    private Searcher mSearcher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locate, container, false);


        //search
        mSearch = v.findViewById(R.id.searchbar);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

        //Recycler view
        mRecyclerView = v.findViewById(R.id.recylerViewLocate);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new LocateAdapter(mLocateList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    private void filter(String text) {
        if(mSearcher != null && mSearcher.getStatus() == AsyncTask.Status.RUNNING){
            mSearcher.cancel(true);
        }
        mSearcher = (Searcher) new Searcher(text).execute();
    }

    // ASYNC TASK Used To Fetch List of Items According To Search
    public class Searcher extends AsyncTask<Void, String, ArrayList<Item>> {

        private final String SEARCH_URL = "http://10.3.0.54:5000/supermercado/api/v1.0/search/"; //connecting
        private String mStringData = "";
        private String mSearchString;
        public ArrayList<Item> RESULTS = new ArrayList<>();

        public Searcher(String search) {
            mSearchString = search.toLowerCase();
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            String JTAG = "SupermercadoJSON";
            String search = SEARCH_URL + mSearchString;
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
                    item.setCost((float) searchItem.getDouble("price"));
                    Log.i(JTAG, searchItem.getString("price"));
                    item.setImageURL(searchItem.getString("image"));
                    Log.i(JTAG, searchItem.getString("image"));
                    item.setID(searchItem.getString("id"));
                    Log.i(JTAG, searchItem.getString("id"));
                    item.setContent(searchItem.getString("content"));
                    Log.i(JTAG, searchItem.getString("content"));
                    item.setAmount(searchItem.getInt("quantity"));
                    Log.i(JTAG, searchItem.getString("quantity"));
                    item.setIsleNum(searchItem.getInt("isle_no"));
                    Log.i(JTAG, searchItem.getString("isle_no"));
                    Log.i(JTAG, item.toString());
                    this.RESULTS.add(item);
                    Log.i(JTAG, RESULTS.toString());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return RESULTS;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
            mLocateList = items;
            mAdapter.addList(mLocateList);
        }
    }
}

