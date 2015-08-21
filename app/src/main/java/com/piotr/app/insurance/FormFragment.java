package com.piotr.app.insurance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by Piotr on 2015-08-15.
 */


public class FormFragment extends Fragment {

    FragmentManager fm;

    SpinnerDialog dialog;

    ArrayList<String> policyPrices = new ArrayList<String>();

    private DatePickerDialog registerDatePickerDialog, startDatePickerDialog, licDatePickerDialog;

    EditText registerDate, pesel, startDate;

    TextView dob;

    SimpleDateFormat dateFormatter;

    DataAdapter mDbHelper;

    Button save, calculate;

    CodeDataAdapter mDbCodeHelper;

    Spinner companies, insurersOC, insurersAC;

    Spinner models;

    Spinner yearsSpinner;

    Spinner yearsOC, yearsAC, city, installments;

    EditText postalCode, value, licence;

    int previousInsurerOC = -1;

    int selectedYear = -1;

    String previousOCname = "";

    int previousInsurerAC = -1;

    String previousACname = "";

    HashMap<Integer, String> companyDict = new HashMap<Integer, String>();

    HashMap<Integer, String> modelsDict = new HashMap<Integer, String>();

    HashMap<Integer, String> codeDict = new HashMap<Integer, String>();

    HashMap<String, Integer> insurersDict = new HashMap<String, Integer>();

    ArrayList<Integer> years = new ArrayList<Integer>();

    int selectedCompany = -1;

    int selectedModel = -1;

    int selectedCity = -1;

    int selectedOCyears = -1;

    int selectedACyears = -1;

    int installmentsSelected = -1;

    Switch lastYearOC, lastYearAC, last3yearsOC, last3yearsAC, windshields, nnw, assistance;

    String nameToSave = "";

    boolean fromCache = false;

    JSONObject cacheJSON;


    public FormFragment() {

    }

    public void cacheForm() {

        fromCache = true;

        this.cacheJSON = getJSONvalues();

    }

