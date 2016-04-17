package com.fhb.powergoldv1;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by VAIO on 2/20/2016.
 */
public class RePurchase extends ActionBar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repurchase);
        setActionBarMenu();
    }
}
