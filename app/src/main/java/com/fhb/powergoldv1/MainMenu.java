package com.fhb.powergoldv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class MainMenu extends ActionBar {
    private Map<String, String> userInfo;
    DatabaseController pgdb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarMenu();

        // set user summary for name, package and date registered
        pgdb =  new DatabaseController(this);
        this.getUserInfo(pgdb.queryAuth());
        this.displayUserInfo();
    }

    private void displayUserInfo() {
        TextView user = (TextView)findViewById(R.id.textViewUsername);
        TextView pkg = (TextView)findViewById(R.id.textViewPackage);
        TextView reg = (TextView)findViewById(R.id.textViewRegDate);
        for (Map.Entry<String,String>  entry:userInfo.entrySet()) {
            if (entry.getKey().matches("PG_USERNAME")) {
                user.setText(entry.getValue());
            } else if (entry.getKey().matches("PACKAGE")) {
                    pkg.setText(entry.getValue());
            } else if (entry.getKey().matches("DATE_REGISTERED")) {
                //dateFormatter.format("dd-MMM-yyyy", Locale.US);
                SimpleDateFormat dateFormatter =  new SimpleDateFormat("cccc, MMMM d, yyyy");
                Date newDate = null;

                try {
                    newDate = dateFormatter.parse(entry.getValue().toString());
                    SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                    String newDateStr = newFormat.format(newDate);
                    reg.setText(newDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getUserInfo(Map<String, String> userList) {
        MySharedPreferences sp = new MySharedPreferences();
        //store package into SharedPreferences
        //sp.clearAllPrefs(getApplicationContext()); // to clear all rates for test purpose


        userInfo = new LinkedHashMap<>();
        for (Map.Entry<String,String> entry:userList.entrySet()) {
            if (entry.getKey().matches("PG_USERNAME")) {
                userInfo.put(entry.getKey(),entry.getValue());
            } else if (entry.getKey().matches("PACKAGE")) {
                if (entry.getValue().matches("SUPER POWER GOLD")) {
                    // check if package changes from Gold or Solid Gold between SP and AUTH table
                    if (!sp.getPrefsString(getApplicationContext(),"Package").matches("STOCKIS")) {
                        // package changed. Save the changes in SP
                        sp.storePrefsBoolean(getApplicationContext(), "PackageChange", true);
                    }
                    entry.setValue("S.POWERGOLD"); // replace to display shorter name
                    //sp.storePrefsString(getApplicationContext(), "Package", "MEMBER"); // test purpose
                    sp.storePrefsString(getApplicationContext(), "Package", "STOCKIS");
                } else {
                    sp.storePrefsString(getApplicationContext(), "Package", "MEMBER");
                }
                userInfo.put(entry.getKey(), entry.getValue());
            } else if (entry.getKey().matches("DATE_REGISTERED")) {
                userInfo.put(entry.getKey(),entry.getValue());
            }
        }
    }

    public void onClickMainMenu(View v) {
        if (v.getId() == R.id.buttonAddMember) {
            Intent i = new Intent(this, Registration.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_CurrentRate) {
            Intent i = new Intent(this, ViewCurrentPGrate.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_Purchase) {
            Intent i = new Intent(this, RePurchase.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_MyInventory) {
            Intent i = new Intent(this, MyInventory.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_PackageInfo) {
            Intent i = new Intent(this, PackageInfo.class);
            startActivity(i);
        }

        if (v.getId() == R.id.button_About) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
    }
}
