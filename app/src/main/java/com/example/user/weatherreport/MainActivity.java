package com.example.user.weatherreport;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
{
    EditText cityName;
    TextView resultTextView;
    String message = "";

    public void findWeather(View view)
    {
        String name="";
        try
        {
            name = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + name + "&appid=9421e0180ec0ada0f0358d609abb2039";
        //https://api.openweathermap.org/data/2.5/weather?q=Delhi&appid=9421e0180ec0ada0f0358d609abb2039
        DownloadTask task = new DownloadTask();
        task.execute(url);
    }



    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        String result = "";
        @Override
        protected String doInBackground(String... strings)
        {

            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1)
                {
                    result = result + (char) data;
                    data = reader.read();
                }
                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (result == null)
            {
                Toast.makeText(getApplicationContext(),"Could not find weather of entered city!!!\n        Check name of city once again!",Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    String mainString = "";
                    String description = "";
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray myArray = jsonObject.getJSONArray("weather");
                    for(int i=0;i<myArray.length();i++)
                    {
                        JSONObject jsonPart =myArray.getJSONObject(i);
                        mainString = jsonPart.getString("main");
                        description = jsonPart.getString("description");
                        message = mainString + ": " + description;
                    }
                    //Log.i("data",message);
                    //resultTextView.setText(message);

                    JSONObject main = jsonObject.getJSONObject("main");
                    double tempK = Double.parseDouble(main.getString("temp"));
                    double temp = tempK - 273.15;
                    String actualTemp = String.format("%.2f",temp);
                    double pressure = Double.parseDouble(main.getString("pressure"));
                    String actualPressure = String.format("%.2f",pressure);
                    double humidity = Double.parseDouble(main.getString("humidity"));
                    String actualHumidity = String.format("%.2f",humidity);
                    message = message + "\n" + "Temperature: " + actualTemp + " °c" + "\n" + "Humidity: " + actualHumidity + " %" + "\n" +"Pressure: " + actualPressure + " mb";
                    resultTextView.setText(message);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }




   public void refresh(View view)
   {
       resultTextView.setText(null);
       cityName.setText("Enter City Name.");
   }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView =(TextView) findViewById(R.id.resultTextView);



    }
}
