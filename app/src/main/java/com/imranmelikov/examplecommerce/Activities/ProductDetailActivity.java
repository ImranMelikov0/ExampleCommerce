package com.imranmelikov.examplecommerce.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.R;
import com.imranmelikov.examplecommerce.Utils.Constants;
import com.imranmelikov.examplecommerce.databinding.ActivityMainBinding;
import com.imranmelikov.examplecommerce.databinding.ActivityProductDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    public ActivityProductDetailBinding binding;

    Product currentproduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityProductDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


        String name=getIntent().getStringExtra("name");
        int id=getIntent().getIntExtra("id",0);
        double price= getIntent().getDoubleExtra("price",0);
        String image=getIntent().getStringExtra("image");
        binding.pricelabel.setText(price + " $");
        Glide.with(this)
                .load(image)
                .into(binding.image);

        getProductDetail(id);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);

        Cart cart = TinyCartHelper.getCart();

        binding.Addtocartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.addItem(currentproduct,1);
                binding.Addtocartbtn.setEnabled(false);
                binding.Addtocartbtn.setText("Product Added");
                new AlertDialog.Builder(ProductDetailActivity.this)
                        .setTitle("Product Added")
                        .setCancelable(false)
                        .setPositiveButton("Continue Shopping", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(ProductDetailActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    void getProductDetail(int id){
        RequestQueue queue= Volley.newRequestQueue(this);
        String url= Constants.GET_PRODUCT_DETAILS_URL + id;
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("success"));{
                        JSONObject object = jsonObject.getJSONObject("product");
                        String description = object.getString("description");
                        binding.label.setText(
                                Html.fromHtml(description));
                        currentproduct=new Product(
                                object.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + object.getString("image"),
                                object.getString("status"),
                                object.getDouble("price"),
                                object.getDouble("price_discount"),
                                object.getInt("stock"),
                                object.getInt("id")
                        );
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.cart,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.cart){
           Intent intent=new Intent(this,CartActivity.class);
           startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}