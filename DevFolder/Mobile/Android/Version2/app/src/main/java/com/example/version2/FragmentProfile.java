package com.example.version2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentProfile extends Fragment {

    TextView mName, mEmail, mlogOut, mType;
    Layout mView_chat, mView_favorites, mView_download, mView_bookmark;
    SessionManager sessionManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_profile, container,false);

        mName = v.findViewById(R.id.name);
        mEmail = v.findViewById(R.id.email);
        mType = v.findViewById(R.id.type);

/*        mView_chat = (Layout) v.findViewById(R.id.view_chat);
        ((View) mView_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

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
        mType.setText(Profile.getType());

        return v ;
    }

    private void logoutUser() {
        sessionManager.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
