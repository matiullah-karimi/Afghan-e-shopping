package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ListView listView = (ListView) findViewById(R.id.listView);
        String array [] = {"My Orders", "Shopping Address", "My Carts", "My Wishlists", "Contact Number", "Feedback"};
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
                        break;
                    case 2:
                        intent = new Intent(Account.this, Cart.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(Account.this, Wishlist.class);
                        startActivity(intent);
                        break;
                    case 4:
                        // some code for contact number
                        break;
                    case 5:
                        // some code for feedback
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
