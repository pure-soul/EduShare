package com.example.original;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original.Database.UpdateCart;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    public SearchAdapter(Context context) {
        this.mContext = context;
    }

    private static String ADDTOLIBRARY = "addToLibrary";

    private ArrayList<Item> mshoppingList = new ArrayList<>();
    String ATAG = "EduShareADAPTER";
    private Context mContext;

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_card, viewGroup, false);
        SearchViewHolder svh = new SearchViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, final int i) {
        final Item currentItem = mshoppingList.get(i);
        searchViewHolder.mTextView1.setText(currentItem.getName());
        searchViewHolder.mTextView2.setText(currentItem.getContent());
        searchViewHolder.add.setOnClickListener(new MyOnClickListener(i, ADDTOLIBRARY));
    }

    @Override
    public int getItemCount() {
        if (mshoppingList.isEmpty()) {
            return 0;
        }
        return mshoppingList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private Button add;
        private TextView mTextView1, mTextView2;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.tv1);
            mTextView2 = itemView.findViewById(R.id.tv2);
            add = itemView.findViewById(R.id.addToLibrary);
        }
    }


    public void addList(ArrayList<Item> items){
        mshoppingList = items;
        notifyDataSetChanged();
    }

    //Personal OnClickListener
    class MyOnClickListener implements View.OnClickListener {

        private String oAction;
        private int mPosition;
        private Item mItem;

        public MyOnClickListener(int position, String action){oAction = action; mPosition = position; mItem = mshoppingList.get(mPosition);}

        @Override
        public void onClick(View v) {

            if (oAction == ADDTOLIBRARY){
                //addToLibrary(mItem); <--needs fixing
                Toast.makeText(mContext, mItem.getName() + " added to Libtrary", Toast.LENGTH_SHORT).show();
            }

            notifyDataSetChanged();
        }

        public void addToLibrary(Item item){
            UpdateCart.get(mContext).addItem(item);
        }

    }


}

