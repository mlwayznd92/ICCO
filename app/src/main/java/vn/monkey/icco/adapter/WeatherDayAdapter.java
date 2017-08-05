package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import vn.monkey.icco.R;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Util;


/**
 * Created by Mlwayz on 5/28/2017.
 */

public class WeatherDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView mRecyclerView;
    private List<Location> events;

    // Provide a suitable constructor (depends on the kind of dataset)
    public WeatherDayAdapter(List<Location> events, RecyclerView mRecyclerView) {
        this.events = events;
        this.mRecyclerView = mRecyclerView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_weather_day, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Location location = events.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        long timestamp = location.getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timestamp * 1000);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        Picasso.with(myViewHolder.context).load(location.getWtImage()).into(myViewHolder.imgWtDay);
        myViewHolder.tvDate.setText(Util.getDateDayMonth(myViewHolder.context, timestamp));
        myViewHolder.tvHour.setText(hour > 12 ? (hour - 12) + " PM" : hour + " AM");
        myViewHolder.tvHigh.setText(String.format(myViewHolder.context.getString(R.string.do_),
                String.valueOf(location.gettMax())));
        myViewHolder.tvLow.setText(String.format(myViewHolder.context.getString(R.string.do_),
                String.valueOf(location.gettMin())));
    }

    /**
     *
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvHigh, tvLow, tvHour;
        public ImageView imgWtDay;
        public Context context;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvHour = (TextView) view.findViewById(R.id.tv_hour);
            tvHigh = (TextView) view.findViewById(R.id.tvHigh);
            tvLow = (TextView) view.findViewById(R.id.tvLow);
            imgWtDay = (ImageView) view.findViewById(R.id.imgWtDay);
            context = view.getContext();
        }
    }
}
