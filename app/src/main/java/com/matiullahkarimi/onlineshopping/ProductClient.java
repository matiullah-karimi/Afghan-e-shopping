package com.matiullahkarimi.onlineshopping;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by Matiullah Karimi on 10/14/2016.
 */
public class ProductClient {
    private static final String API_BASE_URL = "http://172.30.10.186:8080/api";
    public static final String IMAGES_BASE_URL = "http://172.30.10.186:8080/img/";
    private AsyncHttpClient client;

    public ProductClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // register the user
    public void register(String name, String email, String password, JsonHttpResponseHandler handler) {

        HashMap<String, String> params = new HashMap<>();

        params.put("password", password);
        params.put("name", name);
        params.put("email", email);

        RequestParams requestParams = new RequestParams(params);

        String url = getApiUrl("/register");
        client.post(url, requestParams, handler);
    }

    // login the user
    public void getLogin(String username, String password, JsonHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", username.toString());
        params.put("password", password.toString());
        RequestParams param = new RequestParams(params);

        String url = getApiUrl("/login");
        client.post(url, param, handler);
    }

    // get all products
    public void getProducts(JsonHttpResponseHandler handler) {
        if (new Helper().isUserAuthenticated()) {
            String url = getApiUrl("/user/products");
            client.get(url, handler);
        } else {
            String url = getApiUrl("/products");
            client.get(url, handler);
        }
    }

    // add a product to wishlist
    public void addToWishlist(String pId, String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/addToWishlist/" + pId + "/?token=" + token);
        client.post(url, handler);
    }

    // get user wishlists
    public void wishlists(String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/wishlists?token=" + token);
        client.get(url, handler);
    }

    public void removeFromWishlist(String pId, String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/removeFromWishlist/" + pId + "?token=" + token);
        client.post(url, handler);
    }

    // add a product to cart
    public void addToCart(String pId, String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/addToCart/" + pId + "?token=" + token);
        client.post(url, handler);
    }

    // get user carts
    public void carts(String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/carts?token=" + token);
        client.get(url, handler);
    }

    // remove from carts
    public void removeFromCart(String pId, String token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/removeFromCart/" + pId + "?token=" + token);
        client.post(url, handler);
    }

    // products for men
    public void category(String cat, JsonHttpResponseHandler handler) {
        String url;
        if (cat.equals("men")) {
            url = getApiUrl("/men");
        } else if (cat.equals("women")) {
            url = getApiUrl("/women");
        } else {
            url = getApiUrl("/kid");
        }

        client.get(url, handler);
    }

    // search for product
    public void getSearchedProducts(String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/search?keyword=" + query);
        client.get(url, handler);

    }

    // make an order
    public void order(String id, String amount, String color, String size, String address, String contact, String token,
                      JsonHttpResponseHandler handler){
        String url = getApiUrl("/order/"+id+"?token="+token);

        HashMap<String, String> param = new HashMap<>();
        param.put("amount", amount);
        param.put("color", color);
        param.put("size", size);
        param.put("address", address);
        param.put("number", contact);

        RequestParams params = new RequestParams(param);

        client.post(url, params, handler);
    }

    public void myOrders(String token, JsonHttpResponseHandler handler){
        String url = getApiUrl("/orders?token="+token);
        client.get(url,handler);
    }
}