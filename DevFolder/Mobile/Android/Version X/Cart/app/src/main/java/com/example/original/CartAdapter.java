package com.example.original;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original.Database.UpdateCart;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {


    private Context context;
    public static ArrayList<Item> CartArrayList = new ArrayList<>();
    String CTAG = "SupermercadoADAPTER";

    public CartAdapter(Context context) {

        this.context = context;
        CartArrayList = UpdateCart.get(context).getItem();//was = cart
        Log.i(CTAG, "CartAdapter: has " + Integer.toString(CartArrayList.size()) + " items" );
    }

    public void remove(int position) {
        UpdateCart.get(context).deleteEntry(CartArrayList.get(position));//remove from database
        CartArrayList.remove(position);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_card, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.name.setText(CartArrayList.get(position).costAppend());
        holder.cost.setText(CartArrayList.get(position).toPay());
        try {
            holder.image.setImageURI(CartArrayList.get(position).getImage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.options, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (CartArrayList.isEmpty()) {
            return 0;
        }
        return CartArrayList.size();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.cart_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    public ArrayList<Item> getContent() {
        if (CartArrayList != null) {
            return this.CartArrayList;
        }
        return new ArrayList<>();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int oPosition;

        public MyMenuItemClickListener(int position){ oPosition = position;}

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){
                case R.id.delete_item:
                    String name = CartArrayList.get(oPosition).getName();
                    String num = Integer.toString(CartArrayList.get(oPosition).getQuantity());
                    remove(this.oPosition);//delete
                    Toast.makeText(context, "Deleted " + num + " " + name, Toast.LENGTH_SHORT).show();//change to <item_name> Deleted
                    return true;

                 default:
            }
            return false;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        protected TextView name;
        private ImageView image;
        private TextView cost;
        private ImageView options;

        ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.id_text);
            cost = (TextView) view.findViewById(R.id.cost);
            image = (ImageView) view.findViewById(R.id.item_image);
            options = (ImageView) view.findViewById(R.id.menu);
        }
    }

    public void emptyCart(){

        int i = getItemCount();
        while (i>0){
            remove(i-1);
            i--;
        }
    }
}