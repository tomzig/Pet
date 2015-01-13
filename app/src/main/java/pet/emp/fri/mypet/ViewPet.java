package pet.emp.fri.mypet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ViewPet extends ListActivity {
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

        // map each contact's name to a TextView in the ListView layout
        String[] from = new String[]{"naslov", "datum", "ura", "opombe"};
        int[] to = new int[]{R.id.reminderNaslov, R.id.reminderDatum, R.id.reminderUra, R.id.reminderOpombe};

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // only for gingerbread and newer versions
            contactAdapter = new SimpleCursorAdapter(ViewPet.this, R.layout.view_pet,
                    null, from, to, 0);
        } else {
            contactAdapter = new SimpleCursorAdapter(ViewPet.this, R.layout.view_pet,
                    null, from, to);
        }

        setListAdapter(contactAdapter); // set contactView's adapter

    }
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
        DatabaseConnector databaseConnector = new DatabaseConnector(ViewPet.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAllReminders();
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
        getMenuInflater().inflate(R.menu.menu_view_pet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // create a new Intent to launch the AddEditContact Activity
        Intent addNewReminder = new Intent(ViewPet.this, AddReminder.class);
        startActivity(addNewReminder); // start the AddEditContact Activity
        return super.onOptionsItemSelected(item); // call super's method
    }
}
