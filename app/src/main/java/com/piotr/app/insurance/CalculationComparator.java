package com.piotr.app.insurance;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Piotr on 2015-08-18.
 */
public class CalculationComparator implements Comparator<JSONObject>
{

    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

        try {

            String dateStringLeft = lhs.get("filename").toString().substring(0, lhs.get("filename").toString().length()-5);

            String dateStringRight = rhs.get("filename").toString().substring(0, rhs.get("filename").toString().length()-5);

            Date left = dateFormat.parse(dateStringLeft);

            Date right = dateFormat.parse(dateStringRight);

            return right.compareTo(left);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}