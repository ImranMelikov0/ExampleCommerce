package com.imranmelikov.examplecommerce.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.R;
import com.imranmelikov.examplecommerce.databinding.ItemCartBinding;
import com.imranmelikov.examplecommerce.databinding.QuantityDialogBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    Context context;
    ArrayList<Product> products;
    Cartlistener cartlistener;
    Cart cart;

    public interface Cartlistener{
        public void onQuantitychanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products,Cartlistener cartlistener) {
        this.context = context;
        this.products = products;
        this.cartlistener=cartlistener;
        cart= TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product=products.get(position);
        holder.binding.name.setText(product.getName());
        holder.binding.price.setText(product.getPrice() + " $");
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);
        holder.binding.quantity.setText(product.getQuantity() + " item's");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuantityDialogBinding quantityDialogBinding=QuantityDialogBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog=new AlertDialog.Builder(context)
                        .setView(quantityDialogBinding.getRoot())
                        .create();

                quantityDialogBinding.quantity.setText(String.valueOf(product.getQuantity()));
                quantityDialogBinding.productname.setText(product.getName());
                quantityDialogBinding.productstock.setText("Stock:" + product.getStock());
                int stock=product.getStock();
                quantityDialogBinding.plsbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity=product.getQuantity();
                        quantity++;
                        if(quantity>product.getStock()){
                            Toast.makeText(context, "Max available:" + product.getStock(), Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            product.setQuantity(quantity);
                            quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                        }

                    }
                });
                quantityDialogBinding.mnsbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity=product.getQuantity();
                        if(quantity > 1)
                            quantity--;
                        product.setQuantity(quantity);
                        quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                    }
                });
                quantityDialogBinding.savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        notifyDataSetChanged();
                        cart.updateItem(product,product.getQuantity());
                        cartlistener.onQuantitychanged();
                    }
                });
                dialog.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        ItemCartBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= ItemCartBinding.bind(itemView);
        }
    }
}
