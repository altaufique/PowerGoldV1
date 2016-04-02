package com.fhb.powergoldv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseController dbController = new DatabaseController(this);
    }

    public void onClickMainMenu(View v) {
        if (v.getId() == R.id.buttonAddMember) {
            Intent i = new Intent(this, Registration.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_CurrentRate) {
            Intent i = new Intent(this, ViewCurrentPGrate.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_Purchase) {
            Intent i = new Intent(this, RePurchase.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_MyInventory) {
            Intent i = new Intent(this, MyInventory.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_PackageInfo) {
            Intent i = new Intent(this, PackageInfo.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_About) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
    }
}
