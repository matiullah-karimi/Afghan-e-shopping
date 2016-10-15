package com.matiullahkarimi.onlineshopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Karimi on 10/15/2016.
 */
public class Helper {
    private ProgressDialog progressBar;

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

    public void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
