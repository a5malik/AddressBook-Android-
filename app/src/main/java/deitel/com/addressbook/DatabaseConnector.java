package deitel.com.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by akash on 8/22/2015.
 */
public class DatabaseConnector {
    public static final String DATABASE_NAME = "UserContacts";

    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context)
    {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open()
    {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        if(database != null)
            database.close();
    }

    public long insertContact(String name, String phone, String email,
                              String street, String city, String state, String zip)
    {
        ContentValues newContact = new ContentValues();
        newContact.put("name", name);
        newContact.put("phone", phone);
        newContact.put("email", email);
        newContact.put("street", street);
        newContact.put("city", city);
        newContact.put("state", state);
        newContact.put("zip", zip);

        open();
        long rowID = database.insert("contacts", null, newContact);
        close();
        return rowID;
    }

    public void updateContact(long id, String name, String phone,
                              String email, String street, String city, String state, String zip)

    {
        ContentValues editContact = new ContentValues();
        editContact.put("name", name);
        editContact.put("phone", phone);
        editContact.put("email", email);
        editContact.put("street", street);
        editContact.put("city", city);
        editContact.put("state", state);
        editContact.put("zip", zip);

        open();
        database.update("contacts", editContact, "_id= " + id, null);
        close();
    }

    public Cursor getAllContacts()
    {
        return database.query("contacts", new String[] {"_id", "name"},
                null, null, null, null, "name");
    }

    public Cursor getOneContact(long rowID)
    {
        return database.query("contacts", null, "_id="+ rowID, null, null, null, null);
    }

    public void deleteContact(long id)
    {
        open();
        database.delete("contacts", "_id=" + id, null);
        close();
    }


    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE contacts" +
                    "(_id integer primary key autoincrement," +
                    "name TEXT, phone TEXT, email TEXT, " +
                    "street TEXT, city TEXT, state TEXT, zip TEXT);";
            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        }
    }
}
