package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private final SQLiteHelper sqLiteHelper;

    public PersistentAccountDAO(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> accountNumbers = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String query = "SELECT " + SQLiteHelper.ACCOUNT_NO_COL + " FROM " + SQLiteHelper.ACCOUNT_TABLE;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(SQLiteHelper.ACCOUNT_NO_COL));
                accountNumbers.add(accountNo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String query = "SELECT * FROM " + SQLiteHelper.ACCOUNT_TABLE;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(SQLiteHelper.ACCOUNT_NO_COL));
                String bankName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.BANK_NAME_COL));
                String accountHolderName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HOLDER_NAME_COL));
                double balance = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.BALANCE_COL));

                accounts.add(new Account(accountNo, bankName, accountHolderName, balance));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String[] parameters = {accountNo};
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        // prepared statement to avoid sql injection
        String query = "SELECT * FROM " + SQLiteHelper.ACCOUNT_TABLE + " WHERE accountNo = ?";

        Cursor cursor = sqLiteDatabase.rawQuery(query, parameters);

        if (!cursor.moveToFirst()) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        String bankName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.BANK_NAME_COL));
        String accountHolderName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HOLDER_NAME_COL));
        double balance = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.BALANCE_COL));

        Account account = new Account(accountNo, bankName, accountHolderName, balance);


        cursor.close();
        sqLiteDatabase.close();

        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SQLiteHelper.ACCOUNT_NO_COL, account.getAccountNo());
        contentValues.put(SQLiteHelper.BANK_NAME_COL, account.getBankName());
        contentValues.put(SQLiteHelper.HOLDER_NAME_COL, account.getAccountHolderName());
        contentValues.put(SQLiteHelper.BALANCE_COL, account.getBalance());

        sqLiteDatabase.insert(SQLiteHelper.ACCOUNT_TABLE, null, contentValues);
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        String[] parameters = {accountNo};
        String whereClause = SQLiteHelper.ACCOUNT_NO_COL + " = ?";
        int rowsAffected = sqLiteDatabase.delete(SQLiteHelper.ACCOUNT_TABLE, whereClause, parameters);

        // if 0 rows are effected that means no such row with accountNo exists
        if (rowsAffected == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        sqLiteDatabase.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String[] parameters = {accountNo};
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String query = "SELECT " + SQLiteHelper.BALANCE_COL + " FROM " + SQLiteHelper.ACCOUNT_TABLE + " WHERE " + SQLiteHelper.ACCOUNT_NO_COL + "= ?";

        Cursor cursor = sqLiteDatabase.rawQuery(query, parameters);

        if (!cursor.moveToFirst()) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        double currBalance = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.BALANCE_COL));
        cursor.close();

        switch (expenseType) {
            case EXPENSE:
                currBalance -= amount;
                break;
            case INCOME:
                currBalance += amount;
                break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.BALANCE_COL, currBalance);

        String whereClause = SQLiteHelper.ACCOUNT_NO_COL + " = ?";
        sqLiteDatabase.update(SQLiteHelper.ACCOUNT_TABLE, contentValues, whereClause, parameters);
        sqLiteDatabase.close();
    }
}
