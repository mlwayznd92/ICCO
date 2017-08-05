package vn.monkey.icco.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import vn.monkey.icco.model.Location;
import vn.monkey.icco.util.KeyConstant;

public class InfoWindowRefresher implements Callback {
    private Marker maker;
    private Context context;
    private ImageView imageView;
    private String url;

    public InfoWindowRefresher(Context context, Marker maker, ImageView imageView, String url) {
        this.context = context;
        this.maker = maker;
        this.imageView = imageView;
        this.url = url;
    }

    @Override
    public void onSuccess() {
        maker.showInfoWindow();
        if (maker != null && maker.isInfoWindowShown()) {
            Picasso.with(context).load(url).into(imageView);
            maker.showInfoWindow();
        }
    }

    @Override
    public void onError() {
    }
}