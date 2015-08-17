package com.piotr.app.insurance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Piotr on 2015-08-15.
 */
public class CalculationsFragment extends Fragment {

    ArrayList<String> fileNames = new ArrayList<String>();

    public CalculationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calculations_fragment, container, false);

        File dir = getActivity().getApplicationContext().getFilesDir();
        File[] subFiles = dir.listFiles();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {

                fileNames.add(file.getName());

            }
        }

        return rootView;
    }



}