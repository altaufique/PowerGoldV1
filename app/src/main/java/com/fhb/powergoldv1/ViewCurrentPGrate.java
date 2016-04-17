package com.fhb.powergoldv1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VAIO on 3/11/2016.
 */
public class ViewCurrentPGrate extends ActionBar {

    String pgURL;
    String loginURL;
    String raw_page;
    Map<String, Float[]> ratePGtype;
    ProgressDialog progressDialog;
    DatabaseController pgdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pg_gold_rate);
        setActionBarMenu();

        // get PG logon parameter from database AUTHENTICATION
        pgURL ="https://powergold.biz/logon.asp";
        pgdb =  new DatabaseController(this);
        new GetPGrateTable().execute();
    }

    private class GetPGrateTable extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                ratePGtype = getPowerGoldTable(cleanedRateTable);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // data error handlng, ratePGtype return null from getPowerGoldTable method
            if (ratePGtype != null) {
                TextView[] gold_type_att_arr;
                gold_type_att_arr = setTextViewAttGoldTYpe();

                TextView[] stokis_rate_att_arr;
                stokis_rate_att_arr = setTextViewAttStokisRate();

                TextView[] ahli_rate_att_arr;
                ahli_rate_att_arr = setTextViewAttAhliRate();

                TextView[] beli_rate_att_arr;
                beli_rate_att_arr = setTextViewAttBeliRate();
// TODO - use the variable below to manage the re purchase
                Float[] val;
                Integer i=0;
                for(Map.Entry<String, Float[]> entry : ratePGtype.entrySet()){
                    // Since ratePGtype value is an array[], need to create additional step for its value
                    val = entry.getValue(); // an array of float value

                    //System.out.printf("%s-> %.2f:%.2f:%.2f\n", entry.getKey(), val[0], val[1], val[2]);
                    DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
                    String num0 = myFormatter.format(val[0]);
                    String num1 = myFormatter.format(val[1]);
                    String num2 = myFormatter.format(val[2]);

                    gold_type_att_arr[i].setText(entry.getKey());
                    stokis_rate_att_arr[i].setText(num0);
                    ahli_rate_att_arr[i].setText(num1);
                    beli_rate_att_arr[i].setText(num2);
                    i++;
                }
            } else {
                TextView errMsg = (TextView)findViewById(R.id.textViewPGrateMsg);
                errMsg.setText("Data Error or check PG password!!");
            }
            progressDialog.dismiss();
        }
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

        // check PG rate arrays in the ArrayList as in PG formal table web rate
        //for (Float[] strArr : ratePGtype) {
        //    System.out.println(Arrays.toString(strArr));
        //}

        String[] headerPGtype = {"1 gram","1 gramSy","5 gram","10 gram","20 gram","50 gram","100 gram","500 gram",
                "2 Dinar", "1 Dinar", "1/4 Dinar", "1/2 gramSy"};

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
        //List<String> header = Arrays.asList("Gold", "Stokis", "Ahli", "Belian");
        String elementTD;
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
}
