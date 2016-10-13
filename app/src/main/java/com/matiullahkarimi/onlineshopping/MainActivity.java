package com.matiullahkarimi.onlineshopping;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    TabHost tabHost;
    ArrayList<String> arrayList;
    ImageView pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // message arraylist
        arrayList = new ArrayList<>();
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mess = (EditText) findViewById(R.id.message);
                arrayList.add(mess.getText().toString());
                mess.setText("");
            }
        });
        ListView mlist = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter1 = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        mlist.setAdapter(adapter1);

        // starting tabs
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();
//Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Home");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.ic_home_black));
        setUpRecyclerView();
        host.addTab(spec);
//Tab 2
        spec = host.newTabSpec("Message");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.ic_message_black_24dp));
        host.addTab(spec);
//Tab 3
        spec = host.newTabSpec("Wishlist");
        spec.setContent(R.id.tab3);
        spec.setIndicator("",ContextCompat.getDrawable(this, R.drawable.heart));
        host.addTab(spec);
//Tab 4
        spec = host.newTabSpec("Cart");
        spec.setContent(R.id.tab4);
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

        } else if (id == R.id.nav_women) {

        } else if (id == R.id.nav_kid) {

        } else if (id == R.id.nav_about) {

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
        RecyclerAdapter adapter = new RecyclerAdapter(this, Product.getData());
        recyclerView.setAdapter(adapter);

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

        // listing products in cart tab
        final RecyclerView cartRecycler = (RecyclerView) findViewById(R.id.recycler_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecycler.setLayoutManager(linearLayoutManager);
        cartRecycler.setAdapter(adapter);

        cartRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, cartRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                        intent.putExtra("position", position);
                        Log.d("position", position+"");
                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(MainActivity.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );

        // listing products in wishlist tab
        final RecyclerView wishRecycler = (RecyclerView) findViewById(R.id.recycler_wish);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        wishRecycler.setLayoutManager(linearLayoutManager1);
        wishRecycler.setAdapter(adapter);

        wishRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, wishRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                        intent.putExtra("position", position);

                       startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(MainActivity.this, "Long press on image" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );

    }

}
