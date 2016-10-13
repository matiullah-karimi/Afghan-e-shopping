package com.matiullahkarimi.onlineshopping;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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

import java.util.ArrayList;

public class ProductDetail extends AppCompatActivity {
    ImageView pImage;
    TextView pName;
    TextView pPrice;
    ValueAnimator animator;
    Boolean wish;

    ArrayList<Product> dataList = new ArrayList<Product>(Product.getData());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pImage = (ImageView) findViewById(R.id.dImage);
        pName = (TextView) findViewById(R.id.dName);
        pPrice = (TextView) findViewById(R.id.dPrice);

        int position = getIntent().getIntExtra("position", 0);
        Log.d("poston", position+"");

        pImage.setImageResource(dataList.get(position).getImage());
        pName.setText(dataList.get(position).getName());
        pPrice.setText(dataList.get(position).getPrice());
        wish = false;
        final Button wishButton = (Button) findViewById(R.id.btn_wish);
        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wish){
                    wish = true;
                    wishButton.setBackgroundColor(Color.RED);
                }else {
                    wishButton.setBackgroundColor(Color.LTGRAY);
                    wish = false;
                }

            }
        });
//        wishButton.setBackgroundColor(Color.RED);
//        animator = null;
//        wishButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//             if (animator != null){
//                 animator.reverse();
//                 animator = null;
//             }else {
//                 final Button button = (Button) view;
//                 animator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.RED, Color.BLUE );
//
//                 animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
//
//                     @Override
//                     public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                         button.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
//                     }
//                 });
//                 animator.start();
//             }
//            }
//        });
    }

}
