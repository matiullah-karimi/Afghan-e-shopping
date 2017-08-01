package com.arhukh.onlineshopping;

import android.app.LocalActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ProductClient client;
    private Helper helper;
    private Button btnRetry;
    private TextView txtNoInternet;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;
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


        // initializing views
        btnRetry = (Button) findViewById(R.id.btn_retry);
        txtNoInternet = (TextView) findViewById(R.id.no_internet);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // helper class
        helper = new Helper();
        sessionManager = new SessionManager(this);

        // checking for authenticated user
        if(sessionManager.isLoggedIn()){
            HashMap<String,String> user = sessionManager.getUserDetails();
            String username = user.get(sessionManager.KEY_NAME);
            String userEmail = user.get(sessionManager.KEY_EMAIL);

            Toast.makeText(getApplicationContext(),username,Toast.LENGTH_LONG).show();
            View header = navigationView.getHeaderView(0);

            TextView authUsername = (TextView) header.findViewById(R.id.auth_user_name);
            authUsername.setText(username);

            TextView authEmail = (TextView) header.findViewById(R.id.auth_user_email);
            authEmail.setText(userEmail);
        }

        // checking the internet connection
        if (!helper.isNetworkAvailable(this)){
            helper.toast(this, "No Internet Connection");
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

        host.setOnTabChangedListener(new AnimatedTabHostListener(this, host));
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

        else if (id == R.id.nav_feedbacks) {
            Intent intent = new Intent(MainActivity.this, Feedbacks.class);
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

        helper.showProgressBar(MainActivity.this,"Loading...");
        client = new ProductClient();

        client.getProducts(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());

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

        client = new ProductClient();

        client.getSearchedProducts(query, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());
                helper.hideProgressDialog();

                try {
                    JSONArray products = response.getJSONArray("results");
                    if (products.length()==0){
                        helper.toast(MainActivity.this,"No Product Found!!!");
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
class AnimatedTabHostListener implements TabHost.OnTabChangeListener
{

    private static final int ANIMATION_TIME = 240;
    private TabHost tabHost;
    private View previousView;
    private View currentView;
    private GestureDetector gestureDetector;
    private int currentTab;

    /**
     * Constructor that takes the TabHost as a parameter and sets previousView to the currentView at instantiation
     *
     * @param context
     * @param tabHost
     */
    public AnimatedTabHostListener(Context context, TabHost tabHost)
    {
        this.tabHost = tabHost;
        this.previousView = tabHost.getCurrentView();
        gestureDetector = new GestureDetector(context, new MyGestureDetector());
        tabHost.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (gestureDetector.onTouchEvent(event))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        });
    }

    /**
     * When tabs change we fetch the current view that we are animating to and animate it and the previous view in the
     * appropriate directions.
     */
    @Override
    public void onTabChanged(String tabId)
    {

        currentView = tabHost.getCurrentView();
        if (tabHost.getCurrentTab() > currentTab)
        {
            previousView.setAnimation(outToLeftAnimation());
            currentView.setAnimation(inFromRightAnimation());
        }
        else
        {
            previousView.setAnimation(outToRightAnimation());
            currentView.setAnimation(inFromLeftAnimation());
        }
        previousView = currentView;
        currentTab = tabHost.getCurrentTab();

    }

    /**
     * Custom animation that animates in from right
     *
     * @return Animation the Animation object
     */
    private Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f);
        return setProperties(inFromRight);
    }

    /**
     * Custom animation that animates out to the right
     *
     * @return Animation the Animation object
     */
    private Animation outToRightAnimation()
    {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outToRight);
    }

    /**
     * Custom animation that animates in from left
     *
     * @return Animation the Animation object
     */
    private Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f);
        return setProperties(inFromLeft);
    }

    /**
     * Custom animation that animates out to the left
     *
     * @return Animation the Animation object
     */
    private Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outtoLeft);
    }

    /**
     * Helper method that sets some common properties
     *
     * @param animation
     *            the animation to give common properties
     * @return the animation with common properties
     */
    private Animation setProperties(Animation animation)
    {
        animation.setDuration(ANIMATION_TIME);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }

    /**
     * A gesture listener that listens for a left or right swipe and uses the swip gesture to navigate a TabHost that
     * uses an AnimatedTabHost listener.
     *
     * @author Daniel Kvist
     *
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener
    {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        private int maxTabs;

        /**
         * An empty constructor that uses the tabhosts content view to decide how many tabs there are.
         */
        public MyGestureDetector()
        {
            maxTabs = 4;
        }

        /**
         * Listens for the onFling event and performs some calculations between the touch down point and the touch up
         * point. It then uses that information to calculate if the swipe was long enough. It also uses the swiping
         * velocity to decide if it was a "true" swipe or just some random touching.
         */
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
        {
            int newTab = 0;
            if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH)
            {
                return false;
            }
            if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // Swipe right to left
                newTab = currentTab + 1;
            }
            else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // Swipe left to right
                newTab = currentTab - 1;
            }
            if (newTab < 0 || newTab > (maxTabs - 1))
            {
                return false;
            }
            tabHost.setCurrentTab(newTab);
            return super.onFling(event1, event2, velocityX, velocityY);
        }
    }
}
