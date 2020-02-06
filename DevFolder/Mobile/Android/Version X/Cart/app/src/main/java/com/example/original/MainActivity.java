package com.example.original;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private TextView mName, mEmail;
    private DrawerLayout drawer;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        View nav_head = navView.inflateHeaderView(R.layout.nav_header);
        mName = nav_head.findViewById(R.id.name1);
        mEmail = nav_head.findViewById(R.id.email1);

        mName.setText(Profile.getName());
        mEmail.setText(Profile.getEmail());



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SearchFragment()).commit();
            navView.setCheckedItem(R.id.nav_search);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else if (getFragmentManager().getBackStackEntryCount() > 0) { //TODO: TEST
            getFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_search:
                setTitle(R.string.search);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchFragment()).commit();
                break;

//            case R.id.nav_locate:
//                setTitle(R.string.locate);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new LocateFragment()).commit();
//                break;
            case R.id.nav_profile:
                setTitle(Profile.getName());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
//            case R.id.nav_cart:
//                setTitle(R.string.cart);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new CartFragment()).commit();
//                break;
            case R.id.nav_share:
                setTitle(R.string.share);
                sharePage();
                break;
            case R.id.nav_lock:
                logoutUser();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sharePage(){
        // WebView webView = new WebView(this);
        // webView.loadUrl("192.168.43.105:5000");
        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
        shareIntent.setData(Uri.parse("http://192.168.43.105:5000"));//edit url
        startActivity(shareIntent);
    }

    private void logoutUser() {
        sessionManager.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
