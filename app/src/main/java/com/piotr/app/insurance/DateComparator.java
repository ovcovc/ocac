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
public class DateComparator implements Comparator<JSONObject>
{

    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {

            Date left = dateFormat.parse(lhs.getString("expiration"));

            Date right = dateFormat.parse(rhs.getString("expiration"));

            return left.compareTo(right);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}