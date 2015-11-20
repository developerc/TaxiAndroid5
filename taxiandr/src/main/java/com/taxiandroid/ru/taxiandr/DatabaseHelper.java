package com.taxiandroid.ru.taxiandr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saperov on 19.11.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "poezdki";

    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_DTIM = "dtim";
    public static final String COLUMN_PSTART = "pstart";
    public static final String COLUMN_SUMMA = "summa";
    public static final String COLUMN_ETIM = "etim";
    public static final String COLUMN_DGOR = "dgor";
    public static final String COLUMN_DPRIGOR = "dprigor";
    public static final String COLUMN_DRAION = "draion";
    public static final String COLUMN_DMGOR = "dmgor";

    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + COLUMN_DTIM
            + " text, " + COLUMN_PSTART + " text, " + COLUMN_SUMMA
            + " text, " + COLUMN_ETIM + " text,"
            + COLUMN_DGOR + " text,"
            + COLUMN_DPRIGOR + " text,"
            + COLUMN_DRAION + " text,"
            + COLUMN_DMGOR + " text);";


  /*private static final String DATABASE_CREATE_SCRIPT = "create table "
          + DATABASE_TABLE + " (" + BaseColumns._ID
          + " integer primary key autoincrement, " + COLUMN_DTIM
          + " text, " + COLUMN_PSTART + " text, " + COLUMN_SUMMA
          + " text, " + COLUMN_ETIM + " text);";
    */

    //+ " text, " + COLUMN_ETIM + " text);";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
        Log.d("SQLite", "---создаем базу данных---");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.d("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }

    // Добавляем новую запись о поездке
    public void addTaximeter(Taximeter taximeter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DTIM, taximeter.getDtim());
        values.put(COLUMN_PSTART, taximeter.getPstart());
        values.put(COLUMN_SUMMA, taximeter.getSumma());
        values.put(COLUMN_DGOR, taximeter.getmDgor());
        values.put(COLUMN_DPRIGOR, taximeter.getmDprigor());
        values.put(COLUMN_DRAION, taximeter.getmDraion());
        values.put(COLUMN_DMGOR, taximeter.getmDmgor());
        values.put(COLUMN_ETIM, taximeter.getEtim());


        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    //получить запись таксометра
    public Taximeter getTaximeter(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{COLUMN_ID, COLUMN_DTIM, COLUMN_PSTART, COLUMN_SUMMA,COLUMN_DGOR,COLUMN_DPRIGOR,COLUMN_DRAION,COLUMN_DMGOR, COLUMN_ETIM}, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Taximeter taximeter = new Taximeter(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
                return taximeter;
    }

    //получить список поездок
    public List<Taximeter> getAllTaximeter() {
        ArrayList<Taximeter> taximeterList = new ArrayList<Taximeter>();
        //выбираем всю таблицу
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Проходим по всем строкам и добавляем в список
        if (cursor.moveToFirst()) {
            do {
                Taximeter taximeter = new Taximeter();
                taximeter.setID(Integer.parseInt(cursor.getString(0)));
                taximeter.setDtim(cursor.getString(1));
                taximeter.setPstart(cursor.getString(2));
                taximeter.setSumma(cursor.getString(3));
                taximeter.setmDgor(cursor.getString(4));
                taximeter.setmDprigor(cursor.getString(5));
                taximeter.setmDraion(cursor.getString(6));
                taximeter.setmDmgor(cursor.getString(7));
                taximeter.setEtim(cursor.getString(8));
                taximeterList.add(taximeter);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return taximeterList;
    }

    // Получить число контактов
    public int getTaximeterCount() {
        String countQuery = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();
        return count;
    }

    // Обновить контакт
    public int updateContact(Taximeter taximeter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DTIM, taximeter.getDtim());
        values.put(COLUMN_PSTART, taximeter.getPstart());
        values.put(COLUMN_SUMMA, taximeter.getSumma());
        values.put(COLUMN_ETIM, taximeter.getEtim());

        // обновляем строку
        return db.update(DATABASE_TABLE, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(taximeter.getID()) });
    }

    // Удалить поездку
    public void deleteContact(Taximeter taximeter) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, COLUMN_ID + " = ?",
                new String[]{String.valueOf(taximeter.getID())});
        db.close();
    }

    //удалить все поездки
    public void deletePoezdki() {
        SQLiteDatabase db = this.getWritableDatabase();
        String delQuery = "DELETE FROM " + DATABASE_TABLE +";";
        db.execSQL(delQuery);
        db.close();
    }

    public void upgradeTaximeter() {
        SQLiteDatabase db = this.getWritableDatabase();
        String delQuery = "DROP TABLE  " + DATABASE_TABLE+";";
        // Удаляем старую таблицу и создаём новую
        db.execSQL(delQuery);
        // Создаём новую таблицу
        onCreate(db);
    }
}
