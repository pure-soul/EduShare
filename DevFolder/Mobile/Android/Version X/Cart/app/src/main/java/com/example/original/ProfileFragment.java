package com.example.original;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    Button mlogOut;
    TextView mName, mBlog, mEmail, mTreansaction;
    SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        mName = v.findViewById(R.id.name);
        mEmail = v.findViewById(R.id.email);
        mTreansaction = v.findViewById(R.id.transaction);
        mTreansaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new QRFragment(), "PROFILE")
                        .addToBackStack(null)
                        .commit();
            }
        });


        sessionManager = new SessionManager(getContext());

        mlogOut = v.findViewById(R.id.log_out);
        mlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutUser();
            }
        });

        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        }

        // Displaying the user details on the screen
        mName.setText(Profile.getName());
        mEmail.setText(Profile.getEmail());

        mBlog = v.findViewById(R.id.blog);
        mBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePage();
            }
        });


        return v;
    }

    private void logoutUser() {
        sessionManager.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public  void sharePage(){
        // WebView webView = new WebView(this);
        // webView.loadUrl("192.168.43.105:5000");
        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
        shareIntent.setData(Uri.parse("http://192.168.43.105:5000"));//edit url
        startActivity(shareIntent);
    }
}
