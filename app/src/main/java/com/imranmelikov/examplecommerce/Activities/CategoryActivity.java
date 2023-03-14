package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
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
import com.imranmelikov.examplecommerce.databinding.ActivityCategoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    public ActivityCategoryBinding binding;
    ArrayList<Product> products;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCategoryBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        products=new ArrayList<>();
        productAdapter=new ProductAdapter(this,products);

        int catid=getIntent().getIntExtra("catid",0);
        String categoryname=getIntent().getStringExtra("categoryname");
        getProducts(catid);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);

        binding.productlist.setAdapter(productAdapter);
        binding.productlist.setLayoutManager(gridLayoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryname);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProducts(int catid){
        RequestQueue queue= Volley.newRequestQueue(this);
        String url= Constants.GET_PRODUCTS_URL + "?category_id=" + catid;
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray productarray=jsonObject.getJSONArray("products");
                        for(int i=0;i<productarray.length();i++){
                            JSONObject object=productarray.getJSONObject(i);
                            Product product=new Product(
                                    object.getString("name"),
                                    Constants.PRODUCTS_IMAGE_URL + object.getString("image"),
                                    object.getString("status"),
                                    object.getDouble("price"),
                                    object.getDouble("price_discount"),
                                    object.getInt("stock"),
                                    object.getInt("id")
                            );
                            products.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
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
        queue.add(request);
    }
}