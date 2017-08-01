package com.arhukh.onlineshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Wishlist extends AppCompatActivity {

    private ProductClient client;
    private Helper helper;
    private Button btnRetry;
    private TextView txtNoInternet;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }


        // initializing views
        btnRetry = (Button) findViewById(R.id.btn_retry);
        txtNoInternet = (TextView) findViewById(R.id.no_internet);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        // initiaizing class
        helper = new Helper();
        sessionManager = new SessionManager(this);

        // checking the internet connection
        if (!helper.isNetworkAvailable(this)) {
            helper.toast(this, "No Internet Connection");
            recyclerView.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
            txtNoInternet.setVisibility(View.VISIBLE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Wishlist.this, Wishlist.class);
                    finish();
                    startActivity(intent);
                }
            });
        } else {
            // calling method fetchProducts()
            fetchProducts();
        }
    }

    public void fetchProducts() {
        // show progress bar
        helper.showProgressBar(this, "Loading...");
        client = new ProductClient();
        client.wishlists(helper.getToken(Wishlist.this), new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());

                helper.hideProgressDialog();

                try {
                    JSONArray products = response.getJSONArray("wishlists");
                    final ArrayList<Product> names = new ArrayList<Product>();
                    for(int i=0; i<products.length(); i++){
                        JSONObject inner = products.getJSONObject(i);
                        String id = inner.getString("id");
                        String name = inner.getString("title");
                        String image = inner.getString("imagePath");
                        String price = inner.getString("price");
                        String description = inner.getString("description");
                        names.add(new Product(id, name, image, price, description));
                    }

                    final RecyclerAdapter adapter = new RecyclerAdapter(Wishlist.this, names);
                    recyclerView.setAdapter(adapter);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(Wishlist.this, 2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(Wishlist.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {

                                    Intent intent = new Intent(Wishlist.this, ProductDetail.class);
                                    intent.putExtra("id", names.get(position).getId());
                                    intent.putExtra("name", names.get(position).getName());
                                    intent.putExtra("price", names.get(position).getPrice());
                                    intent.putExtra("image", names.get(position).getImage());
                                    intent.putExtra("position", position);
                                    intent.putExtra("activity", "Wishlist");
                                    ActivityTransitionLauncher.with(Wishlist.this).from(view).launch(intent);

                                }
                                @Override public void onLongItemClick(View view, int position) {
                                    Toast.makeText(Wishlist.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                                }
                            })
                    );


                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("responsemessage",throwable.toString());

                helper.hideProgressDialog();
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                txtNoInternet.setText("Unable to connect to the server");
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Wishlist.this, Wishlist.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                helper.hideProgressDialog();
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                txtNoInternet.setText("Unable to connect to the server");
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Wishlist.this, Wishlist.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if (helper.isNetworkAvailable(Wishlist.this)) {
                fetchProducts();
                new Wishlist().fetchProducts();
            }else {
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Wishlist.this, Wishlist.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

