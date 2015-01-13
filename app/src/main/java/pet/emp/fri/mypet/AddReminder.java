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
            rowID = extras.getLong("row_id");
            naslov.setText(extras.getString("naslov"));
            datum.setText(extras.getString("datum"));
            ura.setText(extras.getString("ura"));
            opombe.setText(extras.getString("opombe"));
        } // end if

        // set event listener for the Save Contact Button
        Button saveContactButton = (Button) findViewById(R.id.addReminderShrani);
        saveContactButton.setOnClickListener(savePetButtonClicked);
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
