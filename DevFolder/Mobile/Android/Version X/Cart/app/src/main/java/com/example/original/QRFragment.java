package com.example.original;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class QRFragment extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private QRAdapter qrAdapapter;

    private static ArrayList<QRCode> QR = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qr, container, false);
        View recyclerView = v.findViewById(R.id.recylerViewQR);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        return v;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //checkOut = UpdateCart.get(getActivity()).getItem();
        mLayoutManager = new LinearLayoutManager(getActivity());
        qrAdapapter = new QRAdapter(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(qrAdapapter);
    }

}
