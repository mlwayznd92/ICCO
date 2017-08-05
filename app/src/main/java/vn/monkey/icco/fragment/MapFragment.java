package vn.monkey.icco.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.MapInfoAdapter;
import vn.monkey.icco.event.OnInfoWindowElemTouchListener;
import vn.monkey.icco.layout.MapWrapperLayout;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.model.MyItem;
import vn.monkey.icco.model.Station;
import vn.monkey.icco.model.StationResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.GPSTracker;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class MapFragment extends Fragment
        implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<MyItem> {

    private Context context;
    private CustomApplication myApplication;
    private SupportMapFragment mFragment;
    private GoogleMap googleMap;
    private View mView, mMapInfo;
    private LatLng myLocation;
    private MaterialSearchView searchView;
    private ClusterManager<MyItem> mClusterManager;
    private Button btnDetail, btnAdvice;
    private ProgressBar progressBar;


    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }

    /**
     *
     */
    private void bindView() {
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_maps);
        mFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        LayoutInflater inf = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        mMapInfo = inf.inflate(R.layout.map_info, null);
        btnDetail = (Button) mMapInfo.findViewById(R.id.btnDetail);
        btnAdvice = (Button) mMapInfo.findViewById(R.id.btnAdvice);
    }

    /**
     *
     */
    private void init() {
        askPermission();
        searchListener();
        mFragment.getMapAsync(this);
    }


    /**
     * ask permission access loction
     */
    private void askPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap mGoogleMap) {
        googleMap = mGoogleMap;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            GPSTracker gps = new GPSTracker(getActivity());
            if (gps.canGetLocation()) {
                if (gps.getLocation() != null)
                    myLocation = new LatLng(gps.getLatitude(), gps.getLongitude());
            } else if (!Manager.isChooseGPS) {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }
        mClusterManager = new ClusterManager<MyItem>(context, googleMap);
        mClusterManager.setRenderer(new MyItemRenderer());
        mClusterManager.setOnClusterClickListener(this);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        setButtonListener();
        getStations();
    }

    /**
     *
     */
    private void setButtonListener() {

        MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) mView
                .findViewById(R.id.map_relative_layout);

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(googleMap, Util.getPixelsFromDp(context, 39 + 20));

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        OnInfoWindowElemTouchListener btnInfoListener = new OnInfoWindowElemTouchListener(
                btnDetail) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.flContainer, new MapDetailFragment(marker))
                        .addToBackStack(null).commit();
                searchView.closeSearch();
            }
        };

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        OnInfoWindowElemTouchListener btnAdviceListener = new OnInfoWindowElemTouchListener(
                btnAdvice) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.flContainer, new MapAdviceFragment())
                        .addToBackStack(null).commit();
                searchView.closeSearch();
            }
        };

        btnDetail.setOnTouchListener(btnInfoListener);
        btnAdvice.setOnTouchListener(btnAdviceListener);
        googleMap.setInfoWindowAdapter(
                new MapInfoAdapter(btnInfoListener, btnAdviceListener, mapWrapperLayout, mMapInfo,
                        myApplication));
    }


    /**
     * call api get stations
     */
    private void getStations() {
        // check time reload maps
        if (Manager.MAPS != null && Manager.MAPS.size() > 0 && Manager.LAST_RELOAD_MAPS != null &&
                System.currentTimeMillis() - Manager.LAST_RELOAD_MAPS < AppConfig.TIME_RELOAD_MAPS) {
            prepareLocations(null, false);
            return;
        }
        Manager.LAST_RELOAD_MAPS = System.currentTimeMillis();
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<StationResponse> call = myApplication.apiService.getStations(headerAuthen);
        call.enqueue(new Callback<StationResponse>() {
            @Override
            public void onResponse(Call<StationResponse> call, Response<StationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    List<Station> stations = new ArrayList<Station>();
                    for (StationResponse.Item item : response.body().data.items) {
                        if (item.latitude == null || item.longtitude == null) continue;
                        Station station = new Station();
                        station.setId(item.id);
                        station.setProvinceName(item.province_name);
                        station.setStationName(item.station_name);
                        station.setLatitude(item.latitude);
                        station.setLongtitude(item.longtitude);
                        stations.add(station);
                    }
                    // bin to map
                    prepareLocations(stations, true);
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<StationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     * search
     *
     * @param keyword
     */
    public void search(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<StationResponse> call = myApplication.apiService.searchStations(headerAuthen, keyword);
        call.enqueue(new Callback<StationResponse>() {
            @Override
            public void onResponse(Call<StationResponse> call, Response<StationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    List<Station> stations = new ArrayList<Station>();
                    for (StationResponse.Item item : response.body().data.items) {
                        if (item.latitude == null || item.longtitude == null) continue;
                        Station station = new Station();
                        station.setId(item.id);
                        station.setProvinceName(item.province_name);
                        station.setDistrictName(item.district_name);
                        station.setStationName(item.station_name);
                        station.setLatitude(item.latitude);
                        station.setLongtitude(item.longtitude);
                        stations.add(station);
                    }
                    // bin to map
                    prepareLocationsSearch(stations);
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<StationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     * cluster map
     */
    private void prepareLocations(List<Station> stations, boolean isLoad) {
        // put data to maps
        if (isLoad) {
            Manager.MAPS = new HashMap<String, Location>();
            for (Station station : stations) {
                Location location = new Location();
                location.setId(station.getId());
                location.setLatitude(Double.valueOf(station.getLatitude()));
                location.setLongitude(Double.valueOf(station.getLongtitude()));
                location.setTitle(station.getStationName());
                location.setLocationLv1(String.valueOf(station.getProvinceName()));
                location.setLocationLv2(station.getStationName());
                Manager.MAPS.put(location.getLatitude() + "_" + location.getLongitude(), location);
            }
        }

        // check size
        if (Manager.MAPS.size() <= 0) return;
        if (googleMap != null) googleMap.clear();
        if (mClusterManager != null) mClusterManager.clearItems();

        //cluster
        boolean isSetZoomLocation = false;
        List<String> suggests = new ArrayList<>();
        for (Location location : Manager.MAPS.values()) {
            suggests.add(location.getTitle());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MyItem myItem = new MyItem(latLng, location.getTitle());
            mClusterManager.addItem(myItem);
            if (!isSetZoomLocation) {
                isSetZoomLocation = true;
                googleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(myLocation != null ? myLocation : latLng, 8));
            }
        }
        searchView.setSuggestions(suggests.toArray(new String[suggests.size()]));
        searchOnClickListener();
        mClusterManager.cluster();
    }

    /**
     * cluster map
     */
    private void prepareLocationsSearch(List<Station> stations) {
        if (googleMap != null) googleMap.clear();
        if (mClusterManager != null) mClusterManager.clearItems();
        // check size
        if (stations.size() <= 0) {
            Util.showToastMessage(myApplication, getString(R.string.search_not_found));
            return;
        }
        //        if (stations.size() <= 0) {
        //            prepareLocations(null, false);
        //            return;
        //        }
        //cluster
        boolean isSetZoomLocation = false;
        for (Station station : stations) {
            Location location = new Location();
            location.setId(station.getId());
            location.setLatitude(Double.valueOf(station.getLatitude()));
            location.setLongitude(Double.valueOf(station.getLongtitude()));
            location.setTitle(station.getStationName());
            location.setLocationLv1(String.valueOf(station.getProvinceName()));
            location.setLocationLv2(station.getStationName());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MyItem myItem = new MyItem(latLng, location.getTitle());
            mClusterManager.addItem(myItem);
            if (!isSetZoomLocation) {
                isSetZoomLocation = true;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
            }
        }
        mClusterManager.cluster();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setIcon(R.drawable.ic_search_black_24dp);
    }


    /**
     *
     */
    private void searchOnClickListener() {
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String query = (String) parent.getItemAtPosition(position);
                searchView.setQuery(query, true);
                searchView.closeSearch();
            }
        });
    }

    /**
     *
     */
    private void searchListener() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                searchView.dismissSuggestions();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    /**
     *
     */
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        } catch (Exception ex) {
            Log.e(KeyConstant.APP_CODE, "", ex);
        }

        return true;
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class MyItemRenderer extends DefaultClusterRenderer<MyItem> {

        public MyItemRenderer() {
            super(context.getApplicationContext(), googleMap, mClusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
