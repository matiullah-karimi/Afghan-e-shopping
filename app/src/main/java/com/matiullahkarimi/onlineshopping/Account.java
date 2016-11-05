package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

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
        textUsername = (TextView) findViewById(R.id.account_useremail);
        textEmail = (TextView) findViewById(R.id.account_useremail);
        textEdit = (TextView) findViewById(R.id.account_edit);
        profilePicture = (ImageView) findViewById(R.id.account_profile_pic);

        if (sessionManager.isLoggedIn()){
            HashMap<String,String> user = sessionManager.getUserDetails();
            String username = user.get(sessionManager.KEY_NAME);
            String userEmail = user.get(sessionManager.KEY_EMAIL);

            textUsername.setText(username);
            textEmail.setText(userEmail);

            textEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
                        // user feedback for application
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
