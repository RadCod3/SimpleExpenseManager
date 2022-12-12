package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Account Table Columns
    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_NO_COL = "account_no";
    public static final String BANK_NAME_COL = "bank_name";
    public static final String HOLDER_NAME_COL = "holder_name";
    public static final String BALANCE_COL = "balance";

    // Transaction Table Columns
    public static final String TRANSACTION_TABLE = "account_transaction";
    public static final String DATE_COL = "date";
    public static final String EXPENSE_TYPE = "expense_type";
    public static final String AMOUNT_COL = "amount";
    public static final String TRANSACTION_ID_COL = "id";


    public SQLiteHelper(@Nullable Context context) {
        super(context, "200555H.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        System.out.println("Creating Tables");
        // Creating account Table
        String createAccountTableStatement =
                "CREATE TABLE " + ACCOUNT_TABLE + "(" +
                        ACCOUNT_NO_COL + " TEXT PRIMARY KEY, " +
                        BANK_NAME_COL + " TEXT NOT NULL, " +
                        HOLDER_NAME_COL + " TEXT NOT NULL, " +
                        BALANCE_COL + " REAL NOT NULL)";

        // Creating transaction Table
        String createTransactionTableStatement = "CREATE TABLE " + TRANSACTION_TABLE + "(" +
                TRANSACTION_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_COL + " TEXT NOT NULL, " +
                ACCOUNT_NO_COL + " TEXT NOT NULL, " +
                EXPENSE_TYPE + " TEXT NOT NULL, " +
                AMOUNT_COL + " REAL NOT NULL, " +
                "FOREIGN KEY(" + ACCOUNT_NO_COL + ") REFERENCES " + ACCOUNT_TABLE + "(" + ACCOUNT_NO_COL + "))";

        sqLiteDatabase.execSQL(createAccountTableStatement);
        sqLiteDatabase.execSQL(createTransactionTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        onCreate(sqLiteDatabase);
    }
}
