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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText userName;
    EditText mPassword;
    Button mSubmitLogin;
    TextView mRegister;
    ProgressBar loading;
    //private static String URL_LOGIN = "http://10.3.0.54:5000/supermercado/api/v1.0/";
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //db = new DatabaseHelper(this);
        userName = (EditText) findViewById(R.id.tvuser_name);
        mPassword = (EditText) findViewById(R.id.pass_word);
        mSubmitLogin = (Button) findViewById(R.id.submit_login);
        mRegister = (TextView) findViewById(R.id.register);
        loading = findViewById(R.id.loading);

        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mSubmitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String password = mPassword.getText().toString();

                // Check for empty data in the form
                if (name.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(name, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }


    private void checkLogin(final String name, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //to check
        String url = Config_URL.URL_LOGIN + name +"/"+password;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.optBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session

                        //change to email
                        Profile.setName(name);
                        Profile.setEmail((String) jObj.get("login_email"));
                        session.setLogin(true);
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error");
                        Toast.makeText(getApplicationContext(),
                                "Error: " +errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                    mSubmitLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,"Error: Invalid Credentials Try Again ", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
