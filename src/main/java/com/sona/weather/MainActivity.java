package com.sona.weather;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    EditText cityName;


    public void findWeather(View view){
        InputMethodManager mgr=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        try {
            String encodedCityName= URLEncoder.encode( cityName.getText().toString(),"UTF-8");

            DownloadWeather downloadWeather = new DownloadWeather();
            downloadWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" +encodedCityName + "&APPID=your_api_key");
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_LONG).show();
        }
    }

    public class DownloadWeather extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader ir = new InputStreamReader(is);
                int data;

                String result = "";
                while ((data = ir.read()) != -1) {
                    char curr = (char) data;
                    result = result + curr;
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_LONG).show();
            }
            

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String finaltext="";
            try {
                JSONObject jsonObject=new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");
                JSONArray arr=new JSONArray(weatherInfo);
                for (int i=0;i<arr.length();i++) {
                    JSONObject jpart = arr.getJSONObject(i);
                     finaltext="Weather: "+jpart.getString("main") +"  ; \n    "+ jpart.getString("description");

                }

                double x=Double.parseDouble((jsonObject.getJSONObject("main")).getString("temp"));
                x=x-273.15;
                Integer i=(int)x;
                finaltext+="\nTemperature: "+i+" °C";
                tv.setText(finaltext);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.weatherData);
        cityName = (EditText) findViewById(R.id.cityName);

    }
}
