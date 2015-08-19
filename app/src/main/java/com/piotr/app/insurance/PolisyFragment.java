package com.piotr.app.insurance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Piotr on 2015-08-18.
 */
public class PolisyFragment extends Fragment {

    List<JSONObject> polisy = new ArrayList<JSONObject>();

    ListView listView;

    Button button;



    ArrayAdapter<JSONObject> adapter;

    public PolisyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.polisy_fragment, container, false);

        button = (Button)rootView.findViewById(R.id.add);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddPolicyActivity.class);

                startActivity(intent);

            }

        });

        listView = (ListView) rootView.findViewById(R.id.listView);


        adapter = new ArrayAdapter<JSONObject>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1, polisy) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);


                try {
                    text1.setText(polisy.get(position).get("title").toString());
                    text2.setText(polisy.get(position).get("expiration").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Toast.makeText(getActivity().getApplicationContext(), polisy.get(position).get("description").toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        return rootView;
    }


    public void refreshPolicies() {

        File dir = getActivity().getApplicationContext().getFilesDir();

        File[] subFiles = dir.listFiles();

        polisy.clear();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                if (file.getName().startsWith("polisa")) {

                    try {
                        polisy.add(new JSONObject(readFromFile(file.getName())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

        Collections.sort(polisy, new DateComparator());

        adapter.notifyDataSetChanged();

    }


    @Override
    public void onResume() {

        super.onResume();

        refreshPolicies();

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

    private void writeToFile(String data, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}