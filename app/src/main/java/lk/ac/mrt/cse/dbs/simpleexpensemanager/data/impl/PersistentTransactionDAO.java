package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private final SQLiteHelper sqLiteHelper;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));

    public PersistentTransactionDAO(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.DATE_COL, date.toString());
        contentValues.put(SQLiteHelper.ACCOUNT_NO_COL, accountNo);
        contentValues.put(SQLiteHelper.EXPENSE_TYPE, expenseType.toString());
        contentValues.put(SQLiteHelper.AMOUNT_COL, amount);

        sqLiteDatabase.insert(SQLiteHelper.TRANSACTION_TABLE, null, contentValues);
        sqLiteDatabase.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String query = "SELECT * FROM " + SQLiteHelper.TRANSACTION_TABLE;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(SQLiteHelper.ACCOUNT_NO_COL));

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATE_COL));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String expenseTypeString = cursor.getString(cursor.getColumnIndex(SQLiteHelper.EXPENSE_TYPE));
                ExpenseType expenseType = expenseTypeString.equals("EXPENSE") ? ExpenseType.EXPENSE : ExpenseType.INCOME;

                double amount = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.AMOUNT_COL));

                transactions.add(new Transaction(date, accountNo, expenseType, amount));

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String[] parameters = {String.valueOf(limit)};
        String query = "SELECT * FROM " + SQLiteHelper.TRANSACTION_TABLE + " ORDER BY " + SQLiteHelper.TRANSACTION_ID_COL + " DESC LIMIT ?";

        Cursor cursor = sqLiteDatabase.rawQuery(query, parameters);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(SQLiteHelper.ACCOUNT_NO_COL));

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATE_COL));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String expenseTypeString = cursor.getString(cursor.getColumnIndex(SQLiteHelper.EXPENSE_TYPE));
                ExpenseType expenseType = expenseTypeString.equals("EXPENSE") ? ExpenseType.EXPENSE : ExpenseType.INCOME;

                double amount = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.AMOUNT_COL));

                transactions.add(new Transaction(date, accountNo, expenseType, amount));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return transactions;
    }
}
