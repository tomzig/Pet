// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package pet.emp.fri.mypet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
    // database name
    private static final String DATABASE_NAME = "pets";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context) {
        // create a new DatabaseOpenHelper
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    } // end DatabaseConnector constructor

    // open the database connection
    public void open() throws SQLException {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } // end method open

    // close the database connection
    public void close() {
        if (database != null)
            database.close(); // close the database connection
    } // end method close

    // inserts a new contact in the database
    public void insertPets(String ime, String vrsta, String rojDan, String velikost, String teza, String cip, String stevilka, String drugo) {
        ContentValues pets = new ContentValues();
        pets.put("ime", ime);
        pets.put("vrsta", vrsta);
        pets.put("rojDan", rojDan);
        pets.put("velikost", velikost);
        pets.put("teza", teza);
        pets.put("cip", cip);
        pets.put("stevilka", stevilka);
        pets.put("drugo", drugo);

        open(); // open the database
        database.insert("allpets", null, pets);
        close(); // close the database
    } // end method insertPets

    public void insertReminders(String naslov, String datum, String ura, String opombe) {
        ContentValues pets = new ContentValues();
        pets.put("naslov", naslov);
        pets.put("datum", datum);
        pets.put("ura", ura);
        pets.put("opombe", opombe);

        open(); // open the database
        database.insert("reminders", null, pets);
        close(); // close the database
    } // end method insertPets

    // inserts a new contact in the database
    public void updatePets(long id, String ime, String vrsta, String rojDan, String velikost, String teza, String cip, String stevilka, String drugo) {
        ContentValues pets = new ContentValues();
        pets.put("ime", ime);
        pets.put("vrsta", vrsta);
        pets.put("rojDan", rojDan);
        pets.put("velikost", velikost);
        pets.put("teza", teza);
        pets.put("cip", cip);
        pets.put("stevilka", stevilka);
        pets.put("drugo", drugo);

        open(); // open the database
        database.update("allpets", pets, "_id=" + id, null);
        close(); // close the database
    } // end method updatePets

    public void updateReminders(long id, String naslov, String datum, String ura, String opombe) {
        ContentValues pets = new ContentValues();
        pets.put("naslov", naslov);
        pets.put("datum", datum);
        pets.put("ura", ura);
        pets.put("opombe", opombe);

        open(); // open the database
        database.update("reminders", pets, "_id=" + id, null);
        close(); // close the database
    } // end method insertPets

    // return a Cursor with all contact information in the database
    public Cursor getAllPets() {
        return database.query("allpets", new String[]{"_id", "ime", "vrsta", "rojDan", "velikost", "teza", "cip", "stevilka", "drugo"}, null, null, null, null, "ime");
    } // end method getAllPets

    // return a Cursor with all contact information in the database
    public Cursor getAllReminders() {
        return database.query("reminders", new String[]{"_id", "naslov", "datum", "ura", "opombe"}, null, null, null, null, "naslov");
    } // end method getAllPets

    // get a Cursor containing all information about the contact specified
    // by the given id
    public Cursor getOnePet(long id) {
        return database.query("allpets", null, "_id=" + id, null, null, null, null);
    } // end method getOnePet

    public Cursor getOneReminder(long id) {
        return database.query("reminders", null, "_id=" + id, null, null, null, null);
    }
    // delete the contact specified by the given String name
    public void deletePet(long id) {
        open(); // open the database
        database.delete("allpets", "_id=" + id, null);
        close(); // close the database
    } // end method deletePet
    public void deleteReminder(long id) {
        open(); // open the database
        database.delete("reminders", "_id=" + id, null);
        close(); // close the database
    } // end method deletePet

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        // public constructor
        public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        } // end DatabaseOpenHelper constructor

        // creates the contacts table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db) {
            // query to create a new table named contacts
            String createQuery1 = "CREATE TABLE allpets (_id integer primary key autoincrement, ime TEXT, vrsta TEXT, rojDan TEXT, velikost TEXT, teza TEXT, cip TEXT, stevilka TEXT, drugo TEXT);";
            // initializing the database
            String insertValues1 = "INSERT INTO allpets (_ID, ime, vrsta, rojDan, velikost, teza, cip, stevilka, drugo) values (NULL, 'Rita', 'Francoski buldog', '23.7.2014', '35 cm', '6kg', '6545455', '040-123-456', 'Ima gliste.');";
            db.execSQL(createQuery1); // execute the query
            db.execSQL(insertValues1);

            String createQuery2 = "CREATE TABLE reminders (_id integer primary key autoincrement, naslov TEXT, datum TEXT, ura TEXT, opombe TEXT);";
            String insertValues3 = "INSERT INTO reminders (_ID, naslov, datum, ura, opombe) values (NULL, 'Cepljenje', '20.12.2014', '10.00', 'Cena: 30€');";
            db.execSQL(createQuery2); // execute the query
            db.execSQL(insertValues3);

        } // end method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        } // end method onUpgrade

    } // end class DatabaseOpenHelper
} // end class DatabaseConnector
