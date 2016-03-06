package com.fhb.powergoldv1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by VAIO on 3/5/2016.
 */

public class ScrapPowerGoldWeb {

    private List<String> cookies;
    private HttpsURLConnection conn;
    private String username;
    private String password;

    private final String USER_AGENT = "Mozilla/5.0";

    public static HashMap<String, Float[]> main(String[] args) throws Exception {

        String url = "https://powergold.biz/default.asp";
        String memberpage = "https://powergold.biz/logon.asp";
        String username = "altaufiquemas";
        String password = "Fn_912522#";

        ScrapPowerGoldWeb http = new ScrapPowerGoldWeb();

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        // 1. Send a "GET" request, so that you can extract the form's data.
        String page = http.getPageContent(url);

        // set Powergold logon parameter to append to URL (?xxxxxx&yyyyyyy)
        String postParams = http.getFormParams(page, username, password);

        // 2. Construct above post's content and then send a POST request to login
        // http.sendPost(memberpage, postParams); //not success... step 3 directly login and get page content.

        // 3. success then go to memberpage to scrap the page content after login
        String result = http.getPageContent(memberpage + "?" + postParams);

        // call method to arrange and indexed the table content in array
        Document parseDoc = Jsoup.parse(result);
        String[] indexTable = http.arrangeTable(parseDoc);

        // scrap and return PowerGold Rate arraylist of array only
        HashMap<String, Float[]> ratePGtype = http.getPowerGoldTable(indexTable);

/*
        // Checking the value in tablePG
        // for-each loop, use Map.keySet() for iterating keys, Map.values() for iterating values
        // and Map.entrySet() for iterating key/value pairs.
        Float[] ttt = new Float[12];
        Integer i;
        for(Map.Entry<String, Float[]> entry : ratePGtype.entrySet()){
            // Since ratePGtype value is an array[], need to create additional step for its value
            ttt = entry.getValue(); // an array of float value
            System.out.printf("%s-> %.2f:%.2f:%.2f\n", entry.getKey(), ttt[0], ttt[1], ttt[2]);
        }
*/
        return ratePGtype; // will return HashMap data type of key and value ("1gm", Float[], ...)
    }

    private HashMap<String, Float[]> getPowerGoldTable(String[] allRate) {
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
            if (i<15) continue; // skip to index 15 where PG data starts.
            // remove space-like character ascii code 160.
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

        String[] headerPGtype = {"1gm","1gmSy","5gm","10gm","20gm","50gm","100gm","500gm",
                "2Dnr", "1Dnr", "1/4Dnr", "1/2gmSy"};

        // Looping the headerPGtype and ratePGtype to create HashMap data type to pair both together
        HashMap<String, Float[]> tablePG = new HashMap<String, Float[]>();
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

    private void sendPost(String url, String postParams) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        // conn.setRequestProperty("Host", "accounts.google.com");
        conn.setRequestProperty("Host", "powergold.biz");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        for (String cookie : this.cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "https://powergold.biz/default.asp");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // System.out.println(response.toString());

    }

    private String getPageContent(String memberpage) throws Exception {

        URL obj = new URL(memberpage);
        conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        String url2 = conn.getURL().toString();
        System.out.println("\nSending 'GET' request to URL : " + memberpage);
        System.out.println("Response Code : " + responseCode+"\n");
        //       System.out.println("URL2 : " + url2);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();

    }

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        // Element loginform = doc.getElementById("gaia_loginform");
        // PowerGold form id
        Element loginform = doc.getElementById("Table_01");

        // Get all the <input tag string
        Elements inputElements = loginform.getElementsByTag("input");

        // initialize paramList to store all the parameter
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            // if (key.equals("Email"))
            if (key.equals("username"))
                value = username;
                // else if (key.equals("Passwd"))
            else if (key.equals("password"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }

        return result.toString();
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

}
