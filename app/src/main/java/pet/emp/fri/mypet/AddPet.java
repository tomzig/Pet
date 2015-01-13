package pet.emp.fri.mypet;

import android.app.Activity;
import android.app.AlertDialog;
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
            rowID = extras.getLong("row_id");
            ime.setText(extras.getString("ime"));
            vrsta.setText(extras.getString("vrsta"));
            rojDan.setText(extras.getString("rojDan"));
            velikost.setText(extras.getString("velikost"));
            teza.setText(extras.getString("teza"));
            cip.setText(extras.getString("cip"));
            stevilka.setText(extras.getString("stevilka"));
            drugo.setText(extras.getString("drugo"));
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
