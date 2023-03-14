package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.imranmelikov.examplecommerce.Adapters.CartAdapter;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.databinding.ActivityCartBinding;
import com.imranmelikov.examplecommerce.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    public ActivityCartBinding binding;
    CartAdapter cartAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        products=new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();

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

        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}