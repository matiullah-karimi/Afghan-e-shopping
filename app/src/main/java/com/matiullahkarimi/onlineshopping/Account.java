package com.matiullahkarimi.onlineshopping;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Account extends AppCompatActivity {

    private TextView textUsername, textEmail, textEdit;
    private ImageView profilePicture;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(this);

        //initializing views
        textUsername = (TextView) findViewById(R.id.account_username);
        textEmail = (TextView) findViewById(R.id.account_useremail);
        textEdit = (TextView) findViewById(R.id.account_edit);
        profilePicture = (ImageView) findViewById(R.id.account_profile_pic);

        if (sessionManager.isLoggedIn()){
            final HashMap<String,String> user = sessionManager.getUserDetails();
            final String username = user.get(sessionManager.KEY_NAME);
            final String userEmail = user.get(sessionManager.KEY_EMAIL);

            textUsername.setText(username);
            textEmail.setText(userEmail);

            textEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();

                    View modifyView = inflater.inflate(R.layout.edit_account, null);

                    builder.setTitle("Edit Account");


                    final EditText editUsername = (EditText) modifyView.findViewById(R.id.nameEditText);
                    editUsername.setText(username);
                    final EditText editEmail = (EditText) modifyView.findViewById(R.id.emailEditText);
                    editEmail.setText(userEmail);
                    final EditText editPassword = (EditText) modifyView.findViewById(R.id.passwordEditText);

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(modifyView)
                            // Add action buttons
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int id) {
                                    String name = editUsername.getText().toString();
                                    String email = editEmail.getText().toString();
                                    String password = editPassword.getText().toString();

                                    ProductClient client = new ProductClient();
                                    client.editAccount(new Helper().getToken(Account.this), name, email, password, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            super.onSuccess(statusCode, headers, response);
                                            try {
                                                String message = response.getString("message");
                                                Toast.makeText(Account.this, message, Toast.LENGTH_LONG).show();
                                                sessionManager.logoutUser();
                                                Intent intent = new Intent(Account.this, Login.class);
                                                startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            super.onFailure(statusCode, headers, responseString, throwable);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                     builder.create().show();
                }
            });

            profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(Account.this, view);
                    final int REQUEST_IMAGE_CAPTURE = 1;
                    final int SELECT_PICTURE = 1;

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.item_camera:
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
                                    break;
                                case R.id.item_gallery:
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent,
                                            "Select Picture"), SELECT_PICTURE);
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.show();
                }
            });
        }

        final ListView listView = (ListView) findViewById(R.id.listView);
        String array [] = {"My Orders", "My Carts", "My Wishlists", "Feedback"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, R.id.list_text, array);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch ((int)l){
                    case 0:
                        Intent intent = new Intent(Account.this, MyOrders.class);
                        startActivity(intent);
                        break;
                    case 1:
                        //calling an intent
                        intent = new Intent(Account.this, Cart.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(Account.this, Wishlist.class);
                        startActivity(intent);
                        break;
                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
                        // Get the layout inflater
                        LayoutInflater inflater = getLayoutInflater();
                        View modifyView = inflater.inflate(R.layout.item_feedback, null);

                        builder.setTitle("Feedback");


                        final EditText editTitle = (EditText) modifyView.findViewById(R.id.title_feedback);
                        final EditText editDesc = (EditText) modifyView.findViewById(R.id.description_feedback);
                        final RatingBar ratingBar = (RatingBar) modifyView.findViewById(R.id.ratingBar);

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(modifyView)
                                // Add action buttons
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int id) {
                                        String title = editTitle.getText().toString();
                                        String description = editDesc.getText().toString();
                                        String rating = Float.toString(ratingBar.getRating());
                                        new ProductClient().feedback(new Helper().getToken(Account.this), title, description, rating,
                                                new JsonHttpResponseHandler(){
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        super.onSuccess(statusCode, headers, response);

                                                        try {
                                                            Toast.makeText(Account.this, response.getString("message"), Toast.LENGTH_LONG).show();
                                                        }catch (Exception ex){
                                                            ex.printStackTrace();
                                                        }

                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                        break;

            }
            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
