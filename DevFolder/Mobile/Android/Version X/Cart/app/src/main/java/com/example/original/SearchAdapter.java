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

    private static String ADDTOCART = "addToCart", PLUS = "add1ToQuantity", MINUS = "subtract1FromQuantity";

    private ArrayList<Item> mshoppingList = new ArrayList<>();
    String ATAG = "SupermercadoADAPTER";
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
        searchViewHolder.mQuantity.setText(Integer.toString(currentItem.getQuantity()));
        searchViewHolder.add.setOnClickListener(new MyOnClickListener(i, ADDTOCART));
        searchViewHolder.mAdd.setOnClickListener(new MyOnClickListener(i, PLUS));
        searchViewHolder.mMinus.setOnClickListener(new MyOnClickListener(i, MINUS));
    }

    @Override
    public int getItemCount() {
        if (mshoppingList.isEmpty()) {
            return 0;
        }
        return mshoppingList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private Button mAdd, mMinus, add;
        private TextView mTextView1, mTextView2, mQuantity;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.tv1);
            mTextView2 = itemView.findViewById(R.id.tv2);
            mQuantity = itemView.findViewById(R.id.tvQuantity);
            mAdd = itemView.findViewById(R.id.btnAdd);
            mMinus = itemView.findViewById(R.id.btnMinus);
            add = itemView.findViewById(R.id.addToCart);
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
        private int mQuan;

        public MyOnClickListener(int position, String action){oAction = action; mPosition = position; mItem = mshoppingList.get(mPosition); mQuan = mItem.getQuantity(); }

        @Override
        public void onClick(View v) {

            if (oAction == ADDTOCART){
                addToCart(mshoppingList.get(mPosition));
                mItem.setQuantity(1);
                mshoppingList.set(mPosition, mItem);
                Toast.makeText(mContext, Integer.toString(mQuan) + " " +mshoppingList.get(mPosition).getName() + " added to cart", Toast.LENGTH_SHORT).show();
            }
            if (oAction == PLUS) {
                if (mQuan != mItem.getAmount()){
                    Log.i(ATAG, "Quan = " +Integer.toString(mQuan));
                    mQuan++;
                    Log.i(ATAG, "Quan = " +Integer.toString(mQuan));
                    //mItem.addOne();
                    mItem.setQuantity(mQuan);
                    mshoppingList.set(mPosition, mItem);
                }
            }
            if (oAction == MINUS){
                if (mQuan != 1){
                    mQuan--;
                    //mItem.minusOne();
                    mItem.setQuantity(mQuan);
                    mshoppingList.set(mPosition,mItem);
                }
            }

            notifyDataSetChanged();
        }

        public void addToCart(Item item){
            UpdateCart.get(mContext).addItem(item);
        }

    }


}

