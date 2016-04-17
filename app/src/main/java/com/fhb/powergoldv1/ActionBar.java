package com.fhb.powergoldv1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class ActionBar extends AppCompatActivity {
    Toolbar toolbar;
    Boolean changePassword = false;
    private FileUtils fu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarMenu();
    }

    //AcctionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ActionBar inflater, inflate the view in actionbar from menu xml file
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //AcctionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        getSupportActionBar().setLogo(R.drawable.ic_powergold);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
}

