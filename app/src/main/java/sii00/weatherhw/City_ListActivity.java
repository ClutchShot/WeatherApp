package sii00.weatherhw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class City_ListActivity extends AppCompatActivity {
    private ListView listView;
    private Toolbar toolbar;
    private ImageButton delete;
    private DB db;
    private List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        listView = (ListView)findViewById(R.id.list_cities);
        toolbar = (Toolbar)findViewById(R.id.toolbar1);
        //delete = (ImageButton)findViewById(R.id.delete);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setLogo(R.drawable.ic_arrow);
        View logo = toolbar.getChildAt(0);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(City_ListActivity.this, MainActivity.class);
                finish();
            }});

        initListOfCities();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                int id = (int) map.get(i).get("id");
                bundle.putInt("id", id);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city,menu);
        return true;
    }

    private void initListOfCities(){
        db = DB.getInstance(this);
        map = db.queryAllCities();
        CityListAdapter cityListAdapter = new CityListAdapter(map, City_ListActivity.this);
        listView.setAdapter( cityListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0)
            initListOfCities();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Intent i = new Intent(this, Search_Activity.class);
                this.startActivityForResult(i,0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class CityListAdapter extends BaseAdapter {
        private List<Map<String, Object>> list;
        private LayoutInflater inflater = null;
        private Context context;
        private DB db;

        public CityListAdapter(List<Map<String, Object>> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return getItemId(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertview, ViewGroup viewGroup) {
            View view = convertview;
            if (view==null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.city_list,null);

            }

            TextView city_name = (TextView)view.findViewById(R.id.city_name);
            ImageButton delete = (ImageButton) view.findViewById(R.id.delete);

            city_name.setText((String) list.get(i).get("city"));
            delete.setFocusable(false);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db = DB.getInstance(City_ListActivity.this);
                    db.deleteDataById((int)list.get(i).get("id"));
                    initListOfCities();
                }
            });
            return view;
        }
    }

}