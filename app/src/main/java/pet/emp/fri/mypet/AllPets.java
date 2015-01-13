package pet.emp.fri.mypet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.EditText;
import android.view.View.OnClickListener;


public class AllPets extends ListActivity {
    public static final String ROW_ID = "row_id"; // Intent extra key
    private ListView contactListView; // the ListActivity's ListView
    private CursorAdapter contactAdapter; // adapter for ListView

    // called when the activity is first created
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactListView = getListView();
        contactListView.setOnItemClickListener(reminders);



        // map each contact's name to a TextView in the ListView layout
        String[] from = new String[]{"ime", "vrsta", "rojDan", "velikost", "teza", "cip", "stevilka", "drugo"};
        int[] to = new int[]{R.id.petTV, R.id.petVrsta, R.id.petRojDan, R.id.petVelikost, R.id.petTeza, R.id.petCip, R.id.petStevilka, R.id.petDrugo};

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // only for gingerbread and newer versions
            contactAdapter = new SimpleCursorAdapter(AllPets.this, R.layout.all_pets,
                    null, from, to, 0);
        } else {
            contactAdapter = new SimpleCursorAdapter(AllPets.this, R.layout.all_pets,
                    null, from, to);
        }

        setListAdapter(contactAdapter); // set contactView's adapter

    }

    AdapterView.OnItemClickListener reminders = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact = new Intent(AllPets.this, ViewPet.class);

            // pass the selected contact's row ID as an extra with the Intent
            viewContact.putExtra(ROW_ID, arg3);
            startActivity(viewContact); // start the ViewContact Activity

        }
    };
    @Override
    protected void onResume() {
        super.onResume(); // call super's onResume method

        // create new GetContactsTask and execute it
        new GetContactsTask().execute((Object[]) null);
    } // end method onResume

    @Override
    protected void onStop() {
        contactAdapter.changeCursor(null); // adapted now has no Cursor
        super.onStop();
    } // end method onStop

    // performs database query outside GUI thread
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(AllPets.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAllPets();
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            contactAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        } // end method onPostExecute
    } // end class GetContactsTask
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_pets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // create a new Intent to launch the AddEditContact Activity
        Intent addNewPet = new Intent(AllPets.this, AddPet.class);
        startActivity(addNewPet); // start the AddEditContact Activity
        return super.onOptionsItemSelected(item); // call super's method
    }
}