    public void getFormFromCache() {

        try {

        JSONObject json = new JSONObject();

        json = this.cacheJSON;

        String c = null;

            c = companyDict.get(json.get("company"));



        this.selectedCompany = (int)json.get("company");

        this.companies.setSelection(((ArrayAdapter)companies.getAdapter()).getPosition(c));

        this.selectedYear = Integer.parseInt(json.get("production").toString());

        this.selectedModel = (int)json.get("model");

        this.registerDate.setText(json.get("registration_date").toString());

        this.startDate.setText(json.get("start_date").toString());

        this.value.setText(json.get("value").toString());

        this.pesel.setText(json.get("pesel").toString());

        this.handlePesel();

        this.postalCode.setText(json.get("postal_code").toString());

        this.selectedCity = (int)json.get("city");

        if (json.get("postal_code").toString().length() == 6) {

            setCities(postalCode.getText().toString());

        } else {

            setCities("");

        }

        this.selectedOCyears = (int)json.get("selectedOCyears");
        //this.yearsSpinner.setSelection(((ArrayAdapter)yearsSpinner.getAdapter()).getPosition(prod));
        this.yearsOC.setSelection(selectedOCyears);

        this.selectedACyears = (int)json.get("selectedACyears");

        this.yearsAC.setSelection(selectedACyears);

        this.previousOCname = json.get("previousOCname").toString();

        this.previousInsurerAC = (int)json.get("previousACID");

        this.previousInsurerOC = (int)json.get("previousOCID");

        this.previousACname = json.get("previousACname").toString();

        this.insurersOC.setSelection(((ArrayAdapter)insurersOC.getAdapter()).getPosition(json.get("previousOCname")));

        this.insurersAC.setSelection(((ArrayAdapter)insurersAC.getAdapter()).getPosition(json.get("previousACname")));

        this.lastYearOC.setChecked((boolean)json.get("lastYearOC"));

        this.lastYearAC.setChecked((boolean)json.get("lastYearAC"));

        this.last3yearsOC.setChecked((boolean)json.get("last3yearsOC"));

        this.last3yearsAC.setChecked((boolean)json.get("last3yearsAC"));

        this.nnw.setChecked((boolean)json.get("nnw"));

        this.windshields.setChecked((boolean)json.get("szyby"));

        this.assistance.setChecked((boolean)json.get("assistance"));

        this.installmentsSelected = (int)json.get("installments");

        this.installments.setSelection(((ArrayAdapter)installments.getAdapter()).getPosition(this.installmentsSelected));

            fromCache = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fromCache = false;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public JSONObject getJSONvalues() {

        JSONObject json = new JSONObject();

        try {
            json.put("company", selectedCompany);

            json.put("production", yearsSpinner.getSelectedItem().toString());

            json.put("model", selectedModel);

            json.put("registration_date", registerDate.getText());

            json.put("value", value.getText().toString());

            json.put("pesel", pesel.getText().toString());

            json.put("dob", dob.getText().toString());

            json.put("postal_code", postalCode.getText().toString());

            json.put("start_date", startDate.getText().toString());

            json.put("licence", licence.getText().toString());

            json.put("city", selectedCity);

            json.put("selectedOCyears", selectedOCyears);

            json.put("selectedACyears", selectedACyears);

            json.put("previousOCID", previousInsurerOC);

            json.put("previousOCname", previousOCname);

            json.put("previousACname", previousACname);

            json.put("previousACID", previousInsurerAC);

            json.put("installments", installmentsSelected);

            json.put("lastYearOC", lastYearOC.isChecked());

            json.put("lastYearAC", lastYearAC.isChecked());

            json.put("last3yearsAC", last3yearsAC.isChecked());

            json.put("last3yearsOC", last3yearsOC.isChecked());

            json.put("nnw", nnw.isChecked());

            json.put("szyby", windshields.isChecked());

            json.put("assistance", assistance.isChecked());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    public void saveState() {

        //if (nameToSave.equalsIgnoreCase("")) {

        nameToSave = String.format("%s.txt", new SimpleDateFormat("yyyy-MM-dd'T'HH-mm").format(new Date()));

        //}

        writeToFile(getJSONvalues().toString(), nameToSave);

    }

    public void restoreState(String name) {

        String jsonString = readFromFile(name);

        try {
            JSONObject json = new JSONObject(jsonString);

            String c = companyDict.get(json.get("company"));

            this.selectedCompany = (int)json.get("company");

            this.companies.setSelection(((ArrayAdapter)companies.getAdapter()).getPosition(c));

            this.selectedYear = Integer.parseInt(json.get("production").toString());

            this.selectedModel = (int)json.get("model");

            this.registerDate.setText(json.get("registration_date").toString());

            this.startDate.setText(json.get("start_date").toString());

            this.licence.setText(json.get("licence").toString());

            this.value.setText(json.get("value").toString());

            this.pesel.setText(json.get("pesel").toString());

            this.handlePesel();

            this.postalCode.setText(json.get("postal_code").toString());

            this.selectedCity = (int)json.get("city");

            if (json.get("postal_code").toString().length() == 6) {

                setCities(postalCode.getText().toString());

            } else {

                setCities("");

            }

            this.selectedOCyears = (int)json.get("selectedOCyears");
            //this.yearsSpinner.setSelection(((ArrayAdapter)yearsSpinner.getAdapter()).getPosition(prod));
            this.yearsOC.setSelection(selectedOCyears);

            this.selectedACyears = (int)json.get("selectedACyears");

            this.yearsAC.setSelection(selectedACyears);

            this.previousOCname = json.get("previousOCname").toString();

            this.previousInsurerAC = (int)json.get("previousACID");

            this.previousInsurerOC = (int)json.get("previousOCID");

            this.previousACname = json.get("previousACname").toString();

            this.insurersOC.setSelection(((ArrayAdapter)insurersOC.getAdapter()).getPosition(json.get("previousOCname")));

            this.insurersAC.setSelection(((ArrayAdapter)insurersAC.getAdapter()).getPosition(json.get("previousACname")));

            this.lastYearOC.setChecked((boolean)json.get("lastYearOC"));

            this.lastYearAC.setChecked((boolean)json.get("lastYearAC"));

            this.last3yearsOC.setChecked((boolean)json.get("last3yearsOC"));

            this.last3yearsAC.setChecked((boolean)json.get("last3yearsAC"));

            this.nnw.setChecked((boolean)json.get("nnw"));

            this.windshields.setChecked((boolean)json.get("szyby"));

            this.assistance.setChecked((boolean)json.get("assistance"));

            this.installmentsSelected = (int)json.get("installments");

            this.installments.setSelection(((ArrayAdapter)installments.getAdapter()).getPosition(this.installmentsSelected));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calculator_fragment, container, false);

        String myString = "[ { \"brak polisy\":0 }, { \"Allianz\":1 }, { \"AXA Direct\":2 }, " +
                "{ \"Aviva\":3 }, { \"Benefia\":4 }, { \"mBank\":5 }, { \"Compensa\":6 }, " +
                "{ \"InterRisk\":11 }, { \"LibertyDirect\":12 }, { \"Link4\":13 }, { \"MTU\":14 }, " +
                "{ \"PTU\":15 }, { \"Polski Związek Motorowy\":16 }, { \"PZU\":17 }, { \"TUW\":18 }, " +
                "{ \"TUZ\":19 }, { \"UNIQA\":20 }, { \"Warta\":21 }, { \"Proama\":22 }, " +
                "{ \"TUW Pocztowy\":23 }, { \"Gothaer\":24 }, { \"Inny\":99 }, { \"Allianz Direct\":25 }, " +
                "{ \"Concordia\":7 }, { \"Ergo Hestia\":8 }, { \"Generali\":9 }, { \"HDI\":10 } ]";

        fm = getActivity().getSupportFragmentManager();

        dialog = new SpinnerDialog();

        this.value = (EditText)rootView.findViewById(R.id.value);

        this.save = (Button)rootView.findViewById(R.id.save);

        this.last3yearsAC = (Switch)rootView.findViewById(R.id.last3yearsAC);

        this.last3yearsOC = (Switch)rootView.findViewById(R.id.last3yearsOC);

        this.lastYearAC = (Switch)rootView.findViewById(R.id.lastYearAC);

        this.lastYearOC = (Switch)rootView.findViewById(R.id.lastYearOC);

        this.windshields = (Switch)rootView.findViewById(R.id.szyby);

        this.nnw = (Switch)rootView.findViewById(R.id.nnw);

        this.assistance = (Switch)rootView.findViewById(R.id.ass);

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveState();

            }
        });

        this.installments = (Spinner)rootView.findViewById(R.id.raty);

        //final int[] raty = {1,2,4};

        final List<Integer> raty = new ArrayList<Integer>();

        raty.add(1);

        raty.add(2);

        raty.add(4);

        final ArrayAdapter<Integer> installmentsAdapter = new ArrayAdapter<Integer>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, raty);

        installmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.installments.setAdapter(installmentsAdapter);

        this.installments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                installmentsSelected = raty.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        this.calculate = (Button)rootView.findViewById(R.id.calculate);

        this.calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    postDataToService();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        this.insurersDict = convertToHashMap(myString);

        this.insurersOC = (Spinner)rootView.findViewById(R.id.insurerOC);

        this.insurersAC = (Spinner)rootView.findViewById(R.id.insurerAC);

        this.pesel = (EditText)rootView.findViewById(R.id.pesel);

        this.dob = (TextView)rootView.findViewById(R.id.dob);

        this.models = (Spinner)rootView.findViewById(R.id.model);

        this.companies = (Spinner)rootView.findViewById(R.id.company);

        this.yearsSpinner = (Spinner)rootView.findViewById(R.id.production);

        this.registerDate = (EditText)rootView.findViewById(R.id.registerDate);

        this.registerDate.setInputType(InputType.TYPE_NULL);

        this.registerDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    hideKeyboard();

                }

            }
        });




        this.licence = (EditText)rootView.findViewById(R.id.licence);

        this.licence.setInputType(InputType.TYPE_NULL);

        this.licence.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    hideKeyboard();

                }

            }
        });

        this.licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                licDatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        licDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date date = newDate.getTime();

                if (new Date().after(date)) {

                    licence.setText(dateFormatter.format(newDate.getTime()));

                } else {

                    Toast.makeText(getActivity().getApplicationContext(), "Data musi być w przeszłości", Toast.LENGTH_SHORT).show();

                }

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        this.startDate = (EditText)rootView.findViewById(R.id.startDate);

        this.startDate.setInputType(InputType.TYPE_NULL);

        this.setStartDateField();

        this.city = (Spinner)rootView.findViewById(R.id.city);

        this.postalCode = (EditText)rootView.findViewById(R.id.postalCode);

        this.yearsOC = (Spinner)rootView.findViewById(R.id.yearsOC);

        this.yearsAC = (Spinner)rootView.findViewById(R.id.yearsAC);

        List<String> insurersList = new ArrayList<String>(insurersDict.keySet());

        final ArrayAdapter<String> insurersAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, insurersList);

        insurersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.insurersOC.setAdapter(insurersAdapter);

        this.insurersAC.setAdapter(insurersAdapter);

        this.insurersOC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                previousInsurerOC = insurersDict.get(insurersOC.getSelectedItem().toString());

                previousOCname = insurersOC.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                previousInsurerOC = -1;

                previousOCname = "";

            }
        });

        this.insurersAC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                previousInsurerAC = insurersDict.get(insurersAC.getSelectedItem().toString());

                previousACname = insurersAC.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                previousInsurerAC = -1;

                previousACname = "";

            }
        });

        this.mDbCodeHelper = new CodeDataAdapter(this.getActivity().getApplicationContext());

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);



        this.pesel.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void afterTextChanged(Editable s) {

                handlePesel();

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = pesel.getText().toString();
                len = str.length();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        });




        String[] y = {"0","1","2","3","4","5","6","7","Więcej"};

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, y);

        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.yearsOC.setAdapter(yearsAdapter);

        this.yearsOC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedOCyears = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.yearsAC.setAdapter(yearsAdapter);



        this.yearsAC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedACyears = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        this.postalCode.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void afterTextChanged(Editable s) {
                String str = postalCode.getText().toString();
                if (str.length() == 2 && len < str.length()) {//len check for backspace

                    str = str + "-";

                    postalCode.setText(str);

                    postalCode.setSelection(postalCode.getText().length());

                }

                if (str.length() == 6) {

                    setCities(postalCode.getText().toString());

                } else {

                    setCities("");

                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = postalCode.getText().toString();
                len = str.length();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        });

        List<String> list = new ArrayList<String>(companyDict.values());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        companies.setAdapter(adapter);

        companies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String comp = companies.getSelectedItem().toString();

                selectedCompany = getKeyByValue(companyDict, comp);

                //getModelsById(selectedCompany);

                getYearsById(selectedCompany);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                getModelsById(-1, 0);

            }
        });



        return rootView;

    }

    @Override
    public void onResume(){

        super.onResume();

        MainActivity activity = (MainActivity)getActivity();

        if (!activity.getFileToRestore().equalsIgnoreCase("")) {

            nameToSave = activity.getFileToRestore();

            activity.setFileToRestore("");

            restoreState(nameToSave);

        } else if (this.fromCache) {

            getFormFromCache();

        }

    }

    @Override
    public void onPause(){

        super.onPause();

        try {

            this.cacheForm();

        } catch (Exception e) {


        }

    }

    public void getModelsById(int id, int year) {

        this.modelsDict.clear();

        if (id != -1) {

            mDbHelper.open();

            Cursor c = mDbHelper.getModelsForCompany(id, year);

            if (c.moveToFirst()) {

                while (!c.isAfterLast()) {

                    this.modelsDict.put(c.getInt(1), c.getString(2));

                    c.moveToNext();

                }

            }

            mDbHelper.close();

        }

        List<String> list = new ArrayList<String>(modelsDict.values());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, list);

        models.setAdapter(adapter);

        models.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedModel = getKeyByValue(modelsDict, models.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        if (selectedModel != -1) {

            String c = modelsDict.get(selectedModel);

            this.models.setSelection(adapter.getPosition(c));
        }

        setDateTimeField();

    }


    public void getYearsById(int id) {

        this.years.clear();

        if (id != -1) {

            mDbHelper.open();

            Cursor min = mDbHelper.getMinYearOfProduction(id);

            Cursor max = mDbHelper.getMaxYearOfProduction(id);

            if (min.moveToFirst() && max.moveToFirst()) {

                int counter = min.getInt(0);

                int maxYear = max.getInt(0);

                if (counter > maxYear) {

                    maxYear = Calendar.getInstance().get(Calendar.YEAR);

                }

                do {

                    this.years.add(counter);

                    counter++;

                } while (counter <= maxYear);

            }

            mDbHelper.close();

        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, years);

        yearsSpinner.setAdapter(adapter);

        yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int selectedYear = years.get(position);



                getModelsById(selectedCompany, selectedYear);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        if (selectedYear != 1) {

            this.yearsSpinner.setSelection(adapter.getPosition(selectedYear));

        }
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        this.mDbHelper = new DataAdapter(this.getActivity().getApplicationContext());

        mDbHelper.createDatabase();

        mDbHelper.open();

        Cursor c = mDbHelper.getAllCompanies();

        this.companyDict.clear();

        if (c.moveToFirst()) {

            while (!c.isAfterLast()) {

                this.companyDict.put(c.getInt(0), c.getString(1));

                c.moveToNext();

            }

        }

        mDbHelper.close();

    }

    //Register date

    private void setCities(String code) {

        mDbCodeHelper.createDatabase();

        mDbCodeHelper.open();

        codeDict.clear();

        Cursor c = mDbCodeHelper.getCities(postalCode.getText().toString());

        if (!code.equalsIgnoreCase("")) {

            if (c.moveToFirst()) {

                while (!c.isAfterLast()) {

                    codeDict.put(c.getInt(0), c.getString(4));

                    c.moveToNext();

                }

            }

            mDbCodeHelper.close();

        }

        List<String> codeList = new ArrayList<String>(codeDict.values());

        final ArrayAdapter<String> codeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, codeList);

        codeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.city.setAdapter(codeAdapter);

        this.city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCity = getKeyByValue(codeDict, city.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (selectedCity != -1) {

            this.city.setSelection(codeAdapter.getPosition(codeDict.get(selectedCity)));

        }

    }

    private void setDateTimeField() {
        registerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerDatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        registerDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                Date date = newDate.getTime();

                if (!new Date().before(date)) {

                    registerDate.setText(dateFormatter.format(newDate.getTime()));

                } else {

                    Toast.makeText(getActivity().getApplicationContext(), "Data musi być w przeszłości", Toast.LENGTH_SHORT).show();

                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void setStartDateField() {
        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    hideKeyboard();

                }

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date date = newDate.getTime();

                if (new Date().before(date)) {

                    startDate.setText(dateFormatter.format(newDate.getTime()));

                } else {

                    Toast.makeText(getActivity().getApplicationContext(), "Data musi być w przyszłości", Toast.LENGTH_SHORT).show();

                }

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    public HashMap<String, Integer> convertToHashMap(String jsonString) {
        HashMap<String, Integer> myHashMap = new HashMap<String, Integer>();
        try {
            JSONArray jArray = new JSONArray(jsonString);
            JSONObject jObject = null;
            String keyString=null;
            for (int i = 0; i < jArray.length(); i++) {
                jObject = jArray.getJSONObject(i);
                // beacuse you have only one key-value pair in each object so I have used index 0
                keyString = (String)jObject.names().get(0);
                myHashMap.put(keyString, jObject.getInt(keyString));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myHashMap;
    }


    private String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("trolollo", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("trolollo", "Error closing asset " + name);
                }
            }
        }

        return null;
    }

    public boolean validated() {

        int ie_id = -1;

        int capacity = -1;

        int year_of_prod = -1;

        int lastYearDamageOC = 0;

        int lastYearDamageAC = 0;

        int last2YearsDamageAC = 0;

        int last2YearsDamageOC = 0;

        int insuranceValue = 0;

        if (startDate.getText().toString().equalsIgnoreCase("") || startDate.getText().toString().equalsIgnoreCase("wybierz datę")) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawną wartość rozpoczęcia ochrony!", Toast.LENGTH_SHORT).show();

            return false;

        }

        if (registerDate.getText().toString().equalsIgnoreCase("") || registerDate.getText().toString().equalsIgnoreCase("wybierz datę")) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawną wartość rejestracji!", Toast.LENGTH_SHORT).show();

            return false;

        }

        if (postalCode.getText().toString().equalsIgnoreCase("")) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawną wartość kodu!", Toast.LENGTH_SHORT).show();

            return false;

        }

        String postal = postalCode.getText().toString();



        try {

            insuranceValue = Integer.parseInt(value.getText().toString());

        } catch (Exception e) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawną wartość ubezpieczenia!", Toast.LENGTH_SHORT).show();

            return false;

        }

        String peselString = pesel.getText().toString();

        if (pesel.length() != 11) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawny PESEL!", Toast.LENGTH_SHORT).show();

            return false;

        }



        if (lastYearOC.isChecked()) {

            lastYearDamageOC = 1;

        }

        if (lastYearAC.isChecked()) {

            lastYearDamageAC = 1;

        }

        if (last3yearsAC.isChecked()) {

            last2YearsDamageAC = 1;

        }

        if (last3yearsOC.isChecked()) {

            last2YearsDamageOC = 1;

        }

        Date lic, birth, from;



        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (installmentsSelected == -1) {

            installmentsSelected = 1;

        }

        try {

            birth = dateFormat.parse(dob.getText().toString());

        } catch (ParseException e) {

            Toast.makeText(getActivity().getApplicationContext(), "Zła data urodzenia!", Toast.LENGTH_SHORT).show();

            return false;

        }

        try {

            from = dateFormat.parse(startDate.getText().toString());

        } catch (ParseException e) {

            Toast.makeText(getActivity().getApplicationContext(), "Zła data rozpoczęcia ubezpieczenia!", Toast.LENGTH_SHORT).show();

            return false;

        }

        try {

            lic = dateFormat.parse(licence.getText().toString());

        } catch (ParseException e) {

            Toast.makeText(getActivity().getApplicationContext(), "Zła data otrzymania prawa jazdy!", Toast.LENGTH_SHORT).show();

            return false;

        }

        if (selectedModel == -1) {

            Toast.makeText(getActivity().getApplicationContext(), "Wybierz model pojazdu", Toast.LENGTH_SHORT).show();

            return false;

        }

        if (models.getSelectedItem() == null) {

            Toast.makeText(getActivity().getApplicationContext(), "Wybierz model pojazdu", Toast.LENGTH_SHORT).show();

            return false;

        }

        if (value.getText().toString().equals("0") || value.getText().toString().equals("")) {

            Toast.makeText(getActivity().getApplicationContext(), "Wpisz kwotę wartości pojazdu!", Toast.LENGTH_SHORT).show();

            return false;

        }

        return true;

    }

    public void postDataToService() throws IOException {

        if (validated()) {

            dialog.show(fm, "some_tag");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new RetrieveCalculationsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "lolo");
            else
                new RetrieveCalculationsTask().execute();

        }
        //response.put("HTTPStatus",httpResponse.getStatusLine().toString());

    }

    private void writeToFile(String data, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Toast.makeText(getActivity().getApplicationContext(), "Zapisano!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), "Błąd zapisu!", Toast.LENGTH_SHORT).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String name) {

        String ret = "";

        try {
            InputStream inputStream = getActivity().getApplicationContext().openFileInput(name);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void handlePesel() {

        String str = pesel.getText().toString();
        if (str.length() >= 6) {//len check for backspace

            str = str.substring(0,6);
            DateFormat format = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String dobString = df.format(date);

            dob.setText(dobString);

        } else {

            dob.setText("Uzupełnij PESEL");

        }

    }


    public class RetrieveCalculationsTask extends AsyncTask<String, Void, ArrayList<String>> {

        private Exception exception;



        @Override
        protected ArrayList<String> doInBackground(String... urls) {

            ArrayList<String> prices = new ArrayList<String>();

            try {

                int ie_id = -1;

                int capacity = -1;

                int year_of_prod = -1;

                int lastYearDamageOC = 0;

                int lastYearDamageAC = 0;

                int last2YearsDamageAC = 0;

                int last2YearsDamageOC = 0;

                int insuranceValue = 0;

                String postal = postalCode.getText().toString();

                try {

                    insuranceValue = Integer.parseInt(value.getText().toString());

                } catch (Exception e) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawną wartość ubezpieczenia!", Toast.LENGTH_SHORT).show();

                    return prices;

                }

                String peselString = pesel.getText().toString();

                if (pesel.length() != 11) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Wpisz poprawny PESEL!", Toast.LENGTH_SHORT).show();

                    return prices;

                }


                if (lastYearOC.isChecked()) {

                    lastYearDamageOC = 1;

                }

                if (lastYearAC.isChecked()) {

                    lastYearDamageAC = 1;

                }

                if (last3yearsAC.isChecked()) {

                    last2YearsDamageAC = 1;

                }

                if (last3yearsOC.isChecked()) {

                    last2YearsDamageOC = 1;

                }

                Date lic, birth, from;


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                if (installmentsSelected == -1) {

                    installmentsSelected = 1;

                }

                try {

                    birth = dateFormat.parse(dob.getText().toString());

                } catch (ParseException e) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Zła data urodzenia!", Toast.LENGTH_SHORT).show();

                    return prices;

                }

                try {

                    from = dateFormat.parse(startDate.getText().toString());

                } catch (ParseException e) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Zła data rozpoczęcia ubezpieczenia!", Toast.LENGTH_SHORT).show();

                    return prices;

                }

                try {

                    lic = dateFormat.parse(licence.getText().toString());

                } catch (ParseException e) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Zła data otrzymania prawa jazdy!", Toast.LENGTH_SHORT).show();

                    return prices;

                }

                if (selectedModel == -1) {

                    //Toast.makeText(getActivity().getApplicationContext(), "Wybierz model pojazdu", Toast.LENGTH_SHORT).show();

                    return prices;

                }

                mDbHelper.createDatabase();

                mDbHelper.open();

                Cursor c = mDbHelper.getModelWithId(selectedModel);

                if (c.moveToFirst()) {


                    year_of_prod = (int) yearsSpinner.getSelectedItem();

                    ie_id = c.getInt(3);

                    capacity = c.getInt(4);


                }

                mDbCodeHelper.close();

                String xml = loadAssetTextAsString(getActivity().getApplicationContext(), "post.txt");
                //String xml = loadAssetTextAsString(getActivity().getApplicationContext(), "example.txt");
                String formattedXML = String.format(xml, peselString, dob.getText().toString(), postal, previousInsurerOC, lastYearDamageOC, previousInsurerAC, lastYearDamageAC, licence.getText().toString(), last2YearsDamageAC, licence.getText().toString(), registerDate.getText().toString(), ie_id, year_of_prod, capacity, startDate.getText(), insuranceValue, installmentsSelected, installmentsSelected, installmentsSelected, installmentsSelected, installmentsSelected, installmentsSelected);
                int szyby = windshields.isChecked()? 1 : 0;
                formattedXML = formattedXML.replace("SZYBY", String.format("%d", szyby)).replace("HAJS", String.format("%s",insuranceValue)).replace("SZKODY_OC_ROK", String.format("%s",lastYearDamageOC)).replace("SZKODY_AC_ROK", String.format("%s",lastYearDamageAC));
                String env = String.format("<soapenv:Envelope xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" " +
                        "xmlns:xsd=\\\"http://www.w3.org/2001/XMLSchema\\\" " +
                        "xmlns:soapenv=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\" " +
                        "xmlns:myns=\\\"http://www.example.org/myns/\\\"><soapenv:Header/>" +
                        "<soapenv:Body><myns:getQuoteToProducts " +
                        "soapenv:encodingStyle=\\\"http://schemas.xmlsoap.org/soap/encoding/\\\">" +
                        "<xmlDocument xsi:type=\\\"xsd:string\\\"><![CDATA[%s]]></xmlDocument>" +
                        "</myns:getQuoteToProducts></soapenv:Body></soapenv:Envelope>", formattedXML);

                HttpPost httppost = new HttpPost("http://systemdlaagenta.pl/ws/QuoteServerXML.php");
                StringEntity se = null;
                try {
                    se = new StringEntity(env.replace("\\", ""), HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return prices;
                }

                se.setContentType("text/xml");
                httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
                httppost.setEntity(se);

                HttpClient httpclient = new DefaultHttpClient();
                BasicHttpResponse httpResponse = null;
                try {
                    httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
                } catch (IOException e) {
                    e.printStackTrace();
                    return prices;
                }

                HttpEntity responseEntity = httpResponse.getEntity();

                String calculationString = "";


                try {
                    String respString = EntityUtils.toString(responseEntity, HTTP.UTF_8);

                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();

                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(respString));

                    Document doc = db.parse(is);
                    doc.getDocumentElement().normalize();

                    org.w3c.dom.Element ee = doc.getDocumentElement();

                    NodeList nodeList = doc.getElementsByTagName("*");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            // do something with the current element
                            String n = node.getNodeName();
                            if (n.equals("SOAP-ENV:getQuoteToProductsResponse")) {

                                Node node1 = node.getFirstChild().getFirstChild();

                                calculationString = node1.getNodeValue();

                                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                                        .newInstance();
                                DocumentBuilder documentBuilder = documentBuilderFactory
                                        .newDocumentBuilder();
                                InputSource inputSource = new InputSource();
                                inputSource.setCharacterStream(new StringReader(calculationString));
                                Document document = documentBuilder.parse(inputSource);


                                NodeList nl = document.getFirstChild().getChildNodes();

                                for (int x = 0; x < nl.getLength(); x++) {

                                    Node n2 = nl.item(x);

                                    String nodeVal = n2.getFirstChild().getFirstChild().getFirstChild().getNodeValue();

                                    if (!nodeVal.equals("0")) {

                                        prices.add(nodeVal);

                                    }

                                    System.out.println(nodeVal);
                                }

                            }
                            System.out.println(n);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            policyPrices = prices;

            return prices;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            dialog.dismiss();

            if (policyPrices.size() != 0) {

                int size = policyPrices.size();

                if (size < 10) {

                    int val = Integer.parseInt(policyPrices.get(0));

                    do {

                        int newval = (int)(val-0.13*val) + (int)(Math.random() * (((val+0.13*val) - (val-0.13*val)) + 1));

                        policyPrices.add(String.format("%d", newval));

                        size++;

                    } while (size <10);

                }


                Intent intent = new Intent(getActivity(), OffersActivity.class);

                intent.putStringArrayListExtra("offers", policyPrices);

                startActivity(intent);

            } else {

                Toast.makeText(getActivity(), "Brak rezultatów dla podanych danych", Toast.LENGTH_SHORT).show();

            }

        }
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}