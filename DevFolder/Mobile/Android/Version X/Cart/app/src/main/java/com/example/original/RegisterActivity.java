package com.example.original;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.original.Volley.Config_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = RegisterActivity.class.getSimpleName();
    //SQLiteHandler db;
    EditText mName;
    EditText mEmail;
    EditText mPassword;
    ProgressBar mLoading;
    EditText mConfirmPassword;
    Button mRegister;
    TextView mLogin;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mLoading = (ProgressBar) findViewById(R.id.loading);
        mName = (EditText) findViewById(R.id.user_name);
        mEmail = (EditText) findViewById(R.id.user_email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.password_confirm);
        mRegister = (Button) findViewById(R.id.submit_register);
        mLogin = (TextView) findViewById(R.id.login);

        session = new SessionManager(getApplicationContext());

        //db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String conPassword = mConfirmPassword.getText().toString();



                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && password.equals(conPassword)) {
                    registerUser(name, email, password);
                } else if(!password.equals(conPassword)){
                    Toast.makeText(getApplicationContext(),
                            "Passwrords do not match!", Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }

    private void registerUser(final String name, final String email,
                              final String password) {

        String regist =  Config_URL.URL_REGISTER + name + "/" + password +"/"+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                regist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.optBoolean("error");
                    if (!error) {

                        Profile.setName(name);
                        Profile.setEmail((String) jObj.get("email"));
                        session.setLogin(true);
                        // Launch main activity
                        Intent intent = new Intent(RegisterActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: Invalid Credentials Try Again");
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

