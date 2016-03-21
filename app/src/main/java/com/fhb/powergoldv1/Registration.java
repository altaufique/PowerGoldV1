package com.fhb.powergoldv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by VAIO on 2/20/2016.
 */
public class Registration extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration); // View must refer to the Registration layout.
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
