package com.caitou.simpleweather.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.caitou.simpleweather.R;
import com.caitou.simpleweather.model.City;
import com.caitou.simpleweather.model.County;
import com.caitou.simpleweather.model.Province;
import com.caitou.simpleweather.model.WeatherDB;
import com.caitou.simpleweather.utils.HandleDataUtil;
import com.caitou.simpleweather.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-05.
 */
public class ChooseAreaActivity extends Activity {
    private static final String TAG = "ChooseAreaActivity";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<>();

    // 省列表
    private List<Province> provinceList;
    // 市列表
    private List<City> cityList;
    // 县列表
    private List<County> countyList;

    // 选中的省份
    private Province selectProvince;
    // 选中的城市
    private City selectCity;

    // 当前选中的级别
    private int currentLevel;

    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(i);
                    queryCounties();
                }
            }
        });
        // 加载省级数据
        queryProvinces();
    }

    /**
     *  查询全国所有的省，优先从数据库中查询，如果没有就到服务器中查询
     * */
    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            // 从服务器中获取省份列表
            queryFromServer(null, "province");
        }
    }

    /**
     *  查询选中省内所有的市，优先从数据库中查询，如果没有就到服务器中查询
     * */
    private void queryCities() {
        cityList = weatherDB.loadCites(selectProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            // 从服务器中获取城市列表
            queryFromServer(selectProvince.getProvinceCode(), "city");
        }
    }

    /**
     *  查询选中市内所有的县，优先从数据库中查询，如果没有就到服务器中查询
     * */
    private void queryCounties() {
        countyList = weatherDB.loadCounties(selectCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            // 从服务器中获取县级列表
            queryFromServer(selectCity.getCityCode(), "county");
        }
    }

    /**
     *  根据传入代号和类型从服务器中查询省市县数据
     * */
    private void queryFromServer(String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = HandleDataUtil.handleProvincesResponse(weatherDB, response);
                } else if ("city".equals(type)) {
                    result = HandleDataUtil.handleCitiesResponse(weatherDB, response, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = HandleDataUtil.handleCountiesResponse(weatherDB, response, selectCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法返回主线处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *  显示进度对话框
     * */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     *  关闭进度对话框
     * */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     *  捕获back键，根据当前级别来判断，此时应该返回的列表，还是退出应用
     * */
    @Override
    public void onBackPressed() {
        System.out.println("ddddddddddddddd " + currentLevel);
        if (currentLevel == LEVEL_PROVINCE) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryCounties();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(ChooseAreaActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }
}
