
package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DatabaseHelper dbHelper;

    public PersistentAccountDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Account.TABLE_NAME;

        String[] columns = {
                DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO
        };

        Cursor cursor = db.query(
                table,  // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List accountNos = new ArrayList<String>();
        while(cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO));
            accountNos.add(accountNo);
        }
        cursor.close();

        return accountNos;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Account.TABLE_NAME;

        String[] columns = {
                DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO,
                DatabaseHelper.Account.COLUMN_NAME_BANK_NAME,
                DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_HOLDER_NAME,
                DatabaseHelper.Account.COLUMN_NAME_BALANCE
        };

        Cursor cursor = db.query(
                table,  // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null             // The sort order
        );

        List accounts = new ArrayList<Account>();
        while(cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_HOLDER_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_BALANCE))
            );
            accounts.add(account);
        }
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Account.TABLE_NAME;

        String[] columns = {
                DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO,
                DatabaseHelper.Account.COLUMN_NAME_BANK_NAME,
                DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_HOLDER_NAME,
                DatabaseHelper.Account.COLUMN_NAME_BALANCE
        };

        String selection = DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        String limit  = "1";

        Cursor cursor = db.query(
                table,  // The table to query
                columns,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null,             // The sort order
                limit
        );

        Account account = null;
        if (cursor.moveToNext()) {
            account = new Account(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_HOLDER_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.Account.COLUMN_NAME_BALANCE))
            );
        }

        cursor.close();

        if (account != null) {
            return account;
        } else {
            throw new InvalidAccountException("Account " + accountNo + " is invalid.");
        }
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO, account.getAccountNo());
        values.put(DatabaseHelper.Account.COLUMN_NAME_BANK_NAME, account.getBankName());
        values.put(DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(DatabaseHelper.Account.COLUMN_NAME_BALANCE, account.getBalance());

        db.insert(DatabaseHelper.Account.TABLE_NAME, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Account.TABLE_NAME;

        String selection = DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        int count = db.delete(
                table,  // The table to query
                selection,              // The columns for the WHERE clause
                selectionArgs         // The values for the WHERE clause
        );

        if (count == 0) {
            throw new InvalidAccountException("Account " + accountNo + " is invalid.");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Account.TABLE_NAME;

        double currentBalance = getAccount(accountNo).getBalance();
        double balance = (expenseType == ExpenseType.INCOME) ?
                currentBalance + amount:
                currentBalance - amount;

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Account.COLUMN_NAME_BALANCE, balance);

        String selection = DatabaseHelper.Account.COLUMN_NAME_ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        int count = db.update(
                table,  // The table to query
                values,
                selection,              // The columns for the WHERE clause
                selectionArgs         // The values for the WHERE clause
        );

        if (count == 0) {
            throw new InvalidAccountException("Account " + accountNo + " is invalid.");
        }
    }
}
