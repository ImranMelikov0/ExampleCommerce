package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imranmelikov.examplecommerce.Adapters.CategoryAdapter;
import com.imranmelikov.examplecommerce.Adapters.ProductAdapter;
import com.imranmelikov.examplecommerce.Models.Category;
import com.imranmelikov.examplecommerce.Models.Product;
import com.imranmelikov.examplecommerce.R;
import com.imranmelikov.examplecommerce.Utils.Constants;
import com.imranmelikov.examplecommerce.databinding.ActivityMainBinding;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    ArrayList<Category> categories;
    CategoryAdapter categoryAdapter;
    ArrayList<Product> products;
    ProductAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        initcategories();
        initproduct();
        initSlider();

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("query",text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    void initSlider(){
        binding.carousel.addData(new CarouselItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRODugclY3VcoiUgyAa6xsmXGQkwQJzBOZMKw&usqp=CAU","Big offer 35% discount!!! "));
        binding.carousel.addData(new CarouselItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRK5ekkg5sOXXkOOnB-nZdpxTzoLOzGQ43eNg&usqp=CAU","40% discount for 3 purchases"));
        binding.carousel.addData(new CarouselItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwS4FR1AqNDPBHqp__2Hl5SQ58gDh6mLfGC8isYc9LmgDPlPDnZafaZM9cuwa_IHzJutc&usqp=CAU","55% discount for 5 purchases"));
    }



    void getRecentProducts(){
        RequestQueue queue=Volley.newRequestQueue(this);
        String url=Constants.GET_PRODUCTS_URL + "?count=8";
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

    void initproduct(){
        products=new ArrayList<>();
        productAdapter=new ProductAdapter(this,products);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);

        getRecentProducts();

        binding.productlist.setLayoutManager(gridLayoutManager);
        binding.productlist.setAdapter(productAdapter);
    }

    void getCategories(){
        RequestQueue queue= Volley.newRequestQueue(this);
        StringRequest request=new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray categoriesarray=jsonObject.getJSONArray("categories");
                        for(int i=0;i<categoriesarray.length();i++){
                            JSONObject object=categoriesarray.getJSONObject(i);
                            Category category=new Category(
                                    object.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            categories.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
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

    void initcategories(){
        categories=new ArrayList<>();
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
//        categories.add(new Category("name","","27438a","",1));
        categoryAdapter=new CategoryAdapter(this,categories);

        getCategories();

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,10);

        binding.categorieslist.setAdapter(categoryAdapter);
        binding.categorieslist.setLayoutManager(gridLayoutManager);
    }

}