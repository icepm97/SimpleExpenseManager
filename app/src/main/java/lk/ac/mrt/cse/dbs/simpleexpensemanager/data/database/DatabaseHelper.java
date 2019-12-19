package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Expense.db";

    public static class Account implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_ACCOUNT_NO = "account_no";
        public static final String COLUMN_NAME_BANK_NAME = "bank_name";
        public static final String COLUMN_NAME_ACCOUNT_HOLDER_NAME = "account_holder_name";
        public static final String COLUMN_NAME_BALANCE = "balance";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ACCOUNT_NO + " TEXT," +
                        COLUMN_NAME_BANK_NAME + " TEXT," +
                        COLUMN_NAME_ACCOUNT_HOLDER_NAME + " TEXT," +
                        COLUMN_NAME_BALANCE + " decimal(10,2))";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "`transaction`";
        public static final String COLUMN_NAME_DATE = "account_no";
        public static final String COLUMN_NAME_ACOOUNT_NO = "bank_name";
        public static final String COLUMN_NAME_EXPENSE_TYPE = "account_holder_name";
        public static final String COLUMN_NAME_AMOUNT = "balance";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DATE + " TEXT," +
                        COLUMN_NAME_ACOOUNT_NO + " TEXT," +
                        COLUMN_NAME_EXPENSE_TYPE + " TEXT," +
                        COLUMN_NAME_AMOUNT + " decimal(10,2))";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Account.SQL_CREATE_ENTRIES);
        db.execSQL(Transaction.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Account.SQL_DELETE_ENTRIES);
        db.execSQL(Transaction.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}