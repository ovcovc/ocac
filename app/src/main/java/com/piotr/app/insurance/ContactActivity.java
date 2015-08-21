package com.piotr.app.insurance;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Piotr on 2015-08-20.
 */
public class ContactActivity extends Activity {

    EditText name, contact;

    CheckBox agreement;

    Button send;


    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);

        setContentView(R.layout.contact_layout);

        name = (EditText)findViewById(R.id.name);

        contact = (EditText)findViewById(R.id.contact);

        agreement = (CheckBox)findViewById(R.id.agreement);

        send = (Button)findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {

                    Toast.makeText(ContactActivity.this, "Wpisz imię i nazwisko!", Toast.LENGTH_SHORT).show();

                    return;

                } if (contact.getText().toString().equals("")) {

                    Toast.makeText(ContactActivity.this, "Wpisz dane kontaktowe!", Toast.LENGTH_SHORT).show();

                    return;

                }if (!agreement.isChecked()) {

                    Toast.makeText(ContactActivity.this, "Musisz zgodzić się na przetwarzanie danych osobowych!", Toast.LENGTH_SHORT).show();

                    return;

                }
                Toast.makeText(ContactActivity.this, "Proszę czekać... wysyłam...", Toast.LENGTH_SHORT).show();

                new SendMail().execute();

            }
        });

    }



    private class SendMail extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            Mail m = new Mail("amabeemailer@gmail.com", "amabeemailerpass");

            String[] toArr = {"amabee@amabee.pl"};
            m.setTo(toArr);
            m.setFrom("amabeemailer@gmail.com");
            m.setBody(String.format("Prośba o kontakt od %s. Dane kontaktowe: %s", name.getText().toString(), contact.getText().toString()));
            m.setSubject("Prośba o kontakt w sprawie ubezpieczenia");

            try {

                if(m.send()) {
                    return true;
                } else {
                   return false;
                }
            } catch(Exception e) {
                //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean success) {

            if (success) {

                Toast.makeText(ContactActivity.this, "Pomyślnie wysłano!", Toast.LENGTH_LONG).show();

                ContactActivity.this.finish();

            } else {

                Toast.makeText(ContactActivity.this, "Błąd podczas wysyłania - spróbuj później", Toast.LENGTH_LONG).show();

            }

        }
    }

}
