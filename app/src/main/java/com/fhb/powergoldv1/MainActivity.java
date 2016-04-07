package com.fhb.powergoldv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it is first time login
        DatabaseController pgdb =  new DatabaseController(this);
        PGtables pgTables = new PGtables();
        Boolean isUserExist = pgdb.isTableExists(pgTables.authTableName);
        //Boolean isTableExist = true;

        // if not, open login LoginSetup activity.
        if (!isUserExist) {
            // first time login by user. Get the authentication and Call DatabaseController method.
            Intent i = new Intent(this, PGloginParam.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, MainMenu.class);
            startActivity(i);
        }
    }
/*
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
    }*/
}


