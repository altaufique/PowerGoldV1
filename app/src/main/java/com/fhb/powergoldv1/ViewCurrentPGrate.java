package com.fhb.powergoldv1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by VAIO on 3/11/2016.
 */
public class ViewCurrentPGrate extends ViewCurrentRateActionBar{

    String loginURL;
    String raw_page;
    Map<String, Float[]> ratePGtype;
    ProgressDialog progressDialog;
    PGtables pgTable;
    DatabaseController pgdb;
    List<String> pgRateSP;
    String pgPkgSP;
    TextView tvPackage;
    TextView tvRateDate;
    TextView tvPriceFlux;
    TextView tvPGrateMsg;
    private String rateCategory;

    private static final String[] headerPGtype =
            {"1gm","1gmSy","5gm","10gm","20gm","50gm","100gm","500gm",
            "2dr", "1dr", "1/4dr", "1/2gmSy"};

    private MySharedPreferences pgPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pg_gold_rate);

        pgPrefs = new MySharedPreferences();
        pgTable = new PGtables();
        pgdb = new DatabaseController(this);
        tvPackage = (TextView)findViewById(R.id.textViewRepCat);
        tvRateDate = (TextView)findViewById(R.id.textViewPriceDate);
        tvPriceFlux = (TextView)findViewById(R.id.textViewPriceChange);
        tvPGrateMsg = (TextView)findViewById(R.id.textViewPGrateMsg);
        pgPkgSP = pgPrefs.getPrefsString(getApplicationContext(), "Package");
        tvPackage.setText(pgPkgSP);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarMenu();

        // check if the actionbar refresh event is clicked or not
        Boolean isRefresh=false;
        Bundle bundle = getIntent().getExtras(); // get extra value from caller class
        if (bundle != null) { // refresh ActionBar is clicked
            isRefresh = bundle.getBoolean("refresh");
        }

        // check data in SharedPreference based on Membership PG?
        pgRateSP = null;
        if (pgPkgSP.matches("STOCKIS")) {
            rateCategory = "StokisRate";
            pgRateSP = pgPrefs.getPrefsList(getApplicationContext(), rateCategory);
        } else {
            rateCategory = "MemberRate";
            pgRateSP = pgPrefs.getPrefsList(getApplicationContext(), rateCategory);
        }

        // Check if record exist in DB
        //Boolean isRecInTable = false;
        //String table = pgTable.getRateTableName();
        //isRecInTable = pgdb.isTableExists(table);
        if (pgRateSP != null) { // record exist in database, surely SP data exist, then use SP data
            if (isRefresh) { // refresh button called, get data from web
                new GetPGrateTable().execute();
            } else {
                // prepare the SP data into Map<String, Float[]> format
                ratePGtype = arrangeSPrateTable ();
                setRateChangeDisplay();
                // pgPrefs.storePrefsString(getApplicationContext(), "Date", currDate);
                tvPGrateMsg.setText("Rate as per the latest price change recorded.\n Refresh for latest changes is any.");
                tvRateDate.setText(pgRateSP.get(0));
                displayRate();
            }
        } else { // no record in database, get from web
            new GetPGrateTable().execute();
        }
    }

    private void setRateChangeDisplay() {
        String prefsflux = pgPrefs.getPrefsString(getApplicationContext(), "Flux");
        TextView fluxSymbol = (TextView)findViewById(R.id.textViewTiangle);
        if (prefsflux.contains("-")){
            fluxSymbol.setText("\u25BC"); // Down symbol with red color
            fluxSymbol.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        } else if (prefsflux.matches("0.0") || prefsflux.matches("Initial")) { // show default square
            fluxSymbol.setText("\u25A0"); // Up Symbol with green color
        } else {
            fluxSymbol.setText("\u25B2"); // Up Symbol with green color
            fluxSymbol.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        tvPriceFlux.setText(prefsflux);
    }

    private class GetPGrateTable extends AsyncTask<Void, Void, Void> {
        String pgURL;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pgURL ="https://powergold.biz/logon.asp";

            progressDialog = new ProgressDialog(ViewCurrentPGrate.this);
            progressDialog.setTitle("PowerGold");
            progressDialog.setMessage("Getting Gold Rate.....");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // get the login parameter from database to append to pg URL.
                loginURL = pgURL + pgdb.getAuthParam();

                Document document = Jsoup.connect(loginURL).timeout(20000).get();
                raw_page = document.toString();
                Document parseDoc = Jsoup.parse(raw_page);

                //String startValueStringInTable = "HARGA EMAS POWERGOLD HARI INI";
                String beginString = "1G";
                String endString = "9:30-5:00pm ISNIN-JUMAAT";
                String[] cleanedRateTable = arrangeRateTable(parseDoc, beginString, endString);

                // scrap and return PowerGold Rate arraylist of array only
                // TODO: 4/27/2016
                ratePGtype = getPowerGoldTable(cleanedRateTable);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Float[] stokis_rate_arr = null;
            String currDate = getCurrentDate("dd-MMM-yyyy HH:mm");

            TextView tvRateDate = (TextView)findViewById(R.id.textViewPriceDate);
            tvRateDate.setText(currDate);

            // data error handlng, ratePGtype return null from getPowerGoldTable method
            String[] oneGramRateWeb = displayRate();
            String isPriceChanged = getPriceChange(oneGramRateWeb);
            TextView errMsg = (TextView)findViewById(R.id.textViewPGrateMsg);

            //pgStokisRate.remove(2); // uncomment this to test the price change
            //pgStokisRate.add(2, "150.0"); // uncomment this to test the price change
            // display the price change
            pgPrefs = new MySharedPreferences();
            if (!isPriceChanged.matches("0.0") || isPriceChanged.matches("Initial")
                    || pgPrefs.getPrefsBoolean(getApplicationContext(), "PackageChange")) {

                if (isPriceChanged.matches("Initial")) isPriceChanged = "0.0";
                if (pgPrefs.getPrefsBoolean(getApplicationContext(), "PackageChange")) {
                    rateCategory = "StokisRate";
                    isPriceChanged = "0.0";
                    // clear the pref for PackageChange
                    pgPrefs.removePref(getApplicationContext(), "PackageChange");
                }

                // store the variable in SharedPreferences to pass to other activity
                pgPrefs.storePrefsString(getApplicationContext(), "Flux", isPriceChanged);
                setRateSharedPreferences();

                // Get the latest rate and store in DB
                pgRateSP = pgPrefs.getPrefsList(getApplicationContext(), rateCategory);
                try {
                    pgdb.insert_value(pgTable.getRateTableName(),pgTable.getRateSchema(),convertListToStringArray(pgRateSP));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tvPriceFlux.setText(isPriceChanged);
            setRateChangeDisplay(); // symbol change and update

            progressDialog.dismiss();
        }
    }

    /**
     * To obtain 1gram rate from the web for price comparison from last rate
     * @return String array of 3 elements Stokis, Member and Selling 1gram price rate
     */
    private String[] displayRate() {
        TextView[] gold_type_att_arr = setTextViewAttGoldTYpe();
        TextView[] stokis_rate_att_arr = setTextViewAttStokisRate();
        TextView[] member_rate_att_arr = setTextViewAttAhliRate();
        TextView[] selling_rate_att_arr = setTextViewAttBeliRate();

        Float[] val;
        Integer i=0;
        String[] oneGramWebRate = new String[3]; // for stockis, member and sell rate

        for(Map.Entry<String, Float[]> entry : ratePGtype.entrySet()){
            // Since ratePGtype value is an array[], need to create additional step for its value
            val = entry.getValue(); // an array of float value

            //System.out.printf("%s-> %.2f:%.2f:%.2f\n", entry.getKey(), val[0], val[1], val[2]);
            DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
            String num0 = myFormatter.format(val[0]);
            String num1 = myFormatter.format(val[1]);
            String num2 = myFormatter.format(val[2]);

            // populate the table and display
            gold_type_att_arr[i].setText(entry.getKey());
            stokis_rate_att_arr[i].setText(num0);
            member_rate_att_arr[i].setText(num1);
            selling_rate_att_arr[i].setText(num2);

            if (i == 0) {
                oneGramWebRate[0] = num0;
                oneGramWebRate[1] = num1;
                oneGramWebRate[2] = num2;
            }
            i++;
        }
        return oneGramWebRate;
    }

    public void setRateSharedPreferences () {
        List<String> pgStokisRate = new ArrayList<>();
        List<String> pgMemberRate = new ArrayList<>();
        List<String> pgSellingRate = new ArrayList<>();

        Float[] val;
        String currDate = getCurrentDate("dd-MMM-yyyy HH:mm");
        pgStokisRate.add(currDate);pgStokisRate.add("STOKIS");
        pgMemberRate.add(currDate);pgMemberRate.add("MEMBER");
        pgSellingRate.add(currDate);pgSellingRate.add("SELL");

        for(Map.Entry<String, Float[]> entry : ratePGtype.entrySet()){
            val = entry.getValue(); // an array of float value
            DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
            String num0 = myFormatter.format(val[0]);
            String num1 = myFormatter.format(val[1]);
            String num2 = myFormatter.format(val[2]);

            // value to store in internal and SharedPreferencevariable
            pgStokisRate.add(num0);
            pgMemberRate.add(num1);
            pgSellingRate.add(num2);
        }
        pgPrefs.storePrefsString(getApplicationContext(), "Date", currDate);
        pgPrefs.storePrefsList(getApplicationContext(), "StokisRate", pgStokisRate);
        pgPrefs.storePrefsList(getApplicationContext(), "MemberRate", pgMemberRate);
        pgPrefs.storePrefsList(getApplicationContext(), "SellRate", pgSellingRate);
        // the flux price is registered in isPriceChanged method to reflect instantly.

    }

    private Map<String, Float[]> getPowerGoldTable(String[] ratePG) {
        Integer i;
        Integer index = 0;

        Float[] oneGram = new Float[3];
        Float[] oneGramSy = new Float[3];
        Float[] fiveGram = new Float[3];
        Float[] tenGram = new Float[3];
        Float[] twentyGram = new Float[3];
        Float[] fiftyGram = new Float[3];
        Float[] oneHundredGram = new Float[3];
        Float[] fiveHundredGram = new Float[3];
        Float[] twoDinar = new Float[3];
        Float[] oneDinar = new Float[3];
        Float[] quarterDinar = new Float[3];
        Float[] halfGramSy = new Float[3];

        // start ordering
        // 1Gram rate
        index = 0; // reset the counter
        for (i=2 ; i<8 ; i=i+2) {
            oneGram[index++] = Float.parseFloat(ratePG[i]);
        }

        // 1GramSy rate
        index = 0; // reset the counter
        for (i=3 ; i<9 ; i=i+2) {
            oneGramSy[index++] = Float.parseFloat(ratePG[i]);
        }

        // 5Gram rate
        index = 0; // reset the counter
        for (i=9 ; i<12 ; i++) {
            fiveGram[index++] = Float.parseFloat(ratePG[i]);
        }

        // 10Gram rate -> data break at index 27
        tenGram[0] = Float.parseFloat(ratePG[17]);
        tenGram[1] = Float.parseFloat(ratePG[22]);
        tenGram[2] = Float.parseFloat(ratePG[27]);

        // 20Gram rate -> data break at index 27
        twentyGram[0] = Float.parseFloat(ratePG[18]);
        twentyGram[1] = Float.parseFloat(ratePG[23]);
        twentyGram[2] = Float.parseFloat(ratePG[28]);

        // 50Gram rate -> data break at index 27
        fiftyGram[0] = Float.parseFloat(ratePG[19]);
        fiftyGram[1] = Float.parseFloat(ratePG[24]);
        fiftyGram[2] = Float.parseFloat(ratePG[29]);

        // 100Gram rate -> data break at index 27
        oneHundredGram[0] = Float.parseFloat(ratePG[20]);
        oneHundredGram[1] = Float.parseFloat(ratePG[25]);
        oneHundredGram[2] = Float.parseFloat(ratePG[30]);

        // 500Gram rate -> data break at index 27
        // corrected. previous fiveHundredGram[0] = Float.parseFloat(ratePG[27] + ratePG[28]);
        fiveHundredGram[0] = Float.parseFloat(ratePG[21]);
        fiveHundredGram[1] = Float.parseFloat(ratePG[26]);
        fiveHundredGram[2] = Float.parseFloat(ratePG[31]);

        // twoDinar
        index = 0; // reset the counter
        for (i=36 ; i<44 ; i=i+4) {
            twoDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        twoDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // oneDinar
        index = 0; // reset the counter
        for (i=37 ; i<45 ; i=i+4) {
            oneDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        oneDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // quarterDinar
        index = 0; // reset the counter
        for (i=38 ; i<46 ; i=i+4) {
            quarterDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        quarterDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // halfGramSy
        index = 0; // reset the counter
        for (i=39 ; i<47 ; i=i+4) {
            halfGramSy[index++] = Float.parseFloat(ratePG[i]);
        }
        halfGramSy[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        //List of Float arrays
        List<Float[]> ratePGtype = new ArrayList<Float[]>();
        ratePGtype.add(oneGram);
        ratePGtype.add(oneGramSy);
        ratePGtype.add(fiveGram);
        ratePGtype.add(tenGram);
        ratePGtype.add(twentyGram);
        ratePGtype.add(fiftyGram);
        ratePGtype.add(oneHundredGram);
        ratePGtype.add(fiveHundredGram);
        ratePGtype.add(twoDinar);
        ratePGtype.add(oneDinar);
        ratePGtype.add(quarterDinar);
        ratePGtype.add(halfGramSy);

        // Looping the headerPGtype and ratePGtype to create HashMap data type to pair both together
        Map<String, Float[]> tablePG = new LinkedHashMap<String, Float[]>();
        for (i=0 ; i < 12 ; i++) {
            tablePG.put(headerPGtype[i], ratePGtype.get(i));
        }

        return tablePG;
    }

    /**
     * Special design and unique method only to extract numbers from PGrate table
     * @param sane_doc -> Document type result of Jsoup parse
     * @param beginStr -> String to mark the starting location
     * @param endStr -> String to mark the end location
     * @return -> String array of proper arrange value
     */
    private String[] arrangeRateTable(Document sane_doc, String beginStr, String endStr) { // First Priority Table 999.9 PowerGold

        Integer countElement = 0; // Header Row
        String[] goldRate = new String[150];
        Integer isFound = 0; //switch in the right element found

        Elements elements = sane_doc.body().select("*");

        for (Element element : elements) {
            Integer element_len = element.toString().length();

            if (!element.ownText().isEmpty()) {
                String str = element.ownText().toString().trim();
                // Start saving gold rate table -------------------------------------------
                if (!str.matches("\\S") || str.matches("[0-9:.].*") ) { // non-white space character-\\S
                    // Extract rate table block
                    if (str.matches(beginStr)) {
                        countElement = 0; // Reset the counter if right table found
                        goldRate[countElement] = str;
                        countElement = 1; // Reset the counter if right table found
                        isFound = 1;
                        continue;
                    } else if (isFound == 1) {
                        if (str.matches(endStr)) { // the line just below the last data value
                            isFound = 0;
                            //break;
                        } else {
                            // process str string if number appears to split into two.
                            // its apper the 2nd split number have longer length and smaller digit number
                            if (element_len > 100 && str.length() < 4) {
                                // combine pervious str with splitter number and assigned back.
                                goldRate[countElement-1] = goldRate[countElement-1] + str;
                                continue;
                            } else if (str == null || str.length() < 2) continue;

                            // saving clean array of rate
                            goldRate[countElement] = str.replace(String.valueOf((char) 160), "").trim();
                        }
                    }
                } else {
                    //System.out.println(countElement + "-> " + str);  // uncommented this line to see line not captured.
                    continue; // to prevent increment of countElement for rejected value
                }
                countElement++;
            }
        }
        return goldRate;
    }

    /**
     * Created by Taufiq
     * @return textView array variable
     */
    protected TextView[] setTextViewAttGoldTYpe () {
        //  declare and initiate textview as array string
        TextView[] gold_type_fieldname = new TextView[12];
        gold_type_fieldname[0] = (TextView) findViewById(R.id.textViewGoldType0);
        gold_type_fieldname[1] = (TextView) findViewById(R.id.textViewGoldType1);
        gold_type_fieldname[2] = (TextView) findViewById(R.id.textViewGoldType2);
        gold_type_fieldname[3] = (TextView) findViewById(R.id.textViewGoldType3);
        gold_type_fieldname[4] = (TextView) findViewById(R.id.textViewGoldType4);
        gold_type_fieldname[5] = (TextView) findViewById(R.id.textViewGoldType5);
        gold_type_fieldname[6] = (TextView) findViewById(R.id.textViewGoldType6);
        gold_type_fieldname[7] = (TextView) findViewById(R.id.textViewGoldType7);
        gold_type_fieldname[8] = (TextView) findViewById(R.id.textViewGoldType8);
        gold_type_fieldname[9] = (TextView) findViewById(R.id.textViewGoldType9);
        gold_type_fieldname[10] = (TextView) findViewById(R.id.textViewGoldType10);
        gold_type_fieldname[11] = (TextView) findViewById(R.id.textViewGoldType11);

        return gold_type_fieldname;
    }

    /**
     * Created by Taufiq
     * @return textView array variable
     */
    protected TextView[] setTextViewAttStokisRate () {
        //  declare and initiate textview as array string
        TextView[] stokis_rate_fieldname = new TextView[12];
        stokis_rate_fieldname[0] = (TextView) findViewById(R.id.textViewStokisRate0);
        stokis_rate_fieldname[1] = (TextView) findViewById(R.id.textViewStokisRate1);
        stokis_rate_fieldname[2] = (TextView) findViewById(R.id.textViewStokisRate2);
        stokis_rate_fieldname[3] = (TextView) findViewById(R.id.textViewStokisRate3);
        stokis_rate_fieldname[4] = (TextView) findViewById(R.id.textViewStokisRate4);
        stokis_rate_fieldname[5] = (TextView) findViewById(R.id.textViewStokisRate5);
        stokis_rate_fieldname[6] = (TextView) findViewById(R.id.textViewStokisRate6);
        stokis_rate_fieldname[7] = (TextView) findViewById(R.id.textViewStokisRate7);
        stokis_rate_fieldname[8] = (TextView) findViewById(R.id.textViewStokisRate8);
        stokis_rate_fieldname[9] = (TextView) findViewById(R.id.textViewStokisRate9);
        stokis_rate_fieldname[10] = (TextView) findViewById(R.id.textViewStokisRate10);
        stokis_rate_fieldname[11] = (TextView) findViewById(R.id.textViewStokisRate11);

        return stokis_rate_fieldname;
    }

    /**
     * Created by Taufiq
     * @return textView array variable
     */
    protected TextView[] setTextViewAttAhliRate () {
        //  declare and initiate textview as array string
        TextView[] ahli_rate_fieldname = new TextView[12];
        ahli_rate_fieldname[0] = (TextView) findViewById(R.id.textViewAhliRate0);
        ahli_rate_fieldname[1] = (TextView) findViewById(R.id.textViewAhliRate1);
        ahli_rate_fieldname[2] = (TextView) findViewById(R.id.textViewAhliRate2);
        ahli_rate_fieldname[3] = (TextView) findViewById(R.id.textViewAhliRate3);
        ahli_rate_fieldname[4] = (TextView) findViewById(R.id.textViewAhliRate4);
        ahli_rate_fieldname[5] = (TextView) findViewById(R.id.textViewAhliRate5);
        ahli_rate_fieldname[6] = (TextView) findViewById(R.id.textViewAhliRate6);
        ahli_rate_fieldname[7] = (TextView) findViewById(R.id.textViewAhliRate7);
        ahli_rate_fieldname[8] = (TextView) findViewById(R.id.textViewAhliRate8);
        ahli_rate_fieldname[9] = (TextView) findViewById(R.id.textViewAhliRate9);
        ahli_rate_fieldname[10] = (TextView) findViewById(R.id.textViewAhliRate10);
        ahli_rate_fieldname[11] = (TextView) findViewById(R.id.textViewAhliRate11);

        return ahli_rate_fieldname;
    }

    /**
     * Created by Taufiq
     * @return textView array variable
     */
    protected TextView[] setTextViewAttBeliRate () {
        //  declare and initiate textview as array string
        TextView[] beli_rate_fieldname = new TextView[12];
        beli_rate_fieldname[0] = (TextView) findViewById(R.id.textViewBeliRate0);
        beli_rate_fieldname[1] = (TextView) findViewById(R.id.textViewBeliRate1);
        beli_rate_fieldname[2] = (TextView) findViewById(R.id.textViewBeliRate2);
        beli_rate_fieldname[3] = (TextView) findViewById(R.id.textViewBeliRate3);
        beli_rate_fieldname[4] = (TextView) findViewById(R.id.textViewBeliRate4);
        beli_rate_fieldname[5] = (TextView) findViewById(R.id.textViewBeliRate5);
        beli_rate_fieldname[6] = (TextView) findViewById(R.id.textViewBeliRate6);
        beli_rate_fieldname[7] = (TextView) findViewById(R.id.textViewBeliRate7);
        beli_rate_fieldname[8] = (TextView) findViewById(R.id.textViewBeliRate8);
        beli_rate_fieldname[9] = (TextView) findViewById(R.id.textViewBeliRate9);
        beli_rate_fieldname[10] = (TextView) findViewById(R.id.textViewBeliRate10);
        beli_rate_fieldname[11] = (TextView) findViewById(R.id.textViewBeliRate11);

        return beli_rate_fieldname;
    }

    public void onClickOpenRepurchase (View v){
        Map<String, Integer> rateVarName = new LinkedHashMap<>();
        if (pgPkgSP.matches("STOCKIS")) {
            rateVarName = getStockisRateTextViewVarName();
        } else if (pgPkgSP.matches("MEMBER")) {
            rateVarName = getMemberRateTextViewVarName();
        }
        TextView rate = null;
        String goldType;
        switch (v.getId()) {
            case (R.id.tr1gm):
                goldType = headerPGtype[0];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr1gmSY):
                goldType = headerPGtype[1];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr5gm):
                goldType = headerPGtype[2];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr10gm):
                goldType = headerPGtype[3];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr20gm):
                goldType = headerPGtype[4];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr50gm):
                goldType = headerPGtype[5];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr100gm):
                goldType = headerPGtype[6];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr500gm):
                goldType = headerPGtype[7];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr2dnr):
                goldType = headerPGtype[8];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.tr1dnr):
                goldType = headerPGtype[9];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.trQtrDnr):
                goldType = headerPGtype[10];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
            case (R.id.trHalfGmSy):
                goldType = headerPGtype[11];
                rate = (TextView)findViewById(rateVarName.get(goldType));
                showDialogInput(goldType, rate.getText().toString());
                break;
        }
    }

    private Map<String, Integer> getStockisRateTextViewVarName() {
        Map<String, Integer> varRate = new LinkedHashMap<>();

        for (int i=0; i<headerPGtype.length; i++) {
            int resId = getResources().getIdentifier("textViewStokisRate" + i, "id", getPackageName());
            varRate.put(headerPGtype[i], resId);
        }
        return varRate;
    }

    private Map<String, Integer> getMemberRateTextViewVarName() {
        Map<String, Integer> varRate = new LinkedHashMap<>();

        for (int i=0; i<headerPGtype.length; i++) {
            int resId = getResources().getIdentifier("textViewAhliRate" + i, "id", getPackageName());
            varRate.put(headerPGtype[i], resId);
        }
        return varRate;
    }

    private void showDialogInput(String goldType , String rate) {
        final EditText unitAnsw = new EditText(this);
        unitAnsw.setGravity(1);
        unitAnsw.setSingleLine();
        unitAnsw.setInputType(2);
        unitAnsw.setTextSize(20);
        // set max character Length
        int maxLength = 2;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        unitAnsw.setFilters(FilterArray);

        new AlertDialog.Builder(this)
                .setTitle("RePurchase 999.9 Gold -\n"+goldType+" @RM "+rate)
                .setMessage("Enter unit?")
                .setView(unitAnsw)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String unit = unitAnsw.getText().toString();
                        repurchase(unit);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public String getCurrentDate(String fmt) {
        //Get today date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String strDate = sdf.format(c.getTime());

        return strDate;
    }

    public String[] convertListToStringArray (List<String> lst) {
        String[] arr = new String[lst.size()];
        return lst.toArray(arr);
    }

    public String getPriceChange(String[] OneGramRateFromWeb) {
        // compare current 1gram value with in SP if exist with the latest value
        Float flux= new Float(0);
        if (pgRateSP != null) {
            // data in SP exists.
            Float oneGramWeb = null;
            Float oneGramSP = Float.parseFloat(pgRateSP.get(2));
            // get membership category
            if (pgPkgSP.matches("STOCKIS")) {
                oneGramWeb = Float.parseFloat(OneGramRateFromWeb[0]);
            } else if (pgPkgSP.matches("MEMBER")) {
                oneGramWeb = Float.parseFloat(OneGramRateFromWeb[1]);
            } else { //
                return "Initial";
            }
            // compare both data .....
            flux = oneGramWeb - oneGramSP;
            return flux.toString(); // 0.0 if not change
        } else {
            return "Initial"; // SP not exist, create by passing "initSP" instead of "0.0" to differentiate with compare result
        }
    }

    public Map<String, Float[]> arrangeSPrateTable () {
        List<String> st = pgPrefs.getPrefsList(getApplicationContext(), "StokisRate");
        List<String> mb = pgPrefs.getPrefsList(getApplicationContext(), "MemberRate");
        List<String> sl = pgPrefs.getPrefsList(getApplicationContext(), "SellRate");

        Map<String, Float[]> allrate = new LinkedHashMap<>();
        int type = headerPGtype.length;
        Float[] temp = null; int k=0;
        for (int i=0; i<type; i++) {
            temp = new Float[3];
            // remove "," from the numbers
            temp[0] = Float.parseFloat(st.get(k + 2).replace(",", "")); // rate start at index 2
            temp[1] = Float.parseFloat(mb.get(k + 2).replace(",", "")); // rate start at index 2
            temp[2] = Float.parseFloat(sl.get(k + 2).replace(",", "")); // rate start at index 2

            allrate.put(headerPGtype[i], temp);
            k++;
        }
        return allrate;
    }

    public void repurchase(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
