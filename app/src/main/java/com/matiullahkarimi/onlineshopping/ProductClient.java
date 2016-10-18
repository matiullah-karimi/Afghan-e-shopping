package com.matiullahkarimi.onlineshopping;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;

/**
 * Created by Matiullah Karimi on 10/14/2016.
 */
public class ProductClient {
    private static final String API_BASE_URL = "http://10.19.1.61:8080/api";
    public static final String IMAGES_BASE_URL = "http://10.19.1.61:8080/img/";
    private AsyncHttpClient client;

    public ProductClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // login the user
    public void getLogin(String username,String password,JsonHttpResponseHandler handler){
            HashMap<String,String> params= new HashMap<String, String>();
            params.put("email",username.toString());
            params.put("password",password.toString());
            RequestParams param = new RequestParams(params);

            String url = getApiUrl("/login");
            client.post(url,param,handler);
    }
    // get all products
    public void getProducts( JsonHttpResponseHandler handler){
        String url = getApiUrl("/products");
        client.get(url,handler);
    }

    // add a product to wishlist
    public void addToWishlist(String pId, String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/addToWishlist/"+pId+"/?token="+token);
        client.post(url, handler);
    }

    // get user wishlists
    public void wishlists(String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/wishlists?token="+token);
        client.get(url, handler);
    }

    public void removeFromWishlist(String pId, String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/removeFromWishlist/"+pId+"?token="+token);
        client.post(url, handler);
    }

    // add a product to cart
    public void addToCart(String pId, String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/addToCart/" + pId+ "?token="+token);
        client.post(url, handler);
    }

    // get user carts
    public void carts(String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/carts?token="+token);
        client.get(url, handler);
    }

    public void removeFromCart(String pId, String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/removeFromCart/"+pId+"?token="+token);
        client.post(url, handler);
    }

}
