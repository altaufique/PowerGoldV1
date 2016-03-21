package com.fhb.powergoldv1;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VAIO on 3/20/2016.
 */
public class UpdateRegistration extends Activity implements AdapterView.OnItemSelectedListener {
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

    Spinner spinner_member;
    //TextView selVersion; // it says this TextView is to display the dropdown items.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_registration);
        //System.out.println(state.length);

        // declare the spinner from layout
        spinner_member = (Spinner) findViewById(R.id.spinnerMemberName);

        // -- Spinner setting section.
        // 1) Add spinner object in layout file
        // <Spinner
        //      android:id = ........ />
        //
        // 2) Initialize string array member_name from the database to populate in spinner
        List<String> raw_arr_result = new ArrayList<>();
        pgdb = new DatabaseController(this);
        raw_arr_result = pgdb.query_member();
        // for()
        String[] member_name = raw_arr_result.toArray(new String[0]);

        // Test
        //String[] member_name = {"Taufiq", "Mama", "Atok", "Muhammad Ridzuan Bin Zainal Abidin"};

        //
        // 3) Bound the spinner through array adapter from public ArrayAdapter (Context context, int resource, List<T> object)
        ArrayAdapter<String> adapter_member = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, member_name);

        // Set the layout resources to create the dropdown view and bind to spinner object
        adapter_member
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member.setAdapter(adapter_member);

        // Set the spinner to listen to selective events
        spinner_member.setOnItemSelectedListener(this);

    }

    public void onClickUpdateMember(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdateMember:
                // build an array to pass to method Insert_Member in DatabaseController Class
                //Spinner spinner_member = (Spinner) findViewById(R.id.spinnerMemberName);
                //spinner_1.setOnItemSelectedListener(this);

                List<String> member_arr = new ArrayList<String>();

                member_arr = pgdb.query_member();
                if (!member_arr.isEmpty()) {
                    Toast.makeText(UpdateRegistration.this, "Success!! " +
                            editTextMemberName.getText() +
                            " is registered.", Toast.LENGTH_LONG).show();

                    // Clear all the inputs
                    //clearInputDetails();
                    break;
                } else {
                    Toast.makeText(UpdateRegistration.this, "Error !! " +
                            editTextMemberName.getText() +
                            " could not registered.", Toast.LENGTH_LONG).show();
                }
            case R.id.buttonDeleteRecord:
                Toast.makeText(UpdateRegistration.this, "Delete button pressed!!!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // get the selected position id
        spinner_member.setSelection(position);
        String sel_member = (String) spinner_member.getSelectedItem();

        // Populate members detail in update member layout activity
        editTextMemberName = (EditText) findViewById(R.id.editTextMemberName);
        editTextMemberName.setText(sel_member);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
