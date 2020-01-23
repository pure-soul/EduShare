package com.example.version2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.version2.Volley.Config_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText mFName;
    EditText mLName;
    EditText mEmail;
    EditText mPassword;
    EditText mConfirmPassword;
    Button mRegister;
    TextView mLogin;
    Button mPeer;
    RadioGroup radioGroup;
    RadioButton radioButton, radioTutor, radioStudent;
    Spinner mCategory;
    Spinner mPreference;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFName = (EditText) findViewById(R.id.first_name);
        mLName = (EditText) findViewById(R.id.last_name);
        mCategory = (Spinner) findViewById(R.id.category);
        mPreference = (Spinner) findViewById(R.id.preference);
        mEmail = (EditText) findViewById(R.id.email_address);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mConfirmPassword = (EditText) findViewById(R.id.reg_confirm_password);
        mRegister = (Button) findViewById(R.id.submit_register);
        mLogin = (TextView) findViewById(R.id.login);
        mPeer = (Button) findViewById(R.id.peer);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        mPeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //code to another page to fill out some forms if the user want to be a peer reviewer
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = mFName.getText().toString();
                String lname = mLName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String conPassword = mConfirmPassword.getText().toString();



                if (!fname.isEmpty() && !lname.isEmpty() && !email.isEmpty() && !password.isEmpty() && password.equals(conPassword)) {
                    registerUser(fname, email, password);
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategory.setAdapter(adapter);
        mCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.preferences, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mPreference.setAdapter(adapter1);
        mPreference.setOnItemSelectedListener(this);

    }

    private void registerUser(final String fname, final String email,
                              final String password) {

        String regist =  Config_URL.URL_REGISTER + fname + "/" + password +"/"+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                regist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.optBoolean("error");
                    if (!error) {

                        Profile.setName(fname);
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
                params.put("name", fname);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        //to get the text from the radio button (for future use)
        //radioButton.getText();
        Profile.setType((String) radioButton.getText());

        String status = (String) radioButton.getText();

        if (status == "tutor"){
            mCategory.setVisibility(View.VISIBLE);
            mPreference.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Profile.setCategory(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
