package com.circle.prana.bb.API;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.circle.prana.bb.POJO.LogInDataBean;
import com.circle.prana.bb.POJO.TransactionDataBean;

public class SQLiteHandler extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "android_api";

    // Login table name
    public static final String TABLE_DATA = "user";
    public static final String TABLE_TRANSACTION = "transactions";

    // Login Table Columns names
    public static final String COLUMN_ACCOUNT_NUMBER = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Login table name


    // Login Table Columns names
    public static final String COLUMN_TRANSACTION_NUMBER = "id";
    public static final String COLUMN_FROM = "fromAccount";
    public static final String COLUMN_TO = "toAccount";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CURRENCY_CODE = "currency_code";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";

    public static final String CREATE_SIGNUP_TABLE = "CREATE TABLE " + TABLE_DATA + "("
            + COLUMN_ACCOUNT_NUMBER + " TEXT ,"
            + COLUMN_FIRST_NAME + " TEXT,"
            + COLUMN_LAST_NAME + " TEXT,"
            + COLUMN_CONTACT + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASSWORD + " TEXT"
            + ')';

    public static final String CREATE_TRAANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
            + COLUMN_TRANSACTION_NUMBER + " TEXT ,"
            + COLUMN_FROM + " TEXT,"
            + COLUMN_TO + " TEXT,"
            + COLUMN_AMOUNT + " TEXT,"
            + COLUMN_CURRENCY_CODE + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_TIME + " TEXT"
            + ')';


    public SQLiteDatabase sqlDatabase;


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SIGNUP_TABLE);
        db.execSQL(CREATE_TRAANSACTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }

    private void getReadableSqlDatabase() {
        sqlDatabase = this.getReadableDatabase();
    }

    private void getWritableSqlDatabase() {
        sqlDatabase = this.getWritableDatabase();
    }

    private void closeSqlDatabase() {
        sqlDatabase.close();
    }

    ///Add
    public void addUser(String id, String firstname, String lastname, String contact, String email, String password) {

        try {
            getWritableSqlDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_ACCOUNT_NUMBER, id); // AccountNumber
            values.put(COLUMN_FIRST_NAME, firstname); // Firstname
            values.put(COLUMN_LAST_NAME, lastname); // Lastname
            values.put(COLUMN_CONTACT, contact); // Contact
            values.put(COLUMN_EMAIL, email); // Email
            values.put(COLUMN_PASSWORD, password); // Password



            // Inserting Row
            sqlDatabase.insertWithOnConflict(TABLE_DATA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cursor cursor = sqlDatabase.rawQuery("SELECT * FROM " + TABLE_DATA, null);
            closeSqlDatabase();
        }
        sqlDatabase.close();
    }

    public void addTransaction(String id, String fromA, String toA, String amount, String currency, String date, String time) {

        try {
            getWritableSqlDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TRANSACTION_NUMBER, id);
            values.put(COLUMN_FROM, fromA);
            values.put(COLUMN_TO, toA);
            values.put(COLUMN_AMOUNT, amount);
            values.put(COLUMN_CURRENCY_CODE, currency);
            values.put(COLUMN_DATE, date);
            values.put(COLUMN_TIME, time);



            // Inserting Row
            sqlDatabase.insertWithOnConflict(TABLE_TRANSACTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cursor cursor = sqlDatabase.rawQuery("SELECT * FROM " + TABLE_TRANSACTION, null);
            closeSqlDatabase();
        }
        sqlDatabase.close();
    }

    //Get
    public LogInDataBean getLOGINData(String serviceid)  {
        LogInDataBean servicesData = new LogInDataBean();
        String selectQuery;

        selectQuery = "SELECT * FROM " + TABLE_DATA + " WHERE " + COLUMN_ACCOUNT_NUMBER + " = '" + serviceid+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    servicesData.setSignUpAccountNumber(cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNT_NUMBER)));
                    servicesData.setSignUpFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
                    servicesData.setSignUpLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
                    servicesData.setSignUpPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
                    servicesData.setSignUpContact(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT)));
                    servicesData.setSignUpEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return servicesData;
    }

    public TransactionDataBean getTransactionData(String serviceid)  {
        TransactionDataBean servicesData = new TransactionDataBean();
        String selectQuery;

        selectQuery = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_NUMBER + " = '" + serviceid+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    servicesData.setUserAccountNumber(cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_NUMBER)));
                    servicesData.setUserFrom(cursor.getString(cursor.getColumnIndex(COLUMN_FROM)));
                    servicesData.setUserTo(cursor.getString(cursor.getColumnIndex(COLUMN_TO)));
                    servicesData.setUserAmount(cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT)));
                    servicesData.setUserCurrency(cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY_CODE)));
                    servicesData.setUserDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    servicesData.setUserTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return servicesData;
    }

    //Delete
    public void deleteUsers(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_DATA + " WHERE " + COLUMN_ACCOUNT_NUMBER + " = '" + value + "'");
        db.close();
    }

    public void deleteTransactions(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_NUMBER + " = '" + value + "'");
        db.close();
    }

}
