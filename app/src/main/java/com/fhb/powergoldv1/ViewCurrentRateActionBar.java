package com.fhb.powergoldv1;

import android.content.Intent;
import android.view.MenuItem;

/**
 * Created by FHB:Taufiq on 4/7/2016.
 */
public class ViewCurrentRateActionBar extends ActionBar {
    //ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // refresh the display to latest rate
        switch (item.getItemId()) {
            case R.id.rate_refresh:
                finish();
                Intent i = getIntent();
                i.putExtra("refresh", true);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

