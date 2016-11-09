package com.matiullahkarimi.onlineshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class Feedbacks extends AppCompatActivity {

    private ProductClient client;
    private Helper helper;
    private Button btnRetry;
    private TextView txtNoInternet;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initializing views
        btnRetry = (Button) findViewById(R.id.btn_retry);
        txtNoInternet = (TextView) findViewById(R.id.no_internet);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        // initiaizing class
        helper = new Helper();
        sessionManager = new SessionManager(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Feedbacks.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        fetchProducts();
        
    }

    private void fetchProducts() {

        helper.showProgressBar(Feedbacks.this,"Loading...");
        client = new ProductClient();

        client.feedbacks(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());

                helper.hideProgressDialog();

                try {
                    JSONArray products = response.getJSONArray("feedbacks");
                    final ArrayList<Product> names = new ArrayList<Product>();
                    for(int i=0; i<products.length(); i++){
                        JSONObject inner = products.getJSONObject(i);
                        String id = inner.getString("created_at");
                        String title = inner.getString("title");
                        String username = inner.getString("username");
                        String rate = inner.getString("rate");
                        String description = inner.getString("description");
                        names.add(new Product(id, title, username, rate, description));
                    }

                    final RecyclerAdapter adapter = new RecyclerAdapter(Feedbacks.this, names);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                    recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(Feedbacks.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {

                                    Intent intent = new Intent(Feedbacks.this, ProductDetail.class);
                                    intent.putExtra("id", names.get(position).getId());
                                    intent.putExtra("name", names.get(position).getName());
                                    intent.putExtra("price", names.get(position).getPrice());
                                    intent.putExtra("image", names.get(position).getImage());
                                    intent.putExtra("description", names.get(position).getDescription());
                                    intent.putExtra("position", position);
                                    intent.putExtra("activity", "Feedbacks");
                                    ActivityTransitionLauncher.with(Feedbacks.this).from(view).launch(intent);

                                }
                                @Override public void onLongItemClick(View view, int position) {
                                    Toast.makeText(Feedbacks.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(Feedbacks.this, Feedbacks.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                try {
                    helper.hideProgressDialog();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                txtNoInternet.setText("Unable to connect to the server");
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Feedbacks.this, Feedbacks.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }

}
