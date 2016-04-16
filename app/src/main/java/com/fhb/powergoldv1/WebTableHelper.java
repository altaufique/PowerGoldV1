package com.fhb.powergoldv1;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by FHB:Taufiq on 4/16/2016.
 */
public class WebTableHelper {
    private String[] webTable;
    /**
     * Special design to extract values from web table
     * @param sane_doc -> Document type result of Jsoup parse
     * @param beginStr -> String to mark the starting location
     * @param endStr -> String to mark the end location
     * @return -> String array of proper arrange value
     */
    public String[] setWebTable(Document sane_doc, String beginStr, String endStr) { // First Priority Table 999.9 PowerGold

        Integer countElement = 0; // Header Row
        String elementTD;
        webTable = new String[150];
        Integer isFound = 0; //switch in the right element found

        Elements elements = sane_doc.body().select("*");

        for (Element element : elements) {
            Integer element_len = element.toString().length();

            if (!element.ownText().isEmpty()) {
                String str = element.ownText().toString().trim();
                // Start saving gold rate table -------------------------------------------
                // if (!str.matches("\\S") || str.matches("[0-9:.].*") ) { // non-white space character-\\S
                if (!str.matches("\\S")) { // non-white space character-\\S
                    // Extract rate table block
                    if (str.matches(beginStr)) {
                        countElement = 0; // Reset the counter if right table found
                        webTable[countElement] = str;
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
                            if (str == null || str.length() < 2) continue;

                            // saving clean array of rate
                            webTable[countElement] = str.replace(String.valueOf((char) 160), "").trim();
                        }
                    }
                } else {
                    //System.out.println(countElement + "-> " + str);  // uncommented this line to see line not captured.
                    continue; // to prevent increment of countElement for rejected value
                }
                countElement++;
            }
        }
        return webTable;
    }

    public String[] getWebTable () {
        return webTable;
    }
}
