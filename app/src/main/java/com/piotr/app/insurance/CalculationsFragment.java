package com.piotr.app.insurance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotr on 2015-08-15.
 */
public class CalculationsFragment extends Fragment {

    List<String> fileNames = new ArrayList<String>();

    ListView listView;

    public CalculationsFragment() {
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

                fileNames.add(file.getName());

            }
        }

        listView = (ListView) rootView.findViewById(R.id.listView);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                fileNames );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MainActivity activity = (MainActivity)getActivity();

                String name = fileNames.get(position);

                activity.setFileToRestore(name);

                activity.setTab(0);

            }
        });


        return rootView;
    }



}