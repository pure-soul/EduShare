package com.example.original.Database.debunk;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.original.Item;
import com.example.original.R;

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

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>  {


    private ArrayList<Item> mshoppingList;
    private ArrayList<Item> mshoppingListFull;
    private OnItemClickListener mListener;
    String ATAG = "SupermercadoADAPTER";

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onAddClick(int position);

        void onMinusclick(int position);
    }

    public void setOnItemclickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ShoppingViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public Button mAdd;
        public Button mMinus;
        public TextView mTextView1, mTextView2, mQuantity;


        public ShoppingViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            //mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.tv1);
            mTextView2 = itemView.findViewById(R.id.tv2);
            mQuantity = itemView.findViewById(R.id.tvQuantity);
            mAdd = itemView.findViewById(R.id.btnAdd);
            mMinus = itemView.findViewById(R.id.btnMinus);

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onAddClick(position);
                        }
                    }
                }
            });

            mMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onMinusclick(position);
                        }
                    }

                }
            });
        }

    }

    // get data from array list
    public ShoppingAdapter (ArrayList<Item> shoppingList) {
        this.mshoppingList = shoppingList;
        mshoppingListFull = new ArrayList<>(shoppingList);
    }

    public void search(String search){
        new Searcher(search).execute();
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_card, viewGroup, false);
        ShoppingViewHolder svh = new ShoppingViewHolder(v, mListener);
        return svh;

    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder shoppingViewHolder, int i) {

        Item currentItem = mshoppingList.get(i);
        //to get the image from the view holder
//        try {
//            shoppingViewHolder.mImageView.setImageURI(currentItem.getImage());
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        shoppingViewHolder.mTextView1.setText(currentItem.getName());
        shoppingViewHolder.mTextView2.setText(currentItem.getContent());
    }

    @Override
    public int getItemCount() {
        return mshoppingList.size();
    }

    public void filterList(ArrayList<Item> filteredList){

        mshoppingList = filteredList;
        notifyDataSetChanged();
    }

    //Async Task used to communicate with server and retrieve search results
    public class Searcher extends AsyncTask<Void, String, ArrayList<Item>> {

        private final String SEARCH_URL = "http://10.0.2.2:5000/supermercado/api/v1.0/search/"; //connecting
        private String mStringData = "";
        private String mSearchString;
        public ArrayList<Item> RESULTS = new ArrayList<>();

        public Searcher(String search){
            mSearchString = search;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            String JTAG = "SupermercadoJSON";
            String search  = SEARCH_URL + mSearchString;
            try {
                URL searchURL = new URL(search);
                HttpURLConnection connection = (HttpURLConnection) searchURL.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null){
                    line = reader.readLine();
                    mStringData += line;
                }

                //JSONArray resultARRAY = new JSONArray(mStringData);
                JSONObject jsnobject = new JSONObject(mStringData);
                JSONArray resultARRAY = jsnobject.getJSONArray(mSearchString);
                Log.i(JTAG, resultARRAY.toString());
                for(int i = 0; i < resultARRAY.length(); i++){
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

//            for (Item item: this.RESULTS){
//                UpdateDatabase.get(getActivity()).addItem(item);
//            }

            //Log.i(JTAG, ITEMS.toString());
            //ITEMS = RESULTS;
            //Log.i(JTAG, ITEMS.toString());

            mshoppingList = RESULTS;
            return RESULTS;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
            mshoppingList = RESULTS;
        }

        public ArrayList<Item> getRESULTS() {
            return RESULTS;
        }
    }

}