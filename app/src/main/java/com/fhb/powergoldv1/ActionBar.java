package com.fhb.powergoldv1;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class ActionBar extends AppCompatActivity {
    private FileUtils fu;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO - changing color or transparency of status bar
        // Setting status bar color to suit with the style.
        // get the height of the status bar to add padding for toolbar.
        // Set the padding to match the Status Bar height
        // toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    //AcctionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ActionBar inflater, inflate the view in actionbar from menu xml file
        // inflate the action menu
        MenuInflater mi = getMenuInflater();
        switch (getLocalClassName()) {
            case "ViewCurrentPGrate":
                mi.inflate(R.menu.view_current_rate_menu, menu);
                break;
            case "NewRegistration":
                mi.inflate(R.menu.new_registration_menu, menu);
                break;
            case "UpdateRegistration":
                mi.inflate(R.menu.update_registration_menu, menu);
                break;
            default:
                mi.inflate(R.menu.menu_main, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                EditText editTextMemberName = (EditText)findViewById(R.id.editTextMemberName);
                EditText editTextAddress = (EditText)findViewById(R.id.editTextAddress);
                EditText editTextMobileNo = (EditText)findViewById(R.id.editTextPhone);
                EditText editTextICno = (EditText)findViewById(R.id.editTextICno);
                EditText editTextEmail = (EditText)findViewById(R.id.editTextEmail);
                EditText editTextPGusername = (EditText)findViewById(R.id.editTextPGusername);
                EditText editTextBank = (EditText)findViewById(R.id.editTextBank);
                EditText editTextAccNo = (EditText)findViewById(R.id.editTextAccNo);
                EditText editTextDateJoined = (EditText)findViewById(R.id.editTextDateJoined);
                // Date picker setup
                dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                editTextDateJoined.setInputType(InputType.TYPE_NULL);

                Spinner spinner_pg_package = (Spinner) findViewById(R.id.spinnerPackageName);
                // spinner_pg_package.setSelection(position);
                String editTextPGpkg = (String) spinner_pg_package.getSelectedItem();

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
                pgdb.insert_value(pgTables.getMemberTableName(), pgTables.getMemberSchema(), str);
                /*NewRegistration nr = new NewRegistration();
                nr.setNewMemberDetail(str);
                String errMsg = nr.saveActionMenuInsertMember();
                Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();*/

                return true;
            case R.id.change_password:
                //Toast.makeText(getApplicationContext(), "inside change password", Toast.LENGTH_LONG).show();
                Boolean value = true;
                Intent i = new Intent(this, PGloginParam.class);
                i.putExtra("changePassword", value);
                startActivity(i);
                return true;
            case R.id.backup_db:
                fu = new FileUtils(this);

                // Before writing to external, check the availibility and accesss status
                if (!fu.checkExternalStatus().matches("")) {
                    Toast.makeText(this, fu.checkExternalStatus(), Toast.LENGTH_LONG).show();
                    break;
                }

                fu.setDbFilePath();
                fu.setExternalFilePath();
                String dbFilePath = fu.getDbFilePath();
                String dstPath = fu.getExternalFilePath();

                //String dstPath = "C:\\Users\\VAIO\\Desktop\\";
                FileInputStream fis = null;
                FileOutputStream fos = null;
                if (fu.setFileInputStream(dbFilePath)) fis = fu.getFileInputStream();
                if (fu.setFileOutputStream(dstPath)) fos = fu.getFileOutputStream();

                fu.copyFile(fis, fos); // start the file copy operation

                if (fis != null) fu.closeFileInputStream(); // Closing the inputstream
                if (fos != null) fu.closeFileOutputStream(); // close the outputstream

                Toast.makeText(this, "Database successfully backup.", Toast.LENGTH_LONG).show();

                return true;

            case R.id.restore_db:
                fu = new FileUtils(this);
                fu.setExternalFilePath();
                String backupFilePath = fu.getExternalFilePath();
                try {
                    fu.importDatabase(backupFilePath);
                    Toast.makeText(getApplicationContext(), "Database successfully restored.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void setActionBarMenu() {
        //Customize actionBar.
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(String.valueOf(black))));
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setLogo(R.drawable.ic_powergold_small);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

