package com.imranmelikov.examplecommerce.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.imranmelikov.examplecommerce.Utils.Constants;
import com.imranmelikov.examplecommerce.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    public ActivityPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        String ordercode=getIntent().getStringExtra("ordercode");

        binding.webview.setMixedContentAllowed(true);
        binding.webview.loadUrl(Constants.PAYMENT_URL + ordercode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}