package com.fhb.powergoldv1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.facebook.stetho.Stetho;


/**
 * Created by FHB:Taufiq on 3/6/2016.
 */
public class NewRegistration extends NewRegistrationActionBar implements AdapterView.OnItemSelectedListener, OnClickListener {
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
    // delete me String[] pkg_elements;

    PGtables pgTables = new PGtables();

    String[] newMemberDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_registration);

        // Putting toolbar on top of activity
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarMenu();

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

        editTextMemberName.requestFocus(); // set focus to Name field and draw a keyboard below
        this.toggleFocusedSoftKeyboard(); // draw softkeyboard to appear.

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
    public void onClick(View view) {
        if(view == editTextDateJoined) {
            dateJoinedPickerDialog.show();
        }
    }
    // Spinner Method
    private void setSpinnerPackage () {
        // declare the spinner from layout
        spinner_pg_package = (Spinner) findViewById(R.id.spinnerPackageName);
        spinner_pg_package.setPrompt("Purchased Package");

        // -- Spinner setting section.
        // 1) Add spinner object in layout file
        // <Spinner
        //      android:id = ........ />
        //
        // 2) Initialize string array package_list
        pgdb = new DatabaseController(this);
        // pkg_elements = new String[]{"Gold", "Solid Gold", "SPower Gold"};

        //
        // 3) Bound the spinner through array adapter from public ArrayAdapter (Context context, int resource, List<T> object)
        // pgTables.setPackageSpinnerElement();
        ArrayAdapter<String> adapter_pkg = new ArrayAdapter<>(this,
                R.layout.spinner_textview_pkg, pgTables.PACKAGE_NAME);

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
    }

    // Spinner Method
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    private void toggleFocusedSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void setNewMemberDetail (String[] details){
        newMemberDetail = details;
    }
}
