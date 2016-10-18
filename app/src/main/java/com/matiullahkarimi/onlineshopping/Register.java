package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Register extends AppCompatActivity {

    private Button btnRegister;
    private EditText editName, editEmail, editPassword;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new Helper();

        btnRegister = (Button) findViewById(R.id.register_btn);
        editName = (EditText) findViewById(R.id.name_edit);
        editEmail = (EditText) findViewById(R.id.email_edit);
        editPassword = (EditText) findViewById(R.id.password_edit);
        
        final String name = editName.getText().toString();
        final String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();
        

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helper.isNetworkAvailable(Register.this)){
                    register(name, email, password);
                }
                else {
                    helper.toast(Register.this,"No internet connection");
                }
            }
        });
    }

    private void register(String name, String email, String password) {
        ProductClient clien = new ProductClient();
        clien.register(name, email, password, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                helper.toast(Register.this, "You have Registered Successfully");
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                helper.toast(Register.this, "Something went wrong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                helper.toast(Register.this, "Something went wrong");
            }
        });
    }

}
