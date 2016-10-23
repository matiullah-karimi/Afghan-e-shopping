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
import android.support.v4.widget.SwipeRefreshLayout;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{

    private ProductClient client;
    private Helper helper;
    private Button btnRetry;
    private TextView txtNoInternet;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // swipe to referesh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this);


        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetchProducts();
                                    }
                                }
        );

        // initializing views
        btnRetry = (Button) findViewById(R.id.btn_retry);
        txtNoInternet = (TextView) findViewById(R.id.no_internet);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // helper class
        helper = new Helper();

        // checking the internet connection
        if (!helper.isNetworkAvailable(this)){
            helper.toast(this, "No Internet Connection");
            swipeRefreshLayout.setRefreshing(true);
            recyclerView.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
            txtNoInternet.setVisibility(View.VISIBLE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
        }
        else {
            // show progress bar
            helper.showProgressBar(this, "Loading...");
            // calling method fetchProducts()
            fetchProducts();
        }
        // starting tabs
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        LocalActivityManager mLocalActivityManager = new LocalActivityManager(MainActivity.this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState); // state will be bundle your activity state which you get in onCreate
        host.setup(mLocalActivityManager);
//Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Home");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.ic_home_black));
        host.addTab(spec);
//Tab 2
        spec = host.newTabSpec("Message");
        Intent intentMessage = new Intent(this, Message.class);
        spec.setContent(intentMessage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
    public void onRefresh() {
        fetchProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                helper.showProgressBar(MainActivity.this, "Loading...");
                searchProducts(query);

                searchView.setQuery(query, false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                // Set activity title to search query
                MainActivity.this.setTitle(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
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
            SessionManager manager = new SessionManager(MainActivity.this);
            manager.logoutUser();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchProducts() {

        swipeRefreshLayout.setRefreshing(true);
        client = new ProductClient();

        client.getProducts(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());
                swipeRefreshLayout.setRefreshing(false);

                helper.hideProgressDialog();

                try {
                    JSONArray products = response.getJSONArray("products");
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

                    final RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, names);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                    recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(MainActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {

                                    Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                                    intent.putExtra("id", names.get(position).getId());
                                    intent.putExtra("name", names.get(position).getName());
                                    intent.putExtra("price", names.get(position).getPrice());
                                    intent.putExtra("image", names.get(position).getImage());
                                    intent.putExtra("description", names.get(position).getDescription());
                                    intent.putExtra("position", position);
                                    intent.putExtra("activity", "MainActivity");
                                    ActivityTransitionLauncher.with(MainActivity.this).from(view).launch(intent);

                                }
                                @Override public void onLongItemClick(View view, int position) {
                                    Toast.makeText(MainActivity.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
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
                swipeRefreshLayout.setRefreshing(false);

                helper.hideProgressDialog();
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                txtNoInternet.setText("Unable to connect to the server");
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeRefreshLayout.setRefreshing(false);

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
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }
    private void searchProducts(String query) {

        swipeRefreshLayout.setRefreshing(true);
        client = new ProductClient();

        client.getSearchedProducts(query, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());
                swipeRefreshLayout.setRefreshing(false);

                helper.hideProgressDialog();

                try {
                    JSONArray products = response.getJSONArray("results");
                    if (products.length()==0){
                        helper.toast(MainActivity.this,"No Job Found!!!");
                    }
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

                    final RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, names);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                    recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(MainActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {

                                    Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                                    intent.putExtra("id", names.get(position).getId());
                                    intent.putExtra("name", names.get(position).getName());
                                    intent.putExtra("price", names.get(position).getPrice());
                                    intent.putExtra("image", names.get(position).getImage());
                                    intent.putExtra("description", names.get(position).getDescription());
                                    intent.putExtra("position", position);
                                    intent.putExtra("activity", "MainActivity");
                                    ActivityTransitionLauncher.with(MainActivity.this).from(view).launch(intent);

                                }
                                @Override public void onLongItemClick(View view, int position) {
                                    Toast.makeText(MainActivity.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
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
                swipeRefreshLayout.setRefreshing(false);

                helper.hideProgressDialog();
                recyclerView.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                txtNoInternet.setVisibility(View.VISIBLE);
                txtNoInternet.setText("Unable to connect to the server");
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeRefreshLayout.setRefreshing(false);

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
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }

}
