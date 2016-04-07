package com.fhb.powergoldv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class MainMenu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
