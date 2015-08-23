package com.piotr.app.insurance;

/**
 * Created by Piotr on 2015-08-15.
 */
import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DataAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor showAllTables(){
        String mySql = " SELECT name FROM sqlite_master " + " WHERE type='table'";
        return mDb.rawQuery(mySql, null);
    }


    public Cursor getAllCompanies() {

        String sql = "SELECT * FROM marki ORDER BY LOWER(marka)";

        return mDb.rawQuery(sql, null);

    }

    public Cursor getModelsForCompany(int id, int year){
/*
        SELECT * FROM modele
        WHERE id_marka LIKE 1
        AND (prod_od < 2010 AND prod_do LIKE 0)
        OR (2010 BETWEEN prod_od AND prod_do)
*/

        String sql = String.format("SELECT * FROM modele WHERE id_marka LIKE %d AND ((prod_od < %d AND prod_do LIKE 0) OR (%d BETWEEN prod_od AND prod_do))",id,year,year);

        return mDb.rawQuery(sql, null);

    }

    public Cursor getModelWithId(int id) {

        String sql = "SELECT * FROM modele WHERE MODEL_ID LIKE "+String.valueOf(id);

        return mDb.rawQuery(sql, null);

    }

    public Cursor getCapaciciesOfModel(int id) {

        String sql = "SELECT pojemnosc FROM modele WHERE MODEL_ID LIKE "+String.valueOf(id);

        return mDb.rawQuery(sql, null);

    }


    public Cursor getMinYearOfProduction(int id) {

        String sql = "SELECT MIN(prod_od) FROM modele WHERE id_marka LIKE "+String.valueOf(id);

        return mDb.rawQuery(sql, null);

    }


    public Cursor getMaxYearOfProduction(int id) {

        String sql = "SELECT MAX(prod_do) FROM modele WHERE id_marka LIKE "+String.valueOf(id);

        return mDb.rawQuery(sql, null);

    }


    public Cursor getTestData()
    {

        try

        {

            String sql ="SELECT * FROM marki";

            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur!=null)

            {

                mCur.moveToNext();

            }

            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}