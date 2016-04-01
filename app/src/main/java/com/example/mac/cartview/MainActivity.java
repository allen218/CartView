package com.example.mac.cartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mac.cartview.view.CartView;

public class MainActivity extends AppCompatActivity {

    private Button mBtn;
    private CartView mCartView;
    private ImageView mShoppingCartIc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        mBtn = (Button) findViewById(R.id.btn);
        mCartView = (CartView) findViewById(R.id.cart_view);
        mShoppingCartIc = (ImageView) findViewById(R.id.shopping_cart_ic);


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] location = new int[2];
                mCartView.getLocationOnScreen(location);
                mCartView.setViewDefaultLocation(location[0], location[1]);

                mCartView.startAddCartAnimation(MainActivity.this, mShoppingCartIc);
//                mCartView.setAddOneAnimation(MainActivity.this, 2000);
            }
        });
    }

}
