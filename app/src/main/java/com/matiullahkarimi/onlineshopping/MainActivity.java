package com.matiullahkarimi.onlineshopping;

import android.app.ActivityOptions;
import android.app.LocalActivityManager;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    TabHost tabHost;
    ImageView pImage;
    ProductClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // callign method fetchProducts()
        fetchProducts();
        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // starting tabs
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        LocalActivityManager mLocalActivityManager = new LocalActivityManager(MainActivity.this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState); // state will be bundle your activity state which you get in onCreate
        host.setup(mLocalActivityManager);
//Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Home");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.ic_home_black));
        setUpRecyclerView();
        host.addTab(spec);
//Tab 2
        spec = host.newTabSpec("Message");
        Intent intentMessage = new Intent(this, Message.class);
        spec.setContent(intentMessage);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.ic_message_black_24dp));
        host.addTab(spec);
//Tab 3
        spec = host.newTabSpec("Wishlist");
        Intent wishlistIntent = new Intent(this, Wishlist.class);
        spec.setContent(wishlistIntent);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.heart));
        host.addTab(spec);
//Tab 4
        spec = host.newTabSpec("Cart");
        Intent cartIntent = new Intent(this, Cart.class);
        spec.setContent(cartIntent);
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_shopping_cart_black_24dp));
        host.addTab(spec);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent = new Intent(MainActivity.this, Account.class);
            startActivity(intent);
        } else if (id == R.id.nav_men) {
            Intent intent = new Intent(MainActivity.this, ProductCategories.class);
            intent.putExtra("Category", "men");
            startActivity(intent);
        } else if (id == R.id.nav_women) {
            Intent intent = new Intent(MainActivity.this, ProductCategories.class);
            intent.putExtra("Category", "women");
            startActivity(intent);
        } else if (id == R.id.nav_kid) {
            Intent intent = new Intent(MainActivity.this, ProductCategories.class);
            intent.putExtra("Category", "kid");
            startActivity(intent);
        }else if (id == R.id.nav_login){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Defining Recycler lists
    private void setUpRecyclerView(){

        // listing products in home tab
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        RecyclerAdapter adapter = new RecyclerAdapter(this, new ArrayList<Product>());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                        intent.putExtra("position", position);
                        Log.d("position", position+"");
                        ActivityTransitionLauncher.with(MainActivity.this).from(view).launch(intent);

                    }
                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(MainActivity.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void fetchProducts() {

        client = new ProductClient();
        client.getProducts(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());
                try {
                    JSONArray products = response.getJSONArray("teachers");
                    ArrayList<Product> names = new ArrayList<Product>();
                    for(int i=0; i<products.length(); i++){
                        JSONObject inner = products.getJSONObject(i);
                        String name = inner.getString("name");
                        String image = inner.getString("image");
                        String price = inner.getString("id");
                        names.add(new Product(name, image, price));
                    }
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
                    RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, names);
                    recyclerView.setAdapter(adapter);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());


                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("responsemessage",throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

}
