package com.fhb.powergoldv1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by VAIO on 2/20/2016.
 */
public class RePurchase extends ActionBar {
    private FileUtils fu;
    private TextView rePurchaseGoldType;
    private TextView rePurchaseRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repurchase);

        rePurchaseGoldType = (TextView)findViewById(R.id.repurchaseGoldType);
        rePurchaseRate = (TextView)findViewById(R.id.repurchaseRate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarMenu();

        // retrieve the selected gold type and rate from calling activity
        String goldType = getIntent().getStringExtra("GoldType");
        String rate = getIntent().getStringExtra("Rate");

        rePurchaseGoldType.setText(goldType);
        rePurchaseRate.setText(rate);

        //Toast.makeText(RePurchase.this, "GoldType - " + goldType + " and Rate - " + rate,Toast.LENGTH_LONG).show();
//        MySharedPreferences sp = new MySharedPreferences();
//        Map<String, String> check = sp.getAllPrefsList(getApplicationContext());

    }
}
