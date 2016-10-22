package com.matiullahkarimi.onlineshopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Karimi on 10/15/2016.
 */
public class Helper {
    private ProgressDialog progressBar;
    private SessionManager sessionManager;

    // method for showing progress dialog
    public void showProgressBar(Context context, String message){
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(message);
        progressBar.show();
    }

    // method for hiding progress dialog
    public void hideProgressDialog(){
        progressBar.hide();
    }

    // method for checking internet connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;

        }
        return isAvailable;
    }

    // show toast message method
    public void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // checking if the user is authenticated or not
    public boolean isUserAuthenticated(){
       boolean isUserAuthenticated = false;

        try {
            HashMap<String,String> user = sessionManager.getUserDetails();
            String token = user.get(sessionManager.KEY_TOKEN);
            if (token.length()>0){

                isUserAuthenticated = true;
            }

        } catch (NullPointerException ex) {
            isUserAuthenticated = false;
        }

        return  isUserAuthenticated;
    }

    public String getToken(Context context){
        try{
             sessionManager = new SessionManager(context);
             HashMap<String, String> user_token = sessionManager.getUserDetails();
             String token = user_token.get(sessionManager.KEY_TOKEN);

        return token;
    }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
