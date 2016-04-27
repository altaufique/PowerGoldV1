package com.fhb.powergoldv1;

import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class NewRegistrationActionBar extends ActionBar {
    EditText editTextMemberName;
    EditText editTextAddress;
    EditText editTextMobileNo;
    EditText editTextICno;
    EditText editTextEmail;
    EditText editTextPGusername;
    EditText editTextBank;
    EditText editTextAccNo;
    EditText editTextDateJoined;

    Spinner spinner_pg_package;
    // spinner_pg_package.setSelection(position);
    String editTextPGpkg;
    //ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        editTextMemberName = (EditText)findViewById(R.id.editTextMemberName);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editTextMobileNo = (EditText)findViewById(R.id.editTextPhone);
        editTextICno = (EditText)findViewById(R.id.editTextICno);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPGusername = (EditText)findViewById(R.id.editTextPGusername);
        editTextBank = (EditText)findViewById(R.id.editTextBank);
        editTextAccNo = (EditText)findViewById(R.id.editTextAccNo);
        editTextDateJoined = (EditText)findViewById(R.id.editTextDateJoined);

        spinner_pg_package = (Spinner) findViewById(R.id.spinnerPackageName);
        // spinner_pg_package.setSelection(position);
        editTextPGpkg = (String) spinner_pg_package.getSelectedItem();

        // Checking name field is correctly entered.
        if (editTextMemberName.getText().toString().length() < 6 ||
                !editTextMemberName.getText().toString().matches("^[a-zA-Z ]*$")) {
            Toast.makeText(NewRegistrationActionBar.this, "Error!! Invalid Name" ,Toast.LENGTH_SHORT).show();
            return false;
        }
        String[] str = {
            editTextMemberName.getText().toString(),
            editTextAddress.getText().toString(),
            editTextMobileNo.getText().toString(),
            editTextICno.getText().toString(),
            editTextEmail.getText().toString(),
            editTextPGusername.getText().toString(),
            editTextBank.getText().toString(),
            editTextAccNo.getText().toString(),
            editTextPGpkg,
            editTextDateJoined.getText().toString()
        };

        DatabaseController pgdb = new DatabaseController(this);
        PGtables pgTables = new PGtables();
        boolean isInserted = false;
        try {
            isInserted = pgdb.insert_value(pgTables.getMemberTableName(), pgTables.getMemberSchema(), str);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(NewRegistrationActionBar.this, "Error !! " + e.toString() ,Toast.LENGTH_SHORT).show();
            Log.d("FHB", e.toString());
            return false;

        }

        if (isInserted) {
            // Clear all the inputs
            Toast.makeText(NewRegistrationActionBar.this, "Success!! New User is registered",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(NewRegistrationActionBar.this, "Error !! Could not registered. ",Toast.LENGTH_SHORT).show();
            return false;

        }
        /*NewRegistration nr = new NewRegistration();
        nr.setNewMemberDetail(str);
        String errMsg = nr.saveActionMenuInsertMember();
        Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();*/

        clearAllInput();

        return super.onOptionsItemSelected(item);
    }

    public void clearAllInput() {

        // clear all inputs
        editTextMemberName.setText("");
        editTextAddress.setText("");
        editTextMobileNo.setText("");
        editTextICno.setText("");
        editTextEmail.setText("");
        editTextPGusername.setText("");
        editTextBank.setText("");
        editTextAccNo.setText("");
        //editTextPGpkg.setText("");
        editTextDateJoined.setText("");

        editTextMemberName.requestFocus();

        // Reset package spinner to topmost.
        spinner_pg_package.setSelection(0);

    }
}

