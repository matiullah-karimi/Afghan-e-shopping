package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

public class Wishlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // listing products in wishlist tab
        final RecyclerView wishRecycler = (RecyclerView) findViewById(R.id.recycler_wish);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        wishRecycler.setLayoutManager(linearLayoutManager);
      //  wishRecycler.setAdapter(adapter);

        wishRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(Wishlist.this, wishRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Wishlist.this, ProductDetail.class);
                        intent.putExtra("position", position);

                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(Wishlist.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }
}
