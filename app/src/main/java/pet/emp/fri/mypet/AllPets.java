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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class AllPets extends ListActivity {
    public static long ROW_ID = -1; // Intent extra key
    private ListView contactListView; // the ListActivity's ListView
    private CursorAdapter contactAdapter; // adapter for ListView

    // called when the activity is first created
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactListView = getListView();
        //Button delete = (Button) findViewById(R.id.AllpetsDeleteButton);
        //delete.setOnClickListener(deletePetListener);

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

    AdapterView.OnItemClickListener deletePetListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ROW_ID = arg3;
            deletePet();

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
    private void deletePet() {
        // create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(AllPets.this);

        builder.setTitle(R.string.confirmTitle); // title bar string
        builder.setMessage(R.string.confirmMessage); // message to display

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                final DatabaseConnector databaseConnector = new DatabaseConnector(AllPets.this);

                // create an AsyncTask that deletes the contact in
                // another
                // thread, then calls finish after the deletion
                AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                    @Override
                    protected Object doInBackground(Long... params) {
                        databaseConnector.deletePet(params[0]);
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result) {
                        finish(); // return to the AddressBook Activity
                    } // end method onPostExecute
                }; // end new AsyncTask

                // execute the AsyncTask to delete contact at rowID
                deleteTask.execute(new Long[]{ROW_ID});
            } // end method onClick
        } // end anonymous inner class
        ); // end call to method setPositiveButton

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show(); // display the Dialog
    } // end method deleteContact
}
