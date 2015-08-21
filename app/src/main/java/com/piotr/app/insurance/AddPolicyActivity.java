package com.piotr.app.insurance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Piotr on 2015-08-18.
 */
public class AddPolicyActivity extends Activity {

    Button save, cancel;

    EditText desc, title, date;

    private DatePickerDialog datePickerDialog;

    SimpleDateFormat dateFormatter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_policy_layout);

        save = (Button)findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        cancel = (Button)findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPolicyActivity.this.finish();
            }
        });

        setFields();

    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setFields() {

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        desc = (EditText)findViewById(R.id.desc);

        desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    if (desc.getText().toString().equalsIgnoreCase("wpisz opis")){

                        desc.setText("");

                    }

                }
                else {

                    if (desc.getText().toString().equalsIgnoreCase("")){

                        desc.setText("Wpisz opis");

                    }

                }

            }
        });

        title = (EditText)findViewById(R.id.title);

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    if (title.getText().toString().equalsIgnoreCase("wpisz nazwę")) {

                        title.setText("");

                    }

                } else {

                    if (title.getText().toString().equalsIgnoreCase("")) {

                        title.setText("Wpisz nazwę");

                    }

                }

            }
        });

        title.setInputType(InputType.TYPE_CLASS_TEXT);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);

        desc.setInputType(InputType.TYPE_CLASS_TEXT);

        date = (EditText)findViewById(R.id.policyDate);

        date.setInputType(InputType.TYPE_NULL);

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    hideKeyboard();

                }

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();

                newDate.set(year, monthOfYear, dayOfMonth);

                date.setText(dateFormatter.format(newDate.getTime()));

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    private void save() {

        date.clearFocus();

        title.clearFocus();

        desc.clearFocus();

        if(!date.getText().toString().equalsIgnoreCase("wybierz datę") && !desc.getText().toString().equalsIgnoreCase("wpisz opis") && !title.getText().toString().equalsIgnoreCase("wpisz tytuł")) {

            String filename = String.format("polisa_%s.txt", new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()));

            JSONObject json = new JSONObject();

            try {
                json.put("title", title.getText().toString());

                json.put("expiration", date.getText().toString());

                json.put("description", desc.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            writeToFile(json.toString(), filename);

            Toast.makeText(this, "Zapisano!", Toast.LENGTH_SHORT).show();

            AddPolicyActivity.this.finish();

        } else {

            Toast.makeText(this, "Wypełnij formularz by zapisać dane o polisie!", Toast.LENGTH_SHORT).show();

        }

    }

    private void writeToFile(String data, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
