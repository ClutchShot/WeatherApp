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

public class TimeTempAdapter extends RecyclerView.Adapter<TimeTempAdapter.Viewholder> {
    private ArrayList<String> icon =  new ArrayList<>();
    private ArrayList<String> temp =  new ArrayList<>();
    private ArrayList<String> time =  new ArrayList<>();
    private Context mContext;


    public TimeTempAdapter(Context context,ArrayList<String> icon, ArrayList<String> temp, ArrayList<String> time) {
        this.icon = icon;
        this.temp = temp;
        this.time = time;
        this.mContext = context;
    }

    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ImageView icontest;
//        TextView temptest,timetest;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_temp,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(icon.get(position))
                .into(holder.icontest);
        holder.temptest.setText(temp.get(position));
        holder.timetext.setText(time.get(position));
    }

    @Override
    public int getItemCount() {
        return icon.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        ImageView icontest;
        TextView temptest,timetext;

        public Viewholder(View itemView) {
            super(itemView);
            icontest = itemView.findViewById(R.id.icontest);
            temptest = itemView.findViewById(R.id.temptext);
            timetext = itemView.findViewById(R.id.timetext);
        }
    }
}
