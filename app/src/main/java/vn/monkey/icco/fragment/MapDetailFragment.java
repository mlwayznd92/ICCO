package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.WeatherDayAdapter;
import vn.monkey.icco.event.RecyclerTouchListener;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class MapDetailFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private RecyclerView mRecyclerView;
    private Marker marker;
    private List<Location> events;
    private List<Location> histories;
    private WeatherDayAdapter mRecyclerAdapter;
    private TextView tvAddress, tvDate, tvWtTemp, tvDescription, tvWindSpeed, tvWindDirection,
            tvAmountOfRain;
    private ImageView imgWtDay;
    private GraphView gvRain, gvTemperature;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    public MapDetailFragment(Marker marker) {
        this.marker = marker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_detail, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        prepareData(marker);
        return mView;
    }


    /**
     *
     */
    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_weather_day);
        tvAddress = (TextView) mView.findViewById(R.id.tvAddress);
        tvDate = (TextView) mView.findViewById(R.id.tvDate);
        tvWtTemp = (TextView) mView.findViewById(R.id.tvTemperature);
        tvDescription = (TextView) mView.findViewById(R.id.tvDescription);
        tvAmountOfRain = (TextView) mView.findViewById(R.id.tvAmoutOfRain);
        tvWindSpeed = (TextView) mView.findViewById(R.id.tvWindSpeed);
        tvWindDirection = (TextView) mView.findViewById(R.id.tvWindDirection);
        gvRain = (GraphView) mView.findViewById(R.id.graphRain);
        gvTemperature = (GraphView) mView.findViewById(R.id.graphTemperature);
        imgWtDay = (ImageView) mView.findViewById(R.id.imgWtDay);
    }

    /**
     *
     */
    private void init() {
        initToolbar();
        clickDetailListener();
        // init recycler view
        events = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new WeatherDayAdapter(events, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * @param gvTemperature
     * @param gvRain
     */
    public void initGraph(GraphView gvTemperature, GraphView gvRain) {

        if (histories == null || histories.size() < 2) return;

        DataPoint[] highs = new DataPoint[histories.size()];
        DataPoint[] lows = new DataPoint[histories.size()];
        DataPoint[] rains = new DataPoint[histories.size()];
        String labelDate[] = new String[histories.size()];

        int tMax = 0;
        int tMin = histories.get(0).gettMin();
        int rainMax = 0;
        int rainMin = histories.get(0).getPrecipitation();
        for (int i = 0; i < histories.size(); i++) {
            Location event = histories.get(i);
            if (tMax < event.gettMax()) tMax = event.gettMax();
            if (tMin > event.gettMin()) tMin = event.gettMin();
            if (rainMax < event.getPrecipitation()) rainMax = event.getPrecipitation();
            if (rainMin > event.getPrecipitation()) rainMin = event.getPrecipitation();
            labelDate[i] = (i == 0 || i == (histories.size() - 1)) ?
                    Util.getDateFromLong(event.getTimestamp(), KeyConstant.DATE_FORMAT_DD_MM,
                            true) : "";
            highs[i] = new DataPoint(i, event.gettMax());
            lows[i] = new DataPoint(i, event.gettMin());
            rains[i] = new DataPoint(i, event.getPrecipitation());
        }

        // weather graph
        LineGraphSeries<DataPoint> lowGraph = new LineGraphSeries<>(lows);
        lowGraph.setTitle(getString(R.string.low));
        lowGraph.setDrawDataPoints(true);
        gvTemperature.addSeries(lowGraph);

        LineGraphSeries<DataPoint> highGraph = new LineGraphSeries<>(highs);
        highGraph.setTitle(getString(R.string.high));
        highGraph.setColor(ContextCompat.getColor(myApplication, R.color.red));
        highGraph.setDrawDataPoints(true);
        gvTemperature.addSeries(highGraph);

        StaticLabelsFormatter labelYTemp = new StaticLabelsFormatter(gvTemperature);
        labelYTemp.setHorizontalLabels(labelDate);
        gvTemperature.getGridLabelRenderer().setLabelFormatter(labelYTemp);
        gvTemperature.setTitle(
                String.format(getString(R.string.title_temperature_graph), marker.getTitle(),
                        labelDate[0], labelDate[histories.size() - 1]));

        // rain graph
        BarGraphSeries<DataPoint> rainGraph = new BarGraphSeries<>(rains);
        rainGraph.setSpacing(60);
        gvRain.addSeries(rainGraph);
        StaticLabelsFormatter labelYRain = new StaticLabelsFormatter(gvRain);
        labelYRain.setHorizontalLabels(labelDate);
        gvRain.getGridLabelRenderer().setLabelFormatter(labelYRain);
        gvRain.setTitle(
                String.format(getString(R.string.title_rain_graph), marker.getTitle(), labelDate[0],
                        labelDate[histories.size() - 1]));


        // legend
        gvTemperature.getViewport().setMaxY(tMax + 10);
        gvTemperature.getViewport().setMinY(tMin - 5);
        gvTemperature.getViewport().setYAxisBoundsManual(true);
        gvTemperature.getLegendRenderer().setVisible(true);
        gvTemperature.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        gvRain.getViewport().setMaxY(rainMax + 2);
        gvRain.getViewport().setMinY(rainMin - 2);
        gvRain.getViewport().setYAxisBoundsManual(true);
    }


    private void prepareData(Marker marker) {

        String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
        Location location = Manager.MAPS.get(key);
        events.add(location);
        events.addAll(location.getEvents());
        histories = location.getHistories();
        mRecyclerAdapter.notifyDataSetChanged();
        initGraph(gvTemperature, gvRain);

        //set default view
        Picasso.with(myApplication).load(location.getWtImage()).into(imgWtDay);
        tvAddress.setText(location.getLocationLv2());
        tvDate.setText(Util.getDayMonthYear(myApplication, location.getTimestamp()));
        tvWtTemp.setText(location.getWtTemp().replace(" ⁰C", ""));
        tvAmountOfRain.setText(location.getAmoutOfRain());
        tvWindSpeed.setText(location.getWindSpeed());
        tvWindDirection.setText(location.getWindDirection());
        tvDescription.setText(location.getWtDescription());
    }

    /**
     *
     */
    private void initToolbar() {
        setHasOptionsMenu(true);
        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                toolbar.setNavigationIcon(R.drawable.menu_grey_36x36);
                final DrawerLayout drawer =
                        (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void clickDetailListener() {
        mRecyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity().getApplicationContext(), mRecyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Location location = events.get(position);
                                tvDate.setText(Util.getDayMonthYear(myApplication,
                                        location.getTimestamp()));
                                Picasso.with(myApplication).load(location.getWtImage())
                                        .into(imgWtDay);
                                tvWtTemp.setText(location.getWtTemp().replace(" ⁰C", ""));
                                tvAmountOfRain.setText(location.getAmoutOfRain());
                                tvWindSpeed.setText(location.getWindSpeed());
                                tvWindDirection.setText(location.getWindDirection());
                                tvDescription.setText(location.getWtDescription());
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                            }
                        }));
    }

}
