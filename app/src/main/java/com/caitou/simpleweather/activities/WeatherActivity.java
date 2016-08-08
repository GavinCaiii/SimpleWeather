package com.caitou.simpleweather.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caitou.simpleweather.R;
import com.caitou.simpleweather.utils.HttpUtil;
import com.caitou.simpleweather.utils.Utility;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-08.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;

    // 显示城市名
    private TextView cityText;

    // 显示发布时间
    private TextView publishText;

    // 显示天气描述信息
    private TextView weatherDespText;

    // 显示气温1
    private TextView temp1Text;

    // 显示气温2
    private TextView temp2Text;

    // 显示当前日期
    private TextView currentDataText;

    // 切换城市按钮
    private Button switchCity;

    // 更新天气按钮
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);

        cityText = (TextView) findViewById(R.id.city_text);
        publishText = (TextView) findViewById(R.id.public_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDataText = (TextView) findViewById(R.id.current_data);

        switchCity = (Button) findViewById(R.id.home);
        refreshWeather = (Button) findViewById(R.id.refresh);

        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            // 查询天气
            queryWeatherCode(countyCode);
        } else {
            // 直接显示本地天气
            showWeather();
        }

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    // 查询县级代号所对应的天气代号
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";

        // 根据县级代号从服务器中查询天气代号，type = "countyCode"
        queryFromServer(address, "countyCode");
    }

    // 查询天气代号所对应的天气
    private void queryWeatherInfo(String weatherCode) {
        System.out.println("ddddddddddddddddddddd weatherCode = " + weatherCode);
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".xml";

        // 根据天气代号从服务器中查询天气, type = "weatherCode"
        queryFromServer(address, "weatherCode");
    }

    // 根据传入的地址和类型去向服务器查询天气代号或天气信息
    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 解析服务器返回的JSON数据，并将它们保存到SharePreferences文件中
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 从SharePreferences文件中提取出天气信息并显示
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     *  从SharePreferences文件中读取存储的天气信息，并显示到界面上
     * */
    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityText.setText(preferences.getString("city_name", ""));
        temp1Text.setText(preferences.getString("temp1", ""));
        temp2Text.setText(preferences.getString("temp2", ""));
        weatherDespText.setText(preferences.getString("weather_desp", ""));
        publishText.setText("今天" + preferences.getString("publish_time", "" + "发布"));
        currentDataText.setText(preferences.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.home:
                break;
            case R.id.refresh:
                break;
        }
    }
}
