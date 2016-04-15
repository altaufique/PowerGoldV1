package com.fhb.powergoldv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import java.util.LinkedHashMap;
import java.util.Locale;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by VAIO on 3/20/2016.
 */
public class UpdateRegistration extends Activity implements AdapterView.OnItemSelectedListener, OnClickListener {
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

    Button buttonUpdate;
    Button buttonDelete;

    private DatePickerDialog dateJoinedPickerDialog;
    private SimpleDateFormat dateFormatter;

    Spinner spinner_pg_package;
    String[] pkg_elements = {"Gold", "Solid Gold", "SPower Gold"};
    Map<Integer, String> pkg_elements_map = new LinkedHashMap<>();

    Spinner spinner_member;
    Integer col_ID;
    List<List<String>> raw_arr_result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_registration);

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
        //editTextPGpkg = (EditText)findViewById(R.id.editTextPGpackage);
        editTextDateJoined = (EditText)findViewById(R.id.editTextDateJoined);

        buttonUpdate = (Button)findViewById(R.id.buttonEditRecord);
        buttonDelete = (Button)findViewById(R.id.buttonDeleteRecord);

        // Date picker setup
        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        editTextDateJoined.setInputType(InputType.TYPE_NULL);
        setDateTimeField();

        setSpinnerMember();
        setSpinnerPackage();

        editTextMemberName.requestFocus(); // set focus to Name field and draw a keyboard below
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); // other option



        onClickDeleteMember(); // set onclick for confirmation action

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

    private void setSpinnerMember () {
        // declare the spinner from layout
        spinner_member = (Spinner) findViewById(R.id.spinnerMemberName);
        spinner_member.setPrompt("Edit Member...");

        // -- Spinner setting section.
        // 1) Add spinner object in layout file
        // <Spinner
        //      android:id = ........ />
        //
        // 2) Initialize string array member_name from the database to populate in spinner
        pgdb = new DatabaseController(this);
        raw_arr_result = pgdb.query_member();

        // loop to get member name)
        // initialise the collection
        //List<List<String>> collection = new ArrayList<>();

        // capture id and member names pair in SQLite using Map. Pass to onItemSelect to get right id
        //member_hm = new LinkedHashMap<>();
        // member_lst to pupulate the spinner list.
        String[] member_lst = new String[raw_arr_result.size()];
        // iterate
        Integer i=0; Integer count=raw_arr_result.size();
        for (List<String> innerList : raw_arr_result) {
            member_lst[i++] = innerList.get(1); // get the name field 1 into array
            //member_hm.put(Integer.parseInt(innerList.get(0)),innerList.get(1));
        }

        ArrayAdapter<String> adapter_member = new ArrayAdapter<>(this,
                R.layout.spinner_textview_name, member_lst);

        // Set the layout resources to create the dropdown view and bind to spinner object
        adapter_member
                .setDropDownViewResource(R.layout.spinner_textview_name);
        spinner_member.setAdapter(adapter_member);

        // Set the spinner to listen to selective events
        spinner_member.setOnItemSelectedListener(this);
    }

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
        pkg_elements_map.put(0, "Gold");
        pkg_elements_map.put(1, "Solid Gold");
        pkg_elements_map.put(2, "SPower Gold");
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // get the selected position id

        if (parent == findViewById(R.id.spinnerPackageName)) {
            spinner_pg_package.setSelection(position);
            editTextPGpkg = (String) spinner_pg_package.getSelectedItem();
        } else {
            spinner_member.setSelection(position);
            String sel_member = (String) spinner_member.getSelectedItem();

            // iterate
            Integer i = 0;
            List<String> sel_attr = new ArrayList<>();
            for (List<String> innerList : raw_arr_result) {
                if (innerList.get(1).matches(sel_member))
                    for (String value : innerList) {
                        //sel_attr.add(innerList.get(i++)); // get the name field 1 into array
                        sel_attr.add(value); // get the name field 1 into array
                    }
            }
            // Populate members detail layout activity.
            col_ID = Integer.parseInt(sel_attr.get(0));
            displayMemberDetails(sel_attr);
        }
    }

    // Spinner method
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO
    }

    // Spinner method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Method to change default position in spinner by increment or decrement (minus integer sign)
     * @param s is spinner object
     * @param position is increment integer
     */
    public void changeSpinnerPosition (Spinner s, Integer position){
        int spinnerPosition = s.getSelectedItemPosition() + position;
        this.setSpinnerMember();
        s.setSelection(spinnerPosition);
    }

    public void onClickEditMember(View v) {
        String[] member_details = getInputDetails();

        // Checking name field is correctly entered.
        if (member_details[0].length() < 6 || !member_details[0].matches("^[a-zA-Z ]*$")) {
            Toast.makeText(UpdateRegistration.this, "Error !! Invalid Name.", Toast.LENGTH_LONG).show();
            return;
        }

        Boolean isUpdated = pgdb.updateMember(col_ID, member_details);
        if (isUpdated) {
            Toast.makeText(UpdateRegistration.this, "Success!! " +
                    editTextMemberName.getText() +
                    " is updated.", Toast.LENGTH_LONG).show();

            // save the selected value, refresh the view and defaulted back to selected value;
            changeSpinnerPosition(spinner_member, 0);

        } else {
            Toast.makeText(UpdateRegistration.this, "Error !! " +
                    editTextMemberName.getText() +
                    " could not update.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * call from onCreate to set the confirmation dialog before deleting the db record.
     * Handle it separately in order to assign AlertDialog confirmation prompt when clicked
     */
    public void onClickDeleteMember() {
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateRegistration.this);
                builder.setTitle("Delete DB record.").setMessage("Are you sure???").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the selected record.
                        // Boolean isDeleted = true;
                        Boolean isDeleted = pgdb.deleteMember(col_ID);
                        if (isDeleted) {
                            Toast.makeText(UpdateRegistration.this, "Success!! " +
                                    editTextMemberName.getText() +
                                    " is deleted.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UpdateRegistration.this, "Error !! " +
                                    editTextMemberName.getText() +
                                    " could not delete.", Toast.LENGTH_LONG).show();
                        }

                        Log.d("FHB", "You choose Yes button.");
                        // Change spinner to new position omitting deleted record.
                        changeSpinnerPosition(spinner_member, -1);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("FHB", "You choose No button.");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Date Picker method
    @Override
    public void onClick(View view) {
        if(view == editTextDateJoined) {
            dateJoinedPickerDialog.show();
        }
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

    public void displayMemberDetails (List<String> list) {
        editTextMemberName.setText(list.get(1));
        editTextAddress.setText(list.get(2));
        editTextMobileNo.setText(list.get(3));
        editTextICno.setText(list.get(4));
        editTextEmail.setText(list.get(5));
        editTextPGusername.setText(list.get(6));
        editTextBank.setText(list.get(7));
        editTextAccNo.setText(list.get(8));
        for(Map.Entry<Integer, String> entry : pkg_elements_map.entrySet()){
            if (list.get(9).equals(entry.getValue())) {
                System.out.println(entry.getKey());
                spinner_pg_package.setSelection(entry.getKey());
            }
        }
        editTextDateJoined.setText(list.get(10));
    }
}
