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


public class AddReminder extends Activity {
    private long rowID; // id of contact being edited, if any

    // EditTexts for pet information
    private EditText naslov;
    private EditText datum;
    private EditText ura;
    private EditText opombe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);


        naslov = (EditText) findViewById(R.id.naslov);
        datum = (EditText) findViewById(R.id.datum);
        ura = (EditText) findViewById(R.id.ura);
        opombe = (EditText) findViewById(R.id.opombe);

        Bundle extras = getIntent().getExtras(); // get Bundle of extras

        // if there are extras, use them to populate the EditTexts
        if (extras != null) {
            rowID = extras.getLong("ID");
            naslov.setText(extras.getString("naslov"));
            datum.setText(extras.getString("datum"));
            ura.setText(extras.getString("ura"));
            opombe.setText(extras.getString("opombe"));
        } // end if

        // set event listener for the Save Contact Button
        Button saveContactButton = (Button) findViewById(R.id.addReminderShrani);
        saveContactButton.setOnClickListener(savePetButtonClicked);
    }

    // called when the activity is first created
    @Override
    protected void onResume() {
        super.onResume();

        // create new LoadContactTask and execute it
        new LoadContactTask().execute(rowID);
    } // end method onResume
    // performs database query outside GUI thread
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(AddReminder.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOneReminder(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int naslovIndex = result.getColumnIndex("naslov");
            int uraIndex = result.getColumnIndex("ura");
            int datumIndex = result.getColumnIndex("datum");
            int opombeIndex = result.getColumnIndex("opombe");

            // fill TextViews with the retrieved data
            naslov.setText(result.getString(naslovIndex));
            ura.setText(result.getString(uraIndex));
            datum.setText(result.getString(datumIndex));
            opombe.setText(result.getString(opombeIndex));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask

    View.OnClickListener savePetButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (naslov.getText().length() != 0) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddReminder.this);

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
            databaseConnector.insertReminders(naslov.getText().toString(), datum
                    .getText().toString(), ura.getText().toString(), opombe
                    .getText().toString());
        } // end if
        else {
            databaseConnector.updateReminders(rowID, naslov.getText().toString(), datum
                    .getText().toString(), ura.getText().toString(), opombe
                    .getText().toString());
        } // end else
    } // end class saveContact
}
