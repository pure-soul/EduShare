package com.example.original;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.original.Database.debunk.ShoppingAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LocateAdapter extends RecyclerView.Adapter<LocateAdapter.LocateViewHolder> {


    private ArrayList<Item> mLocateList;
    private ShoppingAdapter.OnItemClickListener mListener;

    public void setOnItemclickListener(ShoppingAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class LocateViewHolder extends RecyclerView.ViewHolder{


        public ImageView mImageView;
        public TextView mName;
        public TextView mIsleNum;

        public LocateViewHolder(@NonNull View itemView, final ShoppingAdapter.OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.locate_image);
            mName = itemView.findViewById(R.id.tvlocate1);
            mIsleNum = itemView.findViewById(R.id.tvIsleNum);
        }
    }

    public LocateAdapter(ArrayList<Item> shoppingList) {
        this.mLocateList = shoppingList;
    }

    @NonNull
    @Override
    public LocateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locate_card, viewGroup, false);
        LocateViewHolder lvh = new LocateViewHolder(v, mListener);
        return lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LocateViewHolder locateViewHolder, int i) {

        Item currentItem = mLocateList.get(i);
        try {
            locateViewHolder.mImageView.setImageURI(currentItem.getImage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        locateViewHolder.mName.setText(currentItem.getName());
        locateViewHolder.mIsleNum.setText(Integer.toString(currentItem.getIsleNum()));
    }

    @Override
    public int getItemCount() {
        if (mLocateList.isEmpty()) {
            return 0;
        }
        return mLocateList.size();
    }

    public void addList(ArrayList<Item> items){
        mLocateList = items;
        notifyDataSetChanged();
    }

}
