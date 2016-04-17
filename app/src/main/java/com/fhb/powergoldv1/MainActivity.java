package com.fhb.powergoldv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it is first time login
        DatabaseController pgdb =  new DatabaseController(this);
        PGtables pgTables = new PGtables();
        //Boolean isUserExist = false;
        Boolean isUserExist = pgdb.isTableExists(pgTables.getAuthTableName());

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
}


