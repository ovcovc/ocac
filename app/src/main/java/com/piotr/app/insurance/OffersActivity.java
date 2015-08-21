package com.piotr.app.insurance;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Piotr on 2015-08-19.
 */
public class OffersActivity extends Activity {

    ArrayList<String> offers = new ArrayList<String>();

    ListView listView;

    Button save, exit, contact;


    public void saveOffers(){

        String data = "";

        for (int x = 0; x < offers.size(); x++) {

            data = data + offers.get(x) + ",";

        }

        data = data.substring(0,data.length()-2);

        String filename = String.format("%s.txt", new SimpleDateFormat("yyyy-MM-dd'T'HH-mm").format(new Date()));

        JSONObject json = new JSONObject();
        try {

            json.put("offers", data);

            json.put("finished", true);

            writeToFile(json.toString(), filename);

            Toast.makeText(this, "Zapisano!", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Toast.makeText(this, "Nastąpił błąd!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);

        offers = getIntent().getStringArrayListExtra("offers");

        setContentView(R.layout.offers_layout);

        save = (Button)findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOffers();
            }
        });

        exit = (Button)findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contact = (Button)findViewById(R.id.contact);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OffersActivity.this, ContactActivity.class);

                startActivity(intent);
            }
        });

        listView = (ListView)findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, offers) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
                text1.setText(String.format("%s PLN za ratę", offers.get(position)));
                text2.setText(String.format("Oferta %d", position+1));
                return view;
            }
        };

        listView.setAdapter(adapter);

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
