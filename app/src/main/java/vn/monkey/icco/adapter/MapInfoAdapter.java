package vn.monkey.icco.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.event.OnInfoWindowElemTouchListener;
import vn.monkey.icco.layout.MapWrapperLayout;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.model.WeatherDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by Mlwayz on 6/12/2017.
 */

public class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mView;
    private MapWrapperLayout mapWrapperLayout;
    private OnInfoWindowElemTouchListener btnAdviceListener, btnInfoListener;
    private TextView wtLocationLv1, wtLocationLv2, wtTemp, wtDescription, wtDate, temp,
            windSpeed, amoutOfRain, windDirection, tvTransparent;
    private ImageView wtImage;
    private CustomApplication myApplication;
    private Location location;
    private String keyMarkerLast;
    private LinearLayout llMainInfo;

    public MapInfoAdapter(
            OnInfoWindowElemTouchListener btnInfoListener,
            OnInfoWindowElemTouchListener btnAdviceListener, MapWrapperLayout mapWrapperLayout,
            View mView, CustomApplication myCustomApplication) {
        this.btnAdviceListener = btnAdviceListener;
        this.btnInfoListener = btnInfoListener;
        this.mView = mView;
        this.mapWrapperLayout = mapWrapperLayout;
        this.myApplication = myCustomApplication;
        keyMarkerLast = "";
        bindView();
    }

    /**
     * bind view
     */
    private void bindView() {
        wtLocationLv1 = (TextView) mView.findViewById(R.id.wt_location_lv1);
        wtLocationLv2 = (TextView) mView.findViewById(R.id.wt_location_lv2);
        wtImage = (ImageView) mView.findViewById(R.id.wt_image);
        wtTemp = (TextView) mView.findViewById(R.id.wt_temp);
        wtDescription = (TextView) mView.findViewById(R.id.wt_description);
        wtDate = (TextView) mView.findViewById(R.id.wt_date);
        temp = (TextView) mView.findViewById(R.id.tvTemperature);
        windSpeed = (TextView) mView.findViewById(R.id.tvWindSpeed);
        amoutOfRain = (TextView) mView.findViewById(R.id.tvAmoutOfRain);
        windDirection = (TextView) mView.findViewById(R.id.tvWindDirection);
        tvTransparent = (TextView) mView.findViewById(R.id.transparent_view);
        llMainInfo = (LinearLayout) mView.findViewById(R.id.llMainInfo);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
        if (!key.equals(keyMarkerLast)) {
            llMainInfo.setVisibility(View.GONE);
            tvTransparent.setText(myApplication.getString(R.string.searching));
            tvTransparent.setVisibility(View.VISIBLE);
            getWeatherDetail(marker);

        }
        btnInfoListener.setMarker(marker);
        btnAdviceListener.setMarker(marker);
        // We must call this to set the current marker and infoWindow references
        // to the MapWrapperLayout
        mapWrapperLayout.setMarkerWithInfoWindow(marker, mView);
        return mView;
    }


    /**
     *
     */
    private void setView(Marker marker) {
        // set view
        if (location == null) return;
        tvTransparent.setVisibility(View.GONE);
        llMainInfo.setVisibility(View.VISIBLE);
        wtLocationLv1.setText(location.getLocationLv1());
        wtLocationLv2.setText(location.getLocationLv2());
        wtTemp.setText(location.getWtTemp());
        wtDescription.setText(location.getWtDescription());
        wtDate.setText(Util.getDateFromLong(location.getTimestamp(),
                KeyConstant.DATE_FORMAT_DD_MM_YYYY, true));
        temp.setText(location.getTemp());
        windSpeed.setText(location.getWindSpeed());
        amoutOfRain.setText(location.getAmoutOfRain());
        windDirection.setText(location.getWindDirection());
        Picasso.with(myApplication).load(location.getWtImage()).into(wtImage,
                new InfoWindowRefresher(myApplication, marker, wtImage, location.getWtImage()));
    }

    /**
     *
     */
    private void getWeatherDetail(final Marker marker) {
        final String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
        location = Manager.MAPS.get(key);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<WeatherDetailResponse> call = myApplication.apiService.getWeatherDetail(headerAuthen,
                location.getId());

        call.enqueue(new Callback<WeatherDetailResponse>() {
            @Override
            public void onResponse(
                    Call<WeatherDetailResponse> call, Response<WeatherDetailResponse> response) {
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    keyMarkerLast = key;
                    WeatherDetailResponse.Item items = response.body().data.items;
                    if (items == null) {
                        tvTransparent.setText(marker.getTitle());
                        marker.showInfoWindow();
                        return;
                    }

                    List<Location> events = new ArrayList<Location>();
                    for (WeatherDetailResponse.Item event : response.body().data.events) {
                        Location loc = new Location();
                        loc.setWtImage(event.image);
                        loc.setWtTemp(event.t_average);
                        loc.setWtDescription(event.content);
                        loc.settMin(event.tmin);
                        loc.settMax(event.tmax);
                        loc.setTemp(event.tmin + "⁰C - " + event.tmax + "⁰C");
                        loc.setAmoutOfRain(event.precipitation + event.precipitation_unit);
                        loc.setWindSpeed(event.wndspd_km_h);
                        loc.setWindDirection(event.wnddir);
                        loc.setTimestamp(event.timestamp);
                        loc.setPrecipitation(items.precipitation);
                        events.add(loc);
                    }

                    location.setEvents(events);
                    location.setWtImage(items.image);
                    location.setWtTemp(items.t_average);
                    location.setWtDescription(items.content);
                    location.setTemp(items.tmin + "⁰C - " + items.tmax + "⁰C");
                    location.setAmoutOfRain(items.precipitation + items.precipitation_unit);
                    location.setWindSpeed(items.wndspd_km_h);
                    location.setWindDirection(items.wnddir);
                    location.setTimestamp(items.timestamp);
                    location.settMin(items.tmin);
                    location.settMax(items.tmax);
                    location.setPrecipitation(items.precipitation);
                    setView(marker);
                    Manager.MAPS.put(key, location);
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<WeatherDetailResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
