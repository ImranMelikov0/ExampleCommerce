package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imranmelikov.examplecommerce.Adapters.ProductAdapter;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.Utils.Constants;
import com.imranmelikov.examplecommerce.databinding.ActivitySearchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public ActivitySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        products=new ArrayList<>();

        String query=getIntent().getStringExtra("query");
        getProduct(query);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(query);

//        products.add(new Product("name","","some",12,12,1,1));
//        products.add(new Product("name","","some",12,12,1,1));
//        products.add(new Product("name","","some",12,12,1,1));
//        products.add(new Product("name","","some",12,12,1,1));
//        products.add(new Product("name","","some",12,12,1,1));
//        products.add(new Product("name","","some",12,12,1,1));
        productAdapter=new ProductAdapter(this,products);


        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);

        binding.productlist.setAdapter(productAdapter);
        binding.productlist.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(String query){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url= Constants.GET_PRODUCTS_URL + "?q=" + query;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err",response);
                    JSONObject object=new JSONObject(response);
                    if(object.getString("status").equals("success")){
                        JSONArray productarray=object.getJSONArray("products");
                        for(int i=0;i<productarray.length();i++){
                            JSONObject jsonObject=productarray.getJSONObject(i);
                            Product product=new Product(
                                    jsonObject.getString("name"),
                                    Constants.PRODUCTS_IMAGE_URL + jsonObject.getString("image"),
                                    jsonObject.getString("status"),
                                    jsonObject.getDouble("price"),
                                    jsonObject.getDouble("price_discount"),
                                    jsonObject.getInt("stock"),
                                    jsonObject.getInt("id")
                            );
                            products.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }else{
                        //Do nothing
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}