package com.matiullahkarimi.onlineshopping;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener{
    private ImageView pImage;
    private TextView pName;
    private TextView pPrice, pDesc;
    private ValueAnimator animator;
    private Boolean wish, addToCart;
    private ExitActivityTransition exitTransition;
    private ArrayList<Product> dataList = new ArrayList<Product>(new ArrayList<Product>());
    private Button btnBuy, btnAdd2Cart;
    private ImageButton btnAdd2Wishlist;
    private ProductClient client;
    private String pId;
    private String activity;
    private SessionManager sessionManager;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        exitTransition = ActivityTransition.with(getIntent()).to(findViewById(R.id.dParentLayout)).start(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // initializing views
        pImage = (ImageView) findViewById(R.id.dImage);
        pName = (TextView) findViewById(R.id.dName);
        pPrice = (TextView) findViewById(R.id.dPrice);
        pDesc = (TextView) findViewById(R.id.dDescription);
        btnAdd2Cart = (Button) findViewById(R.id.btn_cart);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        btnAdd2Wishlist = (ImageButton) findViewById(R.id.btn_wish);

        // getting the select view position
        pId = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String image = getIntent().getStringExtra("image");
        String description = getIntent().getStringExtra("description");
        activity = getIntent().getStringExtra("activity");
        // setting values to views
        pName.setText(name);
        pPrice.setText(price);
        pDesc.setText(description);
        Picasso.with(ProductDetail.this).load(Uri.parse(ProductClient.IMAGES_BASE_URL+image)).error(R.drawable.avatar).into(pImage);


        sessionManager = new SessionManager(ProductDetail.this);
        if (sessionManager.isLoggedIn()){
            btnAdd2Wishlist.setVisibility(View.VISIBLE);
            btnBuy.setVisibility(View.VISIBLE);
            btnAdd2Cart.setVisibility(View.VISIBLE);
        }else {
            btnAdd2Wishlist.setVisibility(View.GONE);
            btnBuy.setVisibility(View.GONE);
            btnAdd2Cart.setVisibility(View.GONE);
        }

        // initial value of wish boolean
        wish = false;
        addToCart = false;

        // registering views to listeners
        btnBuy.setOnClickListener(this);
        btnAdd2Cart.setOnClickListener(this);
        btnAdd2Wishlist.setOnClickListener(this);

        if (activity.equals("Cart")){
            btnAdd2Cart.setText("Remove from Cart");
        }
    }
    @Override
    public void onBackPressed() {
        exitTransition.exit(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_buy:
                new BottomSheet.Builder(ProductDetail.this).title("Choose Paying Type").sheet(R.menu.menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.mPaisa:
                                OrderDialog orderDialog = new OrderDialog();
                                orderDialog.show(getFragmentManager(),"Fragment");
                                break;
                        }
                    }
                }).show();
                break;

            case R.id.btn_cart:
                buttonCart();

                break;

            case R.id.btn_wish:

                if (!wish){

                    wish = true;
                    btnAdd2Wishlist.setBackgroundResource(android.R.drawable.star_big_on);
                    addToWishlist(pId);

                }else {

                    btnAdd2Wishlist.setBackgroundResource(android.R.drawable.star_big_off);
                    wish = false;
                    removeFromWishlist(pId);
                }
                break;
        }

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

    public void addToWishlist(String pId){

        client = new ProductClient();
        client.addToWishlist(pId, new Helper().getToken(ProductDetail.this), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("add_to wishlist", response.toString());
                Toast.makeText(ProductDetail.this, "Succesfully added to your wishlist", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void removeFromWishlist(String pId){
        client = new ProductClient();

        client.removeFromWishlist(pId, new Helper().getToken(ProductDetail.this), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Toast.makeText(ProductDetail.this, "Succesfully removed from your wishlist", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void addToCart(String pId){

        client = new ProductClient();
        client.addToCart(pId, new Helper().getToken(ProductDetail.this), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("add_to wishlist", response.toString());
                Toast.makeText(ProductDetail.this, "Successfully added to your carts", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void removeFromCart(String pId){
        client = new ProductClient();

        client.removeFromCart(pId, new Helper().getToken(ProductDetail.this), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Toast.makeText(ProductDetail.this, "Successfully removed from your carts", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void buttonCart(){
        if (activity.equals("Cart")){
            btnAdd2Cart.setText("Remove from Cart");
            removeFromCart(pId);
        }
        else if(activity.equals("Wishlist")){
            addToCart(pId);
        }
        else {
            addToCart(pId);
        }
    }
}
