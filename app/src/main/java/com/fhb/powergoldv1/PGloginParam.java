package com.fhb.powergoldv1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PGloginParam extends Activity { // Stub error if add "extends Activity"
    EditText editTexUsername;
    EditText editTextPassword;
    TextView textViewWaitMsg;
    Integer webResponseCode;
    String pgLoginParam;
    private List<String> cookies;
    private HttpsURLConnection conn;
    DatabaseController pgdb;

    private final String USER_AGENT = "Mozilla/5.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_setup);

        textViewWaitMsg = (TextView)findViewById(R.id.textViewWaitPrompt);
        editTexUsername = (EditText)findViewById(R.id.editTextPGusername);
        editTextPassword = (EditText)findViewById(R.id.editTextPGpassword);
        textViewWaitMsg = (TextView)findViewById(R.id.textViewWaitPrompt);
        pgdb =  new DatabaseController(this);

    }

    public void onClickCheckLogin (View v) throws Exception {
        if (v.getId() == R.id.buttonLoginSetup) {
            textViewWaitMsg.setText("Wait .....");

            // Check access into PG membership web
            // user class with AsyncTask to avoid error main thread
            new PGwebTestAccess().execute();
        }
    }

    private class PGwebTestAccess extends AsyncTask<Void, Void, Void> {

        String textResult = "";
        Boolean isCorrectLogin = false;

        @Override
        protected Void doInBackground(Void... params) {
            URL url, urlLogon;
            try {
                url = new URL("https://powergold.biz/default.asp");

                // make sure cookies is turn on
                CookieHandler.setDefault(new CookieManager());

                String loginPage = getPageContent(url);

                // set Powergold logon parameter to append to URL (?xxxxxx&yyyyyyy)
                String postParams = getFormParams(loginPage);
                pgLoginParam = "?" + postParams;

                // Test access into PGWebsite
                urlLogon = new URL("https://powergold.biz/logon.asp" + pgLoginParam);
                String userPage = getPageContent(urlLogon);

                if (!userPage.contains("Invalid Username")) isCorrectLogin = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                textResult = e.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isCorrectLogin){
                // Create user table and preserv/insert the login and password.
                PGtables pgTables = new PGtables();
                pgdb.createAuthTable(); // create if not exist

                String username = editTexUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                String[] user_auth = {username, password, pgLoginParam};
                // table automatic will be created from onCreate method in DatabaseController class
                boolean isCreated = pgdb.insert_value(pgTables.authTableName, pgTables.authSchema, user_auth);
                // todo - to change from insert to update database is option change password is added.

                if (isCreated){
                    // call a method to open activity from non activity class.
                    OpenActivity openActivity = new OpenActivity();
                    openActivity.mainMenu(PGloginParam.this);
                } else {
                    textViewWaitMsg.setText("Error creating user table!!");
                }

            } else {
                editTexUsername.setText("");
                editTextPassword.setText("");
                textViewWaitMsg.setText("Invalid Login!!");
            }
        }
    }

    private String getPageContent(URL url) throws Exception {

        conn = (HttpsURLConnection) url.openConnection();

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

        webResponseCode = conn.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + webResponseCode + "\n");

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

    public String getFormParams(String html)
            throws UnsupportedEncodingException {

        // check login attempt into PG web.
        String username = editTexUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

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

    /**
     * This class is required to open activity from non activity class.
     */
    public class OpenActivity {

        public void mainMenu (Context context) {
            Intent i = new Intent(context, MainMenu.class);
            //intent.putExtra("sub1","chemistry");
            context.startActivity(i);
        }
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
}


