package com.fhb.powergoldv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

/**
 * Created by VAIO on 3/6/2016.
 */
public class NewMemberRegistration extends Activity {
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
        setContentView(R.layout.new_member_registration);

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
        //onClickInsertMember(member_info);
        //getData()
    }

    public void onClickInsertMember(View v) {
        switch (v.getId()) {
            case R.id.button_Insert_Member:
                // build an array to pass to method Insert_Member in DatabaseController Class
                String[] member_info = {editTextMemberName.getText().toString(),
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
                boolean isInserted = pgdb.insert_member(member_info);
                Toast.makeText(NewMemberRegistration.this, editTextMemberName.getText() + "Success!! Data is inserted.", Toast.LENGTH_LONG).show();
                break;
            case R.id.buttonCancel:
                Toast.makeText(NewMemberRegistration.this, "Cancel button pressed!!!", Toast.LENGTH_LONG).show();
                break;
        }



 /*       if (v.getId() == R.id.button_Insert_Member) {
            Intent i = new Intent(this, DatabaseController.class);
           // startActivity(i);
        }
*/
    }


    /*
    public void getData() {
        buttonGetPkg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the data from database
                        Cursor result = myDB.getAllData(); // Cursor data type as a ref to Cursor class.

                        if(result.getCount() == 0) {
                            // error message
                            showMessage("ERROR", "No data found!!!");
                            return;
                        }

                        // Show the result
                        // set a variable to hold the string
                        StringBuffer stringBuffer = new StringBuffer(); //buffer now is empty

                        // Loop the entire data
                        while (result.moveToNext()) {
                            stringBuffer.append("Package: " + result.getString(1) +"\n");
                            stringBuffer.append("Name: " + result.getString(2) +"\n");
                            stringBuffer.append("Gold_qty: " + result.getString(3) +"\n");
                            stringBuffer.append("Fee: RM" + result.getString(4) + "\n\n");
                        }
                        showMessage("PowerGold Package", stringBuffer.toString());
                    }
                }
        );
    }

    public void showMessage (String raw_page, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(raw_page);
        builder.setMessage(message);
        builder.show();
    }
*/

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
