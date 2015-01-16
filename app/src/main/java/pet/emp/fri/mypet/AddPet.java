package pet.emp.fri.mypet;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddPet extends Activity {
    private long rowID; // id of contact being edited, if any

    // EditTexts for pet information
    private EditText ime;
    private EditText vrsta;
    private EditText rojDan;
    private EditText velikost;
    private EditText teza;
    private EditText cip;
    private EditText stevilka;
    private EditText drugo;

    // called when the Activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pet);

        ime = (EditText) findViewById(R.id.ime);
        vrsta = (EditText) findViewById(R.id.vrsta);
        rojDan = (EditText) findViewById(R.id.rojDan);
        velikost = (EditText) findViewById(R.id.velikost);
        teza = (EditText) findViewById(R.id.teza);
        cip = (EditText) findViewById(R.id.cip);
        stevilka = (EditText) findViewById(R.id.stevilka);
        drugo = (EditText) findViewById(R.id.drugo);

        Bundle extras = getIntent().getExtras(); // get Bundle of extras

        // if there are extras, use them to populate the EditTexts
        if (extras != null) {
            rowID = extras.getLong("ID");
            ime.setText(extras.getString("ime"));
            vrsta.setText(extras.getString("vrsta"));
            rojDan.setText(extras.getString("rojDan"));
            velikost.setText(extras.getString("velikost"));
            teza.setText(extras.getString("teza"));
            cip.setText(extras.getString("cip"));
            stevilka.setText(extras.getString("stevilka"));
            drugo.setText(extras.getString("drugo"));

            new LoadContactTask().execute(rowID);
        } // end if

        // set event listener for the Save Contact Button
        Button saveContactButton = (Button) findViewById(R.id.AddPetSave);
        saveContactButton.setOnClickListener(savePetButtonClicked);
    }

    View.OnClickListener savePetButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ime.getText().length() != 0) {
                AsyncTask<Object, Object, Object> saveContactTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        savePet(); // save pet to the database
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result) {
                        finish(); // return to the previous Activity
                    } // end method onPostExecute
                }; // end AsyncTask

                // save the contact to the database using a separate thread
                saveContactTask.execute((Object[]) null);
            } // end if
            else {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPet.this);

                // set dialog title & message, and provide Button to dismiss
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show(); // display the Dialog
            } // end else
        } // end method onClick
    }; // end OnClickListener savePetButtonClicked

    // performs database query outside GUI thread
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(AddPet.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOnePet(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int imeIndex = result.getColumnIndex("ime");
            int vrstaIndex = result.getColumnIndex("vrsta");
            int rojDanIndex = result.getColumnIndex("rojDan");
            int velikostIndex = result.getColumnIndex("velikost");
            int tezaIndex = result.getColumnIndex("teza");
            int cipIndex = result.getColumnIndex("cip");
            int stevilkaIndex = result.getColumnIndex("stevilka");
            int drugoIndex = result.getColumnIndex("drugo");

            // fill TextViews with the retrieved data
            ime.setText(result.getString(imeIndex));
            vrsta.setText(result.getString(vrstaIndex));
            rojDan.setText(result.getString(rojDanIndex));
            velikost.setText(result.getString(velikostIndex));
            teza.setText(result.getString(tezaIndex));
            cip.setText(result.getString(cipIndex));
            stevilka.setText(result.getString(stevilkaIndex));
            drugo.setText(result.getString(drugoIndex));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask

    private void savePet() {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null) {
            // insert the contact information into the database
            databaseConnector.insertPets(ime.getText().toString(), vrsta
                    .getText().toString(), rojDan.getText().toString(), velikost
                    .getText().toString(), teza.getText().toString(), cip
                    .getText().toString(), stevilka.getText().toString(), drugo
                    .getText().toString());
        } // end if
        else {
            databaseConnector.updatePets(rowID, ime.getText().toString(), vrsta
                    .getText().toString(), rojDan.getText().toString(), velikost
                    .getText().toString(), teza.getText().toString(), cip
                    .getText().toString(), stevilka.getText().toString(), drugo
                    .getText().toString());
        } // end else
    } // end class saveContact
}
