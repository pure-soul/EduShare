//package com.example.original;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.example.original.Server.Post;
//import com.google.zxing.WriterException;
//
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//
//import androidmads.library.qrgenearator.QRGContents;
//import androidmads.library.qrgenearator.QRGEncoder;
//
//
//public class CartFragment extends Fragment {
//
//    private CartAdapter cartAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//    private static ArrayList<Item> checkOut = new ArrayList<>();
//    private static String CODEKEY = Profile.getName();
//
//    public static String QTAG = "QRSchema Generater";
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_cart, container, false);
//
//
//        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (cartAdapter.getContent().isEmpty() != true) {
//                    checkOut();
//                    Snackbar.make(view, "Collect Items At Supermarket with above QRSchema Code ", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                } else {
//                    Snackbar.make(view, "FIRST ADD ITEMS TO CART", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            }
//        });
//
//        View recyclerView = v.findViewById(R.id.recylerViewCart); //make this to  RECYCLER VIEW
//        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);
//        return v;
//    }
//
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        //checkOut = UpdateCart.get(getActivity()).getItem();
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        cartAdapter = new CartAdapter(getActivity());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(cartAdapter);
//    }
//
//    public void checkOut() {
//        checkOut = cartAdapter.getContent();
//        cartAdapter.emptyCart();
//        generateQR();
//    }
//
//    public void generateQR() {
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        Fragment prev = getFragmentManager().findFragmentByTag(" dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        DialogFragment newFragment = QRDialog.newInstance(3, getActivity());
//        newFragment.show(ft, "dialog");
//    }
//
//    public static class QRDialog extends DialogFragment {
//
//
//        private Bitmap bitmap;
//        private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
//        private static QRCode qrcode;
//        private static Context mContext;
//        private Post post;
//
//        static QRDialog newInstance(int num, Context context) {
//            QRDialog f = new QRDialog();
//            // Supply num input as an argument.
//            Bundle args = new Bundle();
//            mContext = context;
//            qrcode = new QRCode(mContext);
//            String c = qrcode.getUUID() + CODEKEY;
//            args.putString("code", c);
//            args.putString("name", qrcode.getDateString());
//            args.putInt("num", num);
//            f.setArguments(args);
//
//            return f;
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            int mNum = getArguments().getInt("num");
//
//            //Pick a style based on the num.
//            int style = DialogFragment.STYLE_NORMAL, theme = 0;
//            switch ((mNum-1)%6) {
//                case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//                case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//                case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//                case 4: style = DialogFragment.STYLE_NORMAL; break;
//                case 5: style = DialogFragment.STYLE_NORMAL; break;
//                case 6: style = DialogFragment.STYLE_NO_TITLE; break;
//                case 7: style = DialogFragment.STYLE_NO_FRAME; break;
//                case 8: style = DialogFragment.STYLE_NORMAL; break;
//            }
//            switch ((mNum-1)%6) {
//                case 4: theme = android.R.style.Theme_Holo; break;
//                case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//                case 6: theme = android.R.style.Theme_Holo_Light; break;
//                case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//                case 8: theme = android.R.style.Theme_Holo_Light; break;
//            }
//            setStyle(style, theme);
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.qrdisplay, container, false);
//            ImageView qrImage = v.findViewById(R.id.qrcode);
//
//            int smallerDimension = 1000;
//            QRGEncoder qrgEncoder = new QRGEncoder(getArguments().getString("code"), null, QRGContents.Type.TEXT, smallerDimension);
//            try {
//                // Getting QRSchema-Code as Bitmap
//                bitmap = qrgEncoder.encodeAsBitmap();
//                qrcode.setBitmap(bitmap);
//                DateFormat qformat = qrcode.getFormat();
//                Calendar cal = Calendar.getInstance();
//                Date date = cal.getTime();
//                String dateString = qformat.format(date);
//                qrcode.setDate(dateString);
//                // Setting Bitmap to ImageView
//                qrImage.setImageBitmap(bitmap);
//            } catch (WriterException e) {
//                Log.v(QTAG, e.toString());
//            }
//
//            post = (Post) new Post(getArguments().getString("code")).execute(checkOut);//make a post to server of reciept and code
//
//            // Watch for button clicks to save QRSchema Code
//            Button button = (Button)v.findViewById(R.id.qrdownload);
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    getDialog().dismiss();
//                    QRAdapter.add(qrcode);
//                }
//            });
//
//            return v;
//        }
//
//    }
//
//}