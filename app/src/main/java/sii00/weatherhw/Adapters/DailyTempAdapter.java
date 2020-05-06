package sii00.weatherhw.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import sii00.weatherhw.R;

public class DailyTempAdapter extends RecyclerView.Adapter<DailyTempAdapter.ViewHolder> {

    private ArrayList<String> icon =  new ArrayList<>();
    private ArrayList<String> date =  new ArrayList<>();
    private ArrayList<String> week_day =  new ArrayList<>();
    private ArrayList<String> day_temp =  new ArrayList<>();
    private ArrayList<String> night_temp =  new ArrayList<>();
    private Context context;

    public DailyTempAdapter(Context context, ArrayList<String> icon, ArrayList<String> date, ArrayList<String> week_day, ArrayList<String> day_temp, ArrayList<String> night_temp) {
        this.icon = icon;
        this.date = date;
        this.week_day = week_day;
        this.day_temp = day_temp;
        this.night_temp = night_temp;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_temp,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(icon.get(position))
                .into(holder.icon);
        holder.date.setText(date.get(position));
        holder.week_day.setText(week_day.get(position));
        holder.temp_day.setText(day_temp.get(position));
        holder.temp_night.setText(night_temp.get(position));
    }


    @Override
    public int getItemCount() {
        return icon.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
    ImageView icon;
    TextView date, week_day, temp_day, temp_night;
        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.daily_icon);
            date = (TextView)itemView.findViewById(R.id.date);
            week_day = (TextView)itemView.findViewById(R.id.week_day);
            temp_day = (TextView)itemView.findViewById(R.id.day_temp);
            temp_night = (TextView)itemView.findViewById(R.id.night_temp);
        }
    }
}
