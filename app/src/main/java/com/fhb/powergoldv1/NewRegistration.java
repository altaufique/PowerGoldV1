package com.fhb.powergoldv1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputType;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.facebook.stetho.Stetho;


/**
 * Created by FHB:Taufiq on 3/6/2016.
 */
public class NewRegistration extends Activity implements AdapterView.OnItemSelectedListener, OnClickListener {
    DatabaseController pgdb;

    EditText editTextMemberName;
    EditText editTextAddress;
    EditText editTextMobileNo;
    EditText editTextICno;
    EditText editTextEmail;
    EditText editTextPGusername;
    EditText editTextBank;
    EditText editTextAccNo;
    String editTextPGpkg;
    EditText editTextDateJoined;

    private DatePickerDialog dateJoinedPickerDialog;
    private SimpleDateFormat dateFormatter;

    Spinner spinner_pg_package;
    //TextView spinner_textview for spinner items

    public PGtables pgTables = new PGtables();

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
        // using spinner
        editTextDateJoined = (EditText)findViewById(R.id.editTextDateJoined);

        // Date picker setup
        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        editTextDateJoined.setInputType(InputType.TYPE_NULL);
        setDateTimeField();

        setSpinnerPackage();

        callStetho();
    }

    private void setDateTimeField() {
        editTextDateJoined.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dateJoinedPickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDateJoined.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == editTextDateJoined) {
            dateJoinedPickerDialog.show();
        }
    }

    public void onClickInsertMember(View v) {
        switch (v.getId()) {
            case R.id.button_Insert_Member:
                // build an array to pass to method Insert_Member in DatabaseController Class
                String[] member_info = getInputDetails();

                // Checking name field is correctly entered.
                if (member_info[0].length() < 6 || !member_info[0].matches("^[a-zA-Z ]*$")) {
                    Toast.makeText(NewRegistration.this, "Error !! Invalid Name.", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isInserted;
                try {
                    isInserted = pgdb.insert_value(pgTables.memberTableName, pgTables.membersSchema,member_info);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewRegistration.this, "Error !! " +
                            editTextMemberName.getText() +
                            " could not registered. Check username!!", Toast.LENGTH_LONG).show();
                    break;
                }

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
/*            case R.id.buttonCancel:
                Toast.makeText(NewRegistration.this, "Cancel button pressed!!!", Toast.LENGTH_LONG).show();
                break;*/
        }
    }

    // Spinner Method
    private void setSpinnerPackage () {
        // declare the spinner from layout
        spinner_pg_package = (Spinner) findViewById(R.id.spinnerPackageName);

        // -- Spinner setting section.
        // 1) Add spinner object in layout file
        // <Spinner
        //      android:id = ........ />
        //
        // 2) Initialize string array package_list
        pgdb = new DatabaseController(this);
        String[] pkg_elements = {"Gold", "Solid Gold", "SPower Gold"};

        //
        // 3) Bound the spinner through array adapter from public ArrayAdapter (Context context, int resource, List<T> object)
        ArrayAdapter<String> adapter_pkg = new ArrayAdapter<>(this,
                R.layout.spinner_textview_pkg, pkg_elements);

        // Set the layout resources to create the dropdown view and bind to spinner object
        adapter_pkg
                .setDropDownViewResource(R.layout.spinner_textview_pkg);
        spinner_pg_package.setAdapter(adapter_pkg);

        // Set the spinner to listen to selective events
        spinner_pg_package.setOnItemSelectedListener(this);
    }

    // Spinner Method
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // get the selected position id
        spinner_pg_package.setSelection(position);
        editTextPGpkg = (String) spinner_pg_package.getSelectedItem();

        // process the selection ..... TO DO

    }

    // Spinner Method
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Capture all member inputs and save it in a string array
     * @return String array
     */
    public String[] getInputDetails () {
        return new String[]{
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
                //editTextPGpkg.setText("");
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
