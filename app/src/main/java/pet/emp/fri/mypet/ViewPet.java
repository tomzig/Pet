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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


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
        contactListView.setOnItemClickListener(editDelete);

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

    AdapterView.OnItemClickListener editDelete = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, final long arg3) {
//2nd Alert Dialog
            AlertDialog.Builder alertDialogBuilderSuccess = new AlertDialog.Builder(
                    ViewPet.this);
            alertDialogBuilderSuccess.setTitle(getString(R.string.deleteReminder));
            // set dialog message
            alertDialogBuilderSuccess
                    .setMessage(
                            getString(R.string.deleteMessage))
                    .setPositiveButton(getString(R.string.potrdi),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final DatabaseConnector databaseConnector = new DatabaseConnector(ViewPet.this);

                                    // create an AsyncTask that deletes the contact in
                                    // another
                                    // thread, then calls finish after the deletion
                                    AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                                        @Override
                                        protected Object doInBackground(Long... params) {
                                            databaseConnector.deleteReminder(params[0]);
                                            return null;
                                        } // end method doInBackground

                                        @Override
                                        protected void onPostExecute(Object result) {
                                            finish(); // return to the AddressBook Activity
                                        } // end method onPostExecute
                                    }; // end new AsyncTask

                                    // execute the AsyncTask to delete contact at rowID
                                    deleteTask.execute(new Long[]{arg3});
                                } // end method onClick
                            })
                    .setNegativeButton(R.string.preklici,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            final AlertDialog alertDialogSuccess = alertDialogBuilderSuccess.create();
            //////////////////////////////////
            //1st Alert
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewPet.this);
            alertDialogBuilder.setTitle(getString(R.string.viewPetTitle));
            // set dialog message
            alertDialogBuilder
                    .setMessage(getString(R.string.message1))
                    .setPositiveButton(getString(R.string.zbrisi),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    //calling the second alert when it user press the confirm button
                                    alertDialogSuccess.show();
                                }
                            })
                    .setNegativeButton(getString(R.string.uredi),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    Intent addEditContact = new Intent(ViewPet.this, AddReminder.class);
                                    addEditContact.putExtra("ID", arg3);

                                    startActivity(addEditContact); // start the Activity
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

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
