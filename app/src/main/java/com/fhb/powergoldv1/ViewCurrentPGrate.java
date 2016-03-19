package com.fhb.powergoldv1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class ViewCurrentPGrate extends Activity {

    String url="https://powergold.biz/logon.asp?username=fadhams&password=030506";
    String raw_page;
    Map<String, Float[]> ratePGtype;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pg_gold_rate);
        new GetPGrateTable().execute();

/*
        Button rateButton = (Button) findViewById(R.id.button_CurrentRate);

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPGrateTable().execute();
            }
        });
*/
    }

    private class GetPGrateTable extends AsyncTask<Void, Void, Void> {

        //String raw_page;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewCurrentPGrate.this);
            progressDialog.setTitle("PowerGold Rate");
            progressDialog.setMessage("Loading.....");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Document document = Jsoup.connect(url).get();
                Document document = Jsoup.connect(url).timeout(20000).get();
                //raw_page = document.raw_page();
                raw_page = document.toString();
                Document parseDoc = Jsoup.parse(raw_page);
                String[] indexTable = arrangeTable(parseDoc);

                // Clean indexTable from empty string
                Integer i , k=0; String[] cleanTable = new String[100];
                for (i=0 ; i<indexTable.length ; i++) {
                    if (indexTable[i] == null) continue;
                    if (indexTable[i].length() < 2) continue;
                    cleanTable[k++] = indexTable[i];
                }

                // scrap and return PowerGold Rate arraylist of array only
                ratePGtype = getPowerGoldTable(cleanTable);
                //showTable(ratePGtype);


            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //TextView txtTitle = (TextView) findViewById(R.id.titletxt);
            //txtTitle.setText(raw_page);

            TextView[] gold_type_att_arr = new TextView[12];
            gold_type_att_arr = setTextViewAttGoldTYpe();

            TextView[] stokis_rate_att_arr = new TextView[12];
            stokis_rate_att_arr = setTextViewAttStokisRate();

            TextView[] ahli_rate_att_arr = new TextView[12];
            ahli_rate_att_arr = setTextViewAttAhliRate();

            TextView[] beli_rate_att_arr = new TextView[12];
            beli_rate_att_arr = setTextViewAttBeliRate();

            Float[] val = new Float[12];
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
                stokis_rate_att_arr[i].setText(num0.toString());
                ahli_rate_att_arr[i].setText(num1.toString());
                beli_rate_att_arr[i].setText(num2.toString());
                i++;
            }
            //((TextView)findViewById(R.id.textView10)).setText("test");
            progressDialog.dismiss();
        }
    }

    private Map<String, Float[]> getPowerGoldTable(String[] allRate) {
        String[] ratePG = new String[100];
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

        // output array counter
        Integer header=0;

        // get rate for PowerGold only
        for (i=0 ; i<70 ; i++){
            //if (allRate[i].length() < 2) continue; // skip single white space
            if (i<15 ) continue; // skip to index 15 where PG data starts.
            // remove space-like character ascii code 160.
            //if (allRate[i] == null) continue;
            ratePG[index++] = allRate[i].replace(String.valueOf((char) 160), "").trim();
        }

        // start ordering
        // 1Gram rate
        index = 0; // reset the counter
        for (i=8 ; i<13 ; i=i+2) {
            oneGram[index++] = Float.parseFloat(ratePG[i]);
        }

        // 1GramSy rate
        index = 0; // reset the counter
        for (i=9 ; i<14 ; i=i+2) {
            oneGramSy[index++] = Float.parseFloat(ratePG[i]);
        }

        // 5Gram rate
        index = 0; // reset the counter
        for (i=15 ; i<18 ; i++) {
            fiveGram[index++] = Float.parseFloat(ratePG[i]);
        }

        // 10Gram rate -> data break at index 27
        tenGram[0] = Float.parseFloat(ratePG[23]);
        tenGram[1] = Float.parseFloat(ratePG[29]);
        tenGram[2] = Float.parseFloat(ratePG[34]);

        // 20Gram rate -> data break at index 27
        twentyGram[0] = Float.parseFloat(ratePG[24]);
        twentyGram[1] = Float.parseFloat(ratePG[30]);
        twentyGram[2] = Float.parseFloat(ratePG[35]);

        // 50Gram rate -> data break at index 27
        fiftyGram[0] = Float.parseFloat(ratePG[25]);
        fiftyGram[1] = Float.parseFloat(ratePG[31]);
        fiftyGram[2] = Float.parseFloat(ratePG[36]);

        // 100Gram rate -> data break at index 27
        oneHundredGram[0] = Float.parseFloat(ratePG[26]);
        oneHundredGram[1] = Float.parseFloat(ratePG[32]);
        oneHundredGram[2] = Float.parseFloat(ratePG[37]);

        // 500Gram rate -> data break at index 27
        fiveHundredGram[0] = Float.parseFloat(ratePG[27] + ratePG[28]);
        fiveHundredGram[1] = Float.parseFloat(ratePG[33]);
        fiveHundredGram[2] = Float.parseFloat(ratePG[38]);

        // twoDinar
        index = 0; // reset the counter
        for (i=43 ; i<48 ; i=i+4) {
            twoDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        twoDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // oneDinar
        index = 0; // reset the counter
        for (i=44 ; i<49 ; i=i+4) {
            oneDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        oneDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // quarterDinar
        index = 0; // reset the counter
        for (i=45 ; i<50 ; i=i+4) {
            quarterDinar[index++] = Float.parseFloat(ratePG[i]);
        }
        quarterDinar[index++] = Float.parseFloat(ratePG[i].substring(0, ratePG[i].indexOf("/")));

        // halfGramSy
        index = 0; // reset the counter
        for (i=46 ; i<51 ; i=i+4) {
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

/*
        // Checking the value in tablePG
        Float[] ttt = new Float[12];
        for (i=0 ; i < 12 ; i++) {
            ttt = tablePG.get(headerPGtype[i]);
            System.out.println(headerPGtype[i] + ";" + ttt[0] + ";" + ttt[1] + ";" + ttt[2]);
        }
*/

        return tablePG;
    }

    private String[] arrangeTable (Document sane_doc) { // First Priority Table 999.9 PowerGold

        String startValueStringInTable = "HARGA EMAS POWERGOLD HARI INI";
        String endValueStringInTable = "9:30-5:00pm ISNIN-JUMAAT";

        Integer countElement = 0; // Header Row
        //List<String> header = Arrays.asList("Gold", "Stokis", "Ahli", "Belian");
        String elementTD;
        String[] goldRate = new String[150];
        Integer isFound = 0; //switch in the right element found

        Elements elements = sane_doc.body().select("*");

        for (Element element : elements) {
            if (!element.ownText().isEmpty()) {
                String str = element.ownText().toString().trim();

                // Start saving gold rate table -------------------------------------------
                if (!str.matches("\\S") || str.matches("[0-9:.].*") ) { // non-white space character-\\S
                    if (str.matches(startValueStringInTable)) {
                        countElement = 0; // Reset the counter if right table found
                        goldRate[countElement] = str;
                        countElement = 1; // Reset the counter if right table found
                        isFound = 1;
                        continue;
                    } else if (isFound == 1) {
                        if (str.matches(endValueStringInTable)) { // the line just below the last data value
                            isFound = 0;
                            //break;
                        } else goldRate[countElement] = str;
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

/*
    public void showTable (Map<String, Float[]> tblStr) {
        // Checking the value in tablePG
        // for-each loop, use Map.keySet() for iterating keys, Map.values() for iterating values
        // and Map.entrySet() for iterating key/value pairs.
        Float[] ttt = new Float[12];
        Integer i;
        for(Map.Entry<String, Float[]> entry : tblStr.entrySet()){
            // Since ratePGtype value is an array[], need to create additional step for its value
            ttt = entry.getValue(); // an array of float value
            System.out.printf("%s-> %.2f:%.2f:%.2f\n", entry.getKey(), ttt[0], ttt[1], ttt[2]);
        }
        ((TextView)findViewById(R.id.textView10)).setText("test");
        //TextView r2c1 = R.id.textView10
    }
*/

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
