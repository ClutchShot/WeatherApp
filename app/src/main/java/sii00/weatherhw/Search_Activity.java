package sii00.weatherhw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search_Activity extends AppCompatActivity {

    private ListView foundcities_list;
    private EditText input;
    private Toolbar toolbar;
    private DB db;
    private String[] data;
    private String[] keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        input = (EditText)findViewById(R.id.search_city);
        foundcities_list = (ListView)findViewById(R.id.list_found_cities);

        db = DB.getInstance(this);
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setLogo(R.drawable.ic_arrow_l);
        View logo = toolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search_Activity.this, City_ListActivity.class);
                finish();
            }});


        foundcities_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                db.insertData(keys,data);
                Intent intent = getIntent();
                setResult(Activity.RESULT_OK, intent);
                finish();


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.check:
                String city = input.getText().toString();
                GetCity getCity = new GetCity(city);
                getCity.execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetCity extends AsyncTask<String, String, String>{
        private String input_text = "";
        private String CurrentWeather  = "https://api.openweathermap.org/data/2.5/weather?appid=9d9d0f7fac113b92d0477b4759a9aad5&units=metric&q=";


        GetCity(String city){
            CurrentWeather +=city;
        }

        @Override
        protected String doInBackground(String... params) {
            return openConnect();
        }


        private String openConnect() {
            String result = "";
            try {
                URL url = new URL(CurrentWeather);
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
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject object = new JSONObject(result);
                String name = object.getString("name");
                String lon = object.getJSONObject("coord").getString("lon");
                String lat = object.getJSONObject("coord").getString("lat");
                data = new String[]{name, lon, lat};
                keys = new String[]{ "city", "lon", "lat"};
                ArrayList<Map<String, Object>> data = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("country", object.getJSONObject("sys").getString("country") );

                data.add(map);

                String[] from = { "name", "country" };
                int[] to = { R.id.found_city, R.id.country};

                SimpleAdapter cities = new SimpleAdapter(getApplication(), data, R.layout.find_city, from, to);
                foundcities_list.setAdapter(cities);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}