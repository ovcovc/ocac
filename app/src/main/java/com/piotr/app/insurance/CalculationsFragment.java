package com.piotr.app.insurance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Piotr on 2015-08-15.
 */
public class CalculationsFragment extends Fragment {

    List<JSONObject> fileNames = new ArrayList<JSONObject>();

    ListView listView;

    public CalculationsFragment() {
    }

    public boolean isFinished(JSONObject json){

        try {
            return json.getBoolean("finished");
        } catch (JSONException e) {
            return false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calculations_fragment, container, false);

        File dir = getActivity().getApplicationContext().getFilesDir();
        File[] subFiles = dir.listFiles();

        fileNames.clear();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                if (!file.getName().startsWith("polisa")) {

                    try {

                        JSONObject json = new JSONObject(readFromFile(file.getName()));

                        json.put("filename", file.getName());

                        fileNames.add(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }

        Collections.sort(fileNames, new CalculationComparator());

        listView = (ListView) rootView.findViewById(R.id.listView);

        ArrayAdapter<JSONObject> adapter = new ArrayAdapter<JSONObject>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1, fileNames) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));


                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

                    String dateString = fileNames.get(position).get("filename").toString().substring(0, fileNames.get(position).get("filename").toString().length()-5);

                    Date d = dateFormat.parse(dateString);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd.MM.yyyy");

                    text1.setText(String.format("Kalkulacja z %s",sdf.format(d)));
                    if (isFinished(fileNames.get(position))) {
                        text2.setText("Przejdź do rezultatów");
                    } else {
                        text2.setText("Nieukończona");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isFinished(fileNames.get(position))) {

                    ArrayList<String> offers = new ArrayList<String>();

                    try {

                        offers = new ArrayList<String>(Arrays.asList(fileNames.get(position).get("offers").toString().split(",")));

                        Intent intent = new Intent(getActivity(), OffersActivity.class);

                        intent.putStringArrayListExtra("offers", offers);

                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    MainActivity activity = (MainActivity) getActivity();

                    String name = "";
                    try {
                        name = fileNames.get(position).getString("filename");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    activity.setFileToRestore(name);

                    activity.setTab(0);
                }

            }
        });


        return rootView;
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



}