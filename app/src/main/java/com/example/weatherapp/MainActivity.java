package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView res;
    LocationManager locationManager;
    LocationListener listener;
    String cityname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputcity);
        res = (TextView) findViewById(R.id.Output);

    }

    public void getWeather(View view) {
        String text= String.valueOf(editText.getText());
        editText.setText("");
        editText.setHint(text);
        res.setVisibility(View.INVISIBLE);
        jsonget jsonget = new jsonget();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + text + "&appid=192cf0b3d9d4f1b6f1b7a1577e534473";
        jsonget.execute(url);
        InputMethodManager img = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        img.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public class jsonget extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            Log.i("Starting", "Reached Here");
            String res = "";
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    res += (char) data;
                    data = reader.read();
                }
                Log.d("Aanshik", res);
                return res;

            } catch (Exception e) {
                Log.d("Error here", e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherinfo = jsonObject.getString("weather");
                    JSONArray jsonArray = new JSONArray(weatherinfo);
                    String message = "";
                    Log.d("Insidde the loop", "I'M NOT IN");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.d("Insidde the loop", "I'M IN");
                        JSONObject jsonpart = jsonArray.getJSONObject(i);
                        String main = jsonpart.getString("main");
                        String description = jsonpart.getString("description");
                        Log.d("Main get", main);
                        Log.d("description", description);
                        if ((!main.equals("")) || (!description.equals(""))) {
                            message += main + ": " + description + "\r\n";
                        }
                    }
                    String moreinfo = jsonObject.getString("main");
                    JSONObject moreinf = new JSONObject(moreinfo);
                    String temprature = moreinf.getString("temp");
                    String feelslike = moreinf.getString("feels_like");
                    temprature = String.format ("%,.2f",(Float.parseFloat(temprature)-273))+" °C";
                    feelslike = String.format ("%,.2f",(Float.parseFloat(feelslike)-273))+" °C";
                    String humidity=moreinf.getString("humidity");
                    String pressure = moreinf.getString("pressure");
                    message+="Temprature: "+temprature+"\r\n";
                    message+="Feels like: "+feelslike+"\r\n";
                    message+="Humidity: "+humidity+"%"+"\r\n";
                    message+="Pressure: "+pressure+"hPa"+"\r\n";





                    if (!message.equals("")) {
                        res.setText(message);
                        res.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                    Log.i("Error", e.toString());
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(MainActivity.this, "Invalid Input! Try again.", LENGTH_SHORT).show();
            }
        }
    }
}