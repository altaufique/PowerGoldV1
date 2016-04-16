package com.fhb.powergoldv1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PGloginParam extends AppCompatActivity { // Stub error if add "extends Activity"
    EditText editTexUsername;
    EditText editTextPassword;
    EditText editTextOldPassword;
    EditText editTextNewPassword;
    TextView textViewWaitMsg;

    Integer webResponseCode;
    String pgLoginParam;
    private Document memberPageContent;
    private Boolean changePassword;
    String username; // decalared publicly because to use for both new login and edit password
    List<String> oldLoginPassword;
    private List<String> cookies;
    private HttpsURLConnection conn;
    DatabaseController pgdb;

    private final String USER_AGENT = "Mozilla/5.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changePassword=false;

        Bundle bundle = getIntent().getExtras(); // get extra value from caller class
        if (bundle != null) {
            changePassword = bundle.getBoolean("changePassword");
        }

        if (!changePassword) { // first time login, login layout
            setContentView(R.layout.login_setup);
            editTexUsername = (EditText)findViewById(R.id.editTextPGusername);
            editTextPassword = (EditText)findViewById(R.id.editTextPGpassword);
            textViewWaitMsg = (TextView)findViewById(R.id.textViewWaitPrompt);
        } else { // change password update loyout
            //Toast.makeText(this, "inside PGLoginParam - " + changePassword, Toast.LENGTH_SHORT).show();
            setContentView(R.layout.login_update);
            editTextOldPassword = (EditText)findViewById(R.id.editTextOldpassword);
            editTextNewPassword = (EditText)findViewById(R.id.editTextNewPassword);
            textViewWaitMsg = (TextView)findViewById(R.id.textViewWaitPrompt);
        }
        pgdb =  new DatabaseController(this);
    }

    public void onClickLoginPassword(View v) throws Exception {
        Boolean isUsernameOK = true;
        if (v.getId() == R.id.buttonLoginSetup) {
            textViewWaitMsg.setText("Wait!! .....");
        } if (v.getId() == R.id.buttonUpdatePassword) {
            //oldLoginPassword = new ArrayList<>();
            oldLoginPassword = this.getLoginPassword();
            if (oldLoginPassword.get(1).matches(editTextOldPassword.getText().toString())) {
                // get post param for login and new password
                textViewWaitMsg.setText("Wait!! Checking new password..");
                username = oldLoginPassword.get(0); // defined as public to access in getFormParam
                // assign the right variables for checking and updating database

            } else {
                isUsernameOK = false;
                Toast.makeText(this, "Error!! Wrong Old Password..", Toast.LENGTH_LONG).show();
                textViewWaitMsg.setText("");
                //editTextNewPassword.setText("");
                editTextOldPassword.setText("");
                editTextOldPassword.requestFocus();
            }
        }
        // Check access into PG membership web
        // user class with AsyncTask to avoid error main thread
        if (isUsernameOK) new PGwebTestAccess().execute();
    }

    public List<String> getLoginPassword() {
        List<List<String>> raw_arr_result = pgdb.queryAuth();

        List<String> auth_lst = new ArrayList<>();
        // iterate
        for (List<String> innerList : raw_arr_result) {
            auth_lst = innerList;
        }
        return auth_lst;
    }

    private class PGwebTestAccess extends AsyncTask<Void, Void, Void> {

        String textResult = "";
        Boolean isCorrectLogin = false;

        @Override
        protected Void doInBackground(Void... params) {
            URL url, urlLogon;
            String startMemberInfoStringTable = "Username:";
            String endMemberInfoStringTable = "Total RP I:";

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
                memberPageContent = Jsoup.connect(urlLogon.toString()).timeout(20000).get();

                if (!memberPageContent.toString().contains("Invalid Username")) isCorrectLogin = true;

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
                if (!changePassword) { // first time login
                    // Create user table and preserv/insert the login and password.
                    PGtables pgTables = new PGtables();
                    pgdb.createAuthTable(); // create if not exist

                    String username = editTexUsername.getText().toString();
                    String password = editTextPassword.getText().toString();

                    // process memberPageContent to add user details into the auth database
                    WebTableHelper wth = new WebTableHelper();

                    String beginString = "Username:";
                    String endString = "Total Pairing:";
                    wth.setWebTable(memberPageContent, beginString, endString);
                    String[] webTable = wth.getWebTable(); // element 3, 11 and 13 are Name, Date ragistered and Package
                    String[] user_auth = {username, password, pgLoginParam, webTable[3],webTable[11],webTable[13]};

                    // table automatic will be created from onCreate method in DatabaseController class
                    // Only one record is allowed for auth table. Delete any existing record before insert new.
                    boolean isDeleted = pgdb.deleteAllAuthRec();
                    boolean isCreated = pgdb.insert_value(pgTables.authTableName, pgTables.authSchema, user_auth);

                    if (isCreated) {
                        // call a method to open activity from non activity class.
                        OpenActivity openActivity = new OpenActivity();
                        openActivity.mainMenu(PGloginParam.this);
                    } else {
                        textViewWaitMsg.setText("Error creating user table!!");
                    }
                } else { // change password
                    // Update database with new password and new form parameters.
                    String newPassword = editTextNewPassword.getText().toString();
                    //Log.d("FHB", username + ", " + newPassword + ", " + pgLoginParam);

                    Boolean isUpdated = pgdb.updateAuth(newPassword, pgLoginParam);
                    if (isUpdated) {
                        OpenActivity openActivity = new OpenActivity();
                        openActivity.mainMenu(PGloginParam.this);
                    } else {
                        textViewWaitMsg.setText("Error!!.. Unable to update.");
                    }
                }

            } else {
                if (!changePassword) {
                    editTexUsername.setText("");
                    editTextPassword.setText("");
                    textViewWaitMsg.setText("Invalid Login!!");
                } else {
                    textViewWaitMsg.setText("Invalid PG Password!!");
                    editTextNewPassword.requestFocus();
                }
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
        // String username;
        String password;
        if (changePassword) {
            // username is public variable already assigned in change password block
            password = editTextNewPassword.getText().toString();
        } else {
            username = editTexUsername.getText().toString();
            password = editTextPassword.getText().toString();
        }
        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        Element loginform = doc.getElementById("Table_01");

        // Get all the <input tag string
        Elements inputElements = loginform.getElementsByTag("input");

        // initialize paramList to store all the parameter
        List<String> paramList = new ArrayList<>();
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


