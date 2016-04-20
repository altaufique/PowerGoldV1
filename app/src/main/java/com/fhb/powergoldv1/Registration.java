package com.fhb.powergoldv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by VAIO on 2/20/2016.
 */
public class Registration extends ActionBar {
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration); // View must refer to the Registration layout.

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarMenu();

        DatabaseController db = new DatabaseController(this);
        if (db.query_member().isEmpty()) {
            // Boolean updateMember = false; // no data in database. Update member button disable.
            // bt.setTextColor(ContextCompat.getColor(this, R.color.black));
            bt = (Button)findViewById(R.id.buttonUpdateMember);
            bt.setClickable(false);
        }

    }

    public void onClickRegisterMenu(View v) {
        if (v.getId() == R.id.buttonAddMember) {
            Intent i = new Intent(this, NewRegistration.class);
            startActivity(i);
        }

        if (v.getId() == R.id.buttonUpdateMember) {
            Intent i = new Intent(this, UpdateRegistration.class);
            startActivity(i);
        }
 }
}
