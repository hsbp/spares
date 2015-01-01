package org.hsbp.spares.android;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SparesActivity extends Activity
{
    protected <T> void initSpinner(final int id, final T[] items) {
        final Spinner spinner = (Spinner)findViewById(id);
        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
