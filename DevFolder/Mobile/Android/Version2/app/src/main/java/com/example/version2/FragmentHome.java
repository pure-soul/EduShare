package com.example.version2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    Button mB1,mB2,mB3;
    SearchView mSearch;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
//    private ArrayList<list> mSearchList = new ArrayList<>();
//    private SearchAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container,false);



        return v;
    }
}
