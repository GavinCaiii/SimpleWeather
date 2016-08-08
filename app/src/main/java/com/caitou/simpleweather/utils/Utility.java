package com.caitou.simpleweather.utils;

import android.text.TextUtils;

import com.caitou.simpleweather.model.City;
import com.caitou.simpleweather.model.County;
import com.caitou.simpleweather.model.Province;
import com.caitou.simpleweather.model.WeatherDB;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-05.
 */
public class Utility {

    // 解析和处理服务器返回的省级数据
    public synchronized static boolean handleProvincesResponse(WeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到province表格中
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    // 解析和处理服务器返回的市级数据
    public static boolean handleCitiesResponse(WeatherDB db, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存放到city表格中
                    db.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    // 解析和处理服务器返回的县级数据
    public static boolean handleCountiesResponse(WeatherDB db, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表格中
                    db.saveCounties(county);
                }
                return true;
            }
        }
        return false;
    }
}
