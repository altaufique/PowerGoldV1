package com.fhb.powergoldv1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by FHB:Taufiq on 4/21/2016.
 */
public class MySharedPreferences extends AppCompatActivity {
    private String PREFSNAME = "GOLD_RATE";
    //private SharedPreferences prefs;
    private SharedPreferences prefs;
    private Gson gson;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

    }

    /**
     * Store ArrayList into SharedPreferences
     * @param key String to identify the key to object
     * @param val String to store
     */
    public void storePrefsString(Context ctx, String key, String val) {
        context = ctx;
        // Convert from List array to string
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        // call the Editor method to add the string into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, val);
        editor.commit();
    }

    /**
     * Store ArrayList into SharedPreferences
     * @param key String to identify the key to object
     * @param valList Arraylist to store
     */
    public void storeRePurchaseItem (Context ctx, String key, Map<Integer, String[]> valList) {
        gson = new Gson();
        context = ctx;
        String val = gson.toJson(valList);

        // Convert from List array to string
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        // call the Editor method to add the string into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, val);
        editor.commit();
    }

    /**
     * Store ArrayList into SharedPreferences
     * @param key String to identify the key to object
     * @param valList Arraylist to store
     */
    public void storePrefsList(Context ctx, String key, List<String> valList) {
        gson = new Gson();
        context = ctx;
        String val = gson.toJson(valList);

        // Convert from List array to string
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        // call the Editor method to add the string into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, val);
        editor.commit();
    }

    /**
     * Store ArrayList into SharedPreferences
     * @param key String to identify the key to object
     * @param val Boolean to store
     */
    public void storePrefsBoolean(Context ctx, String key, Boolean val) {
        context = ctx;
        // Convert from List array to string
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        // call the Editor method to add the string into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public Map<Integer, String[]> getPrefsRePurchase (Context ctx, String key) {
        context = ctx;
        gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        // call the "MyKey" key value store in SharedPreferrences.
        String outValue = prefs.getString(key, "");
        // get the correct parameter type name to feed to gson.fromJason,
        // in this case it is java.util.List<java.lang.String>"
        Type type = new TypeToken<Map<Integer,String[]>>() {}.getType();
        // convert back to original List array.
        Map<Integer,String[]> outList = gson.fromJson(outValue, type);

        return outList;
    }

    public List<String> getPrefsList (Context ctx, String key) {
        context = ctx;
        gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

        Map<String, String> check = (Map<String, String>) prefs.getAll();
        // call the "MyKey" key value store in SharedPreferrences.
        String outValue = prefs.getString(key, "");
        // get the correct parameter type name to feed to gson.fromJason,
        // in this case it is java.util.List<java.lang.String>"
        Type type = new TypeToken<List<String>>() {}.getType();
        // convert back to original List array.
        List<String> outList = gson.fromJson(outValue, type);

        return outList;
    }

    public String getPrefsString (Context ctx, String key) {
        context = ctx;
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

        String outValue = prefs.getString(key, "");

        return outValue;
    }

    public Boolean getPrefsBoolean (Context ctx, String key) {
        context = ctx;
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

        Boolean outValue = prefs.getBoolean(key, false);

        return outValue;
    }

    public Boolean removePref (Context ctx, String key) {
        context = ctx;
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

        // call the Editor method to add the string into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        Boolean isRemoved = editor.commit();

        return isRemoved;
    }
/*
    public Map<String, String> getAllPrefsList (Context ctx) {
        context = ctx;
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);

        return (Map<String, String>) prefs.getAll();
    }*/

    public void clearAllPrefs(Context ctx) {
        context = ctx;
        SharedPreferences prefs = context.getSharedPreferences(PREFSNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
