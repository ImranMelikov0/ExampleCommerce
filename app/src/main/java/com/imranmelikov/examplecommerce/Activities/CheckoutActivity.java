package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.imranmelikov.examplecommerce.Adapters.CartAdapter;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.Utils.Constants;
import com.imranmelikov.examplecommerce.databinding.ActivityCheckoutBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    public ActivityCheckoutBinding binding;
    CartAdapter cartAdapter;
    ArrayList<Product> products;
    double totalprice=0;
    final int tax=11;
    ProgressDialog progressDialog;
    Cart cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCheckoutBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        products=new ArrayList<>();

        cart = TinyCartHelper.getCart();

        for(Map.Entry<Item,Integer> item:cart.getAllItemsWithQty().entrySet()){
            Product product=(Product) item.getKey();
            int quantity=item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        cartAdapter=new CartAdapter(this, products, new CartAdapter.Cartlistener() {
            @Override
            public void onQuantitychanged() {
                binding.subtotal.setText(String.format("%.2f $",cart.getTotalPrice()));
            }
        });
//        products.add(new Product("","","",1,1,1,1));
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        DividerItemDecoration decoration=new DividerItemDecoration(this,layoutManager.getOrientation());
        binding.cartlist.setAdapter(cartAdapter);
        binding.cartlist.setLayoutManager(layoutManager);
        binding.cartlist.addItemDecoration(decoration);
        binding.subtotal.setText(String.format("%.2f $",cart.getTotalPrice()));

        totalprice=(cart.getTotalPrice().doubleValue() * tax/100) + cart.getTotalPrice().doubleValue();
        binding.total.setText(totalprice + " $");

        binding.checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessOrder();
                progressDialog.show();
            }
        });
    }
    void ProcessOrder(){
        RequestQueue queue= Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        JSONObject dataobject=new JSONObject();
        try {
            jsonObject.put("address",binding.addressbox.getText().toString());
            jsonObject.put("buyer",binding.namebox.getText().toString());
            jsonObject.put("comment",binding.commmentbox.getText().toString());
            jsonObject.put("created_at", Calendar.getInstance().getTimeInMillis());
            jsonObject.put("last_update",Calendar.getInstance().getTimeInMillis());
            jsonObject.put("date_ship",Calendar.getInstance().getTimeInMillis());
            jsonObject.put("email",binding.emailbox.getText().toString());
            jsonObject.put("phone",binding.phonebox.getText().toString());
            jsonObject.put("serial","cab8c1a4e4421a3b");
            jsonObject.put("shipping","");
            jsonObject.put("shipping_location","");
            jsonObject.put("shipping_rate","0.0");
            jsonObject.put("status","WAITING");
            jsonObject.put("tax",tax);
            jsonObject.put("total_fees",totalprice);

            JSONArray product_order_detail=new JSONArray();
            for(Map.Entry<Item,Integer> item:cart.getAllItemsWithQty().entrySet()){
                Product product=(Product) item.getKey();
                int quantity=item.getValue();
                product.setQuantity(quantity);
                JSONObject productOBJ=new JSONObject();
                productOBJ.put("amount",quantity);
                productOBJ.put("price_item",product.getPrice());
                productOBJ.put("product_id",product.getId());
                productOBJ.put("product_name",product.getName());
                product_order_detail.put(productOBJ);
            }
            dataobject.put("product_order",jsonObject);
            dataobject.put("product_order_detail",product_order_detail);

            Log.e("err",dataobject.toString());
        }catch (JSONException e){

        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("success")){
                        Toast.makeText(CheckoutActivity.this, "Success order", Toast.LENGTH_SHORT).show();
                        String ordernumber=response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order successful")
                                .setCancelable(false)
                                .setMessage("Your order number :" + ordernumber)
                                .setPositiveButton("Pay now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(CheckoutActivity.this,PaymentActivity.class);
                                        intent.putExtra("ordercode",ordernumber);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }else {
                        Toast.makeText(CheckoutActivity.this, "Failed order", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order failed")
                                .setCancelable(false)
                                .setMessage("Something went wrong please try again")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                    progressDialog.dismiss();
                    Log.e("res",response.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}