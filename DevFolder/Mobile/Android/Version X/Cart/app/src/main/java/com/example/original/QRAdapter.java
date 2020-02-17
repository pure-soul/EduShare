package com.example.original;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class QRAdapter extends RecyclerView.Adapter<QRAdapter.ViewHolder> {

    private static ArrayList<QRCode> qrCodeArrayList = new ArrayList<>();

    private Context context;

    public QRAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qr_card, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(qrCodeArrayList.get(i).getDateString());
        viewHolder.qrcode.setImageBitmap(qrCodeArrayList.get(i).getBitmap());
        viewHolder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(viewHolder.options, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return qrCodeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView qrcode;
        TextView name;
        ImageView options;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.qr_name);
            qrcode = view.findViewById(R.id.qr_image);
            options = view.findViewById(R.id.menu);
        }
    }

    public static void add(QRCode code){
        qrCodeArrayList.add(code);
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.cart_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int oPosition;

        public MyMenuItemClickListener(int position){ oPosition = position;}

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){
                case R.id.delete_item:
                    remove(this.oPosition);//delete
                    return true;

                default:
            }
            return false;
        }
    }

    public void remove(int position) {
        qrCodeArrayList.remove(position);
        notifyDataSetChanged();
    }
}