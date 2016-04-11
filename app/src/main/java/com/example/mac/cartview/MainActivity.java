package com.example.mac.cartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mac.cartview.view.CartView;
import com.example.mac.cartview.view.ClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ClickListener {

    private Button mBtn;
    private CartView mCartView;
    private ImageView mShoppingCartIc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        currentNum = 12;
        mCartView.setCurrentNum(currentNum);
    }

    private int currentNum;

    private void initView() {
        mBtn = (Button) findViewById(R.id.btn);
        mCartView = (CartView) findViewById(R.id.cart_view);
        mShoppingCartIc = (ImageView) findViewById(R.id.shopping_cart_ic);
        mCartView.setClickListener(this);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNum++;

                int[] location = new int[2];
                mCartView.getLocationOnScreen(location);
                mCartView.setViewDefaultLocation(location[0], location[1]);

                mCartView.startAddCartAnimation(MainActivity.this, mShoppingCartIc);

                mCartView.setCurrentNum(currentNum);

//                mCartView.setAddOneAnimation(MainActivity.this, 2000);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.cart_view:
                Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
