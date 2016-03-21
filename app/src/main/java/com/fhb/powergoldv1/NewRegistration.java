package com.fhb.powergoldv1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

/**
 * Created by VAIO on 3/6/2016.
 */
public class NewRegistration extends Activity {
    DatabaseController pgdb;

    EditText editTextMemberName;
    EditText editTextAddress;
    EditText editTextMobileNo;
    EditText editTextICno;
    EditText editTextEmail;
    EditText editTextPGusername;
    EditText editTextBank;
    EditText editTextAccNo;
    EditText editTextPGpkg;
    EditText editTextDateJoined;

    Button buttonRecordMember;
    Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_registration);

        pgdb = new DatabaseController(this);

        // Casting declared Widgets
        editTextMemberName = (EditText)findViewById(R.id.editTextMemberName);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editTextMobileNo = (EditText)findViewById(R.id.editTextPhone);
        editTextICno = (EditText)findViewById(R.id.editTextICno);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPGusername = (EditText)findViewById(R.id.editTextPGusername);
        editTextBank = (EditText)findViewById(R.id.editTextBank);
        editTextAccNo = (EditText)findViewById(R.id.editTextAccNo);
        editTextPGpkg = (EditText)findViewById(R.id.editTextPGpackage);
        editTextDateJoined = (EditText)findViewById(R.id.editTextDateJoined);

        callStetho();
    }

    public void onClickInsertMember(View v) {
        switch (v.getId()) {
            case R.id.button_Insert_Member:
                // build an array to pass to method Insert_Member in DatabaseController Class
                String[] member_info = getInputDetails();

                boolean isInserted = pgdb.insert_member(member_info);
                if (isInserted) {
                    Toast.makeText(NewRegistration.this, "Success!! " +
                            editTextMemberName.getText() +
                            " is registered.", Toast.LENGTH_LONG).show();

                    // Clear all the inputs
                    clearInputDetails();
                    break;
                } else {
                    Toast.makeText(NewRegistration.this, "Error !! " +
                            editTextMemberName.getText() +
                            " could not registered.", Toast.LENGTH_LONG).show();
                }
            case R.id.buttonCancel:
                Toast.makeText(NewRegistration.this, "Cancel button pressed!!!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Capture all member inputs and save it in a string array
     * @param ()
     * @return String array
     */
    public String[] getInputDetails () {
        String[] details = {
                editTextMemberName.getText().toString(),
                editTextAddress.getText().toString(),
                editTextMobileNo.getText().toString(),
                editTextICno.getText().toString(),
                editTextEmail.getText().toString(),
                editTextPGusername.getText().toString(),
                editTextBank.getText().toString(),
                editTextAccNo.getText().toString(),
                editTextPGpkg.getText().toString(),
                editTextDateJoined.getText().toString()
        };
        return details;
    }

    public void clearInputDetails () {
                editTextMemberName.setText("");
                editTextAddress.setText("");
                editTextMobileNo.setText("");
                editTextICno.setText("");
                editTextEmail.setText("");
                editTextPGusername.setText("");
                editTextBank.setText("");
                editTextAccNo.setText("");
                editTextPGpkg.setText("");
                editTextDateJoined.setText("");
    }

    // Call Stheto library to do database and other resources checking
    public void callStetho () {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

}
