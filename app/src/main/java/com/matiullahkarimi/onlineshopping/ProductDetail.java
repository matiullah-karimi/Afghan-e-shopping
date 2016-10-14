package com.matiullahkarimi.onlineshopping;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;

import java.util.ArrayList;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener{
    ImageView pImage;
    TextView pName;
    TextView pPrice;
    ValueAnimator animator;
    Boolean wish, addToCart;
    private ExitActivityTransition exitTransition;
    ArrayList<Product> dataList = new ArrayList<Product>(new ArrayList<Product>());
    private Button btnBuy, btnAdd2Cart, btnAdd2Wishlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        exitTransition = ActivityTransition.with(getIntent()).to(findViewById(R.id.dImage)).start(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initializing views
        pImage = (ImageView) findViewById(R.id.dImage);
        pName = (TextView) findViewById(R.id.dName);
        pPrice = (TextView) findViewById(R.id.dPrice);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        btnAdd2Cart = (Button) findViewById(R.id.btn_cart);
        btnAdd2Wishlist = (Button) findViewById(R.id.btn_wish);

        // getting the select view position
        int position = getIntent().getIntExtra("position", 0);
        Log.d("poston", position+"");

        // setting values to views
       // pImage.setImageResource(dataList.get(position).getImage());
        pName.setText(dataList.get(position).getName());
        pPrice.setText(dataList.get(position).getPrice());

        // initial value of wish boolean
        wish = false;
        addToCart = false;

        // registering views to listeners
        btnBuy.setOnClickListener(this);
        btnAdd2Cart.setOnClickListener(this);
        btnAdd2Wishlist.setOnClickListener(this);
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
                new BottomSheet.Builder(ProductDetail.this).title("title").sheet(R.menu.menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.help:
                                Toast.makeText(ProductDetail.this, "help me", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.btn_cart:
                if (!addToCart){
                    addToCart = true;
                    btnAdd2Cart.setBackgroundColor(Color.RED);
                }else {
                    btnAdd2Cart.setBackgroundColor(Color.LTGRAY);
                    addToCart = false;
                }
                break;

            case R.id.btn_wish:
                if (!wish){
                    wish = true;
                    btnAdd2Wishlist.setBackgroundColor(Color.RED);
                }else {
                    btnAdd2Wishlist.setBackgroundColor(Color.LTGRAY);
                    wish = false;
                }
                break;
        }

    }
}
