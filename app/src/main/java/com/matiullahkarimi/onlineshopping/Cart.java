package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Cart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // listing products in cart tab
        final RecyclerView cartRecycler = (RecyclerView) findViewById(R.id.recycler_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecycler.setLayoutManager(linearLayoutManager);
       // cartRecycler.setAdapter(adapter);

        cartRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(Cart.this, cartRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Cart.this, ProductDetail.class);
                        intent.putExtra("position", position);
                        Log.d("position", position+"");
                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(Cart.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }
}
