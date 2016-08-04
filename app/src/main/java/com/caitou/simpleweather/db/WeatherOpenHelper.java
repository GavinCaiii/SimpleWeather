package com.caitou.simpleweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-04.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {
    // 省级 表格建表语言
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";
    // 市级 表格建表语言
    public static final String CREATE_CITY = "create table City (" +
            "id integer primary key autoincrement, " +
            "province_name text, " +
            "province_code text, " +
            "province_id integer)";
    // 县级 表格建表语言
    public static final String CREATE_COUNTRY = "create table Country (" +
            "id integer primary key autoincrement, " +
            "country_name text, " +
            "country_code text" +
            "country_id integer)";

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建表格
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTRY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
