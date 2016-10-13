package com.matiullahkarimi.onlineshopping;

import java.security.Policy;
import java.util.ArrayList;

/**
 * Created by Matiullah Karimi on 10/11/2016.
 */
public class Product {
    private String name;
    private int image;
    private String price;

    public Product(){}

    public Product(String name, int image, String price) {
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static ArrayList<Product> getData(){
        ArrayList<Product> dataList = new ArrayList<>();

        int[] images = getImages();

        for (int i=0; i< images.length; i++){
            Product product = new Product();
            product.setImage(images[i]);
            product.setName("Image " + i);
            product.setPrice( i + "000 AF");
            dataList.add(product);
        }

        return dataList;
    }

    public static int[] getImages(){
        int[] images = {R.drawable.girl3, R.drawable.avatar, R.drawable.doctor, R.drawable.captain, R.drawable.judge,
        R.drawable.monk, R.drawable.thief, R.drawable.woman};

        return images;
    }
}
