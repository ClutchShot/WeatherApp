package sii00.weatherhw;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.Toolbar;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import sii00.weatherhw.Adapters.DailyTempAdapter;
import sii00.weatherhw.Adapters.TimeTempAdapter;


public class MainActivity extends AppCompatActivity {

    private TextView temp_now, description, feels_like, city, sunrise, sunset, wind, humidity, pressure, uv_index;
    private ImageView icon_now;
    private RecyclerView hourly,daily;
    private Toolbar toolbar;
    private double lon = 0.0;
    private double lat = 0.0;
    private DB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp_now = (TextView) findViewById(R.id.temp_now);
        description = (TextView) findViewById(R.id.description);
        feels_like = (TextView) findViewById(R.id.feels_like);
        city = (TextView) findViewById(R.id.city);
        icon_now = (ImageView)findViewById(R.id.weather_icon_now);
        sunrise = (TextView)findViewById(R.id.sunrise);
        sunset = (TextView)findViewById(R.id.sunset);
        wind = (TextView)findViewById(R.id.wind_speed);
        humidity = (TextView)findViewById(R.id.humidity);
        pressure = (TextView)findViewById(R.id.pressure);
        uv_index = (TextView)findViewById(R.id.uv_index);
        toolbar  = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setLogo(R.drawable.ic_menu);
        View logo = toolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(MainActivity.this, City_ListActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,1);
        }
    });


        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        LocationCity locationCity = new LocationCity(getApplicationContext());
        Location location = locationCity.getLocation();

        if (lon==0.0 && lat==0.0){
            lon = location.getLongitude();
            lat = location.getLatitude();
        }


        GetWeather getweather = new GetWeather();
        getweather.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            int city_id = bundle.getInt("id");
            db = DB.getInstance(this);
            Map<String,Double> map = db.getCityLocationById(city_id);
            lon = map.get("lon");
            lat = map.get("lat");
            GetWeather getweather = new GetWeather();
            getweather.execute();
        }
    }

    private void initRecyclerHourly(ArrayList icon, ArrayList temp, ArrayList time) {
        hourly = (RecyclerView) findViewById(R.id.recyle_view);
        hourly.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        hourly.setAdapter(new TimeTempAdapter(this, icon, temp, time));
    }

    private void initRecyclerDaily(ArrayList icon, ArrayList date, ArrayList week,ArrayList day, ArrayList night){
        daily = (RecyclerView) findViewById(R.id.recyle_view1);
        daily.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        daily.setAdapter(new DailyTempAdapter(this, icon, date, week, day, night));
    }

    private class GetWeather extends AsyncTask<String, String, String> {

        String OneClickAPI = "";
        private ArrayList<String> icon = new ArrayList<>();
        private ArrayList<String> temp = new ArrayList<>();
        private ArrayList<String> time = new ArrayList<>();
        private ArrayList<String> icon_daily =  new ArrayList<>();
        private ArrayList<String> date =  new ArrayList<>();
        private ArrayList<String> week_day =  new ArrayList<>();
        private ArrayList<String> day_temp =  new ArrayList<>();
        private ArrayList<String> night_temp =  new ArrayList<>();


        private String openConnection() {
            String result = "";
            try {

                URL url = new URL(OneClickAPI);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result = result + line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("info", result);
            return result;
        }

        @Override
        protected String doInBackground(String... params) {

            OneClickAPI = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&appid=9d9d0f7fac113b92d0477b4759a9aad5&units=metric",lat,lon);

            return openConnection();
        }

        @Override
        protected void onPostExecute(String result) {
            String t = "";
            try {
                JSONObject object = new JSONObject(result);
                JSONArray list = (JSONArray) object.getJSONArray("hourly");
                city.setText(object.getString("timezone"));

                for (int i = 0; i < 24; i++) {
                    temp.add(list.getJSONObject(i).getString("temp").concat("\u2103"));

                    long hour = list.getJSONObject(i).getInt("dt") * 1000L;
                    Date date = new Date(hour);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    time.add(simpleDateFormat.format(date));
                    icon.add(String.format("http://openweathermap.org/img/wn/%s@2x.png",
                            list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")));
                }
                initRecyclerHourly(icon, temp, time);

                JSONArray daily_list = (JSONArray) object.getJSONArray("daily");
                SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("d MMMM");
                SimpleDateFormat simpleDateFormatWeek = new SimpleDateFormat("EEEE");
                Date dateMounth = new Date();
                for (int i=0; i < 7; i++){
                    icon_daily.add(String.format("http://openweathermap.org/img/wn/%s@2x.png",
                            daily_list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")));
                    dateMounth.setTime(daily_list.getJSONObject(i).getLong("dt") * 1000L);
                    date.add(simpleDateFormatDate.format(dateMounth));
                    week_day.add(simpleDateFormatWeek.format(dateMounth));
                    day_temp.add(daily_list.getJSONObject(i).getJSONObject("temp").getString("day").concat("\u2103"));
                    night_temp.add(daily_list.getJSONObject(i).getJSONObject("temp").getString("night").concat("\u2103"));
                }
                initRecyclerDaily(icon_daily,date,week_day,day_temp,night_temp);

                temp_now.setText(object.getJSONObject("current").getString("temp").concat("\u2103"));
                String image = String.format("http://openweathermap.org/img/wn/%s@2x.png",
                        object.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon"));
                Picasso.get().load(image).into(icon_now);
                String text = "Feels like " + object.getJSONObject("current").getString("feels_like").concat("\u2103");
                feels_like.setText(text);
                long hour = object.getJSONObject("current").getLong("sunrise") * 1000L;
                Date dateTime = new Date(hour);
                SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
                sunrise.setText(simpleDateFormatTime.format(dateTime));
                dateTime.setTime(object.getJSONObject("current").getLong("sunset") * 1000L);
                sunset.setText(simpleDateFormatTime.format(dateTime));
                wind.setText(object.getJSONObject("current").getString("wind_speed").concat("m/s"));
                humidity.setText(object.getJSONObject("current").getString("humidity").concat("%"));
                pressure.setText(object.getJSONObject("current").getString("pressure").concat("hPa"));
                uv_index.setText(object.getJSONObject("current").getString("uvi"));
                description.setText(object.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
