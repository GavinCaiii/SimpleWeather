package com.caitou.simpleweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.caitou.simpleweather.db.WeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-04.
 */
public class WeatherDB {
    private static final String TAG = "WeatherDB";

    // 数据库名字
    public static final String DB_NAME = "weather";

    private static final String TB_PROVINCE = "province";
    private static final String TB_PROVINCE_NAME = "province_name";
    private static final String TB_PROVINCE_CODE = "province_code";
    private static final String TB_PROVINCE_ID = "province_id";

    private static final String TB_CITY = "city";
    private static final String TB_CITY_NAME = "city_name";
    private static final String TB_CITY_CODE = "city_code";
    private static final String TB_CITY_ID = "city_id";

    private static final String TB_COUNTRY = "country";
    private static final String TB_COUNTRY_NAME = "country_name";
    private static final String TB_COUNTRY_CODE = "country_code";

    // 数据库版本
    public static int VERSION = 1;

    private static WeatherDB instance;

    private SQLiteDatabase db;

    private WeatherDB(Context context) {
        WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        // 创建数据库
        db = dbHelper.getWritableDatabase();
    }

    // 单例模型
    public synchronized static WeatherDB getInstance(Context context) {
        if (instance == null)
            instance = new WeatherDB(context);
        return instance;
    }

    // 将province实例存储到数据库
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put(TB_PROVINCE_NAME, province.getProvinceName());
            values.put(TB_PROVINCE_CODE, province.getProvinceCode());
            db.insert(TB_PROVINCE, null, values);
        }
    }

    // 从数据库中读取全国所有省份的信息
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query(TB_PROVINCE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(TB_PROVINCE_NAME)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(TB_COUNTRY_CODE)));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    // 将city实例存储到数据库
    public void saveCity(City city) {
        if (city == null)
            return;
        ContentValues values = new ContentValues();
        values.put(TB_CITY_NAME, city.getCityName());
        values.put(TB_PROVINCE_ID, city.getProvinceId());
        values.put(TB_CITY_CODE, city.getCityCode());

        db.insert(TB_CITY, null, values);
    }

    // 从数据库中读取某省下所有的城市信息
    public List<City> loadCites(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query(TB_CITY, null, "province_id = ?",
                new String[] {String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex(TB_CITY_NAME)));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(TB_CITY_CODE)));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    // 将country实例存储到数据库
    public void saveCountries(Country country) {
        if (country == null)
            return;
        ContentValues values = new ContentValues();
        values.put(TB_COUNTRY_NAME, country.getCountryName());
        values.put(TB_COUNTRY_CODE, country.getCountryCode());
        values.put(TB_CITY_ID, country.getCityId());

        db.insert(TB_CITY, null, values);
    }

    // 从数据库中读取某城市下所有的县城信息
    public List<Country> loadCountries(int cityId) {
        List<Country> list = new ArrayList<>();
        Cursor cursor = db.query(TB_COUNTRY, null, "city_id = ?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex(TB_COUNTRY_NAME)));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex(TB_CITY_CODE)));
                list.add(country);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
