/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    DatabaseHelper dbHelper;

    public PersistentTransactionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Transaction.COLUMN_NAME_DATE, (new SimpleDateFormat("yyyy-MM-dd")).format(date));
        values.put(DatabaseHelper.Transaction.COLUMN_NAME_ACOOUNT_NO, accountNo);
        values.put(DatabaseHelper.Transaction.COLUMN_NAME_EXPENSE_TYPE, expenseType.toString());
        values.put(DatabaseHelper.Transaction.COLUMN_NAME_AMOUNT, amount);

        db.insert(DatabaseHelper.Transaction.TABLE_NAME, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Transaction.TABLE_NAME;

        String[] columns = {
                DatabaseHelper.Transaction.COLUMN_NAME_DATE,
                DatabaseHelper.Transaction.COLUMN_NAME_ACOOUNT_NO,
                DatabaseHelper.Transaction.COLUMN_NAME_EXPENSE_TYPE,
                DatabaseHelper.Transaction.COLUMN_NAME_AMOUNT
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

        List transactions = new ArrayList<Transaction>();
        while(cursor.moveToNext()) {

            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_DATE));
            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd")).parse(dateString);
            } catch (ParseException e) {
                date = new Date();
            }

            String expenseTypeString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_EXPENSE_TYPE));
            ExpenseType expenseType = (expenseTypeString == ExpenseType.INCOME.toString()) ?
                    ExpenseType.INCOME:
                    ExpenseType.EXPENSE;

            Transaction transaction = new Transaction(
                    date,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_ACOOUNT_NO)),
                    expenseType,
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_AMOUNT))
            );

            transactions.add(transaction);
        }
        cursor.close();

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = DatabaseHelper.Transaction.TABLE_NAME;

        String[] columns = {
                DatabaseHelper.Transaction.COLUMN_NAME_DATE,
                DatabaseHelper.Transaction.COLUMN_NAME_ACOOUNT_NO,
                DatabaseHelper.Transaction.COLUMN_NAME_EXPENSE_TYPE,
                DatabaseHelper.Transaction.COLUMN_NAME_AMOUNT
        };

        String limitString = Integer.toString(limit);

        Cursor cursor = db.query(
                table,  // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null,             // The sort order
                limitString
        );

        List transactions = new ArrayList<Transaction>();
        while (cursor.moveToNext()) {

            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_DATE));
            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd")).parse(dateString);
            } catch (ParseException e) {
                date = new Date();
            }

            String expenseTypeString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_EXPENSE_TYPE));
            ExpenseType expenseType = (expenseTypeString.equals(ExpenseType.INCOME.toString())) ?
                    ExpenseType.INCOME:
                    ExpenseType.EXPENSE;

            Transaction transaction = new Transaction(
                    date,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_ACOOUNT_NO)),
                    expenseType,
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.Transaction.COLUMN_NAME_AMOUNT))
            );

            transactions.add(transaction);
        }
        cursor.close();

        return transactions;
    }

}
