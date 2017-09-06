package vn.monkey.icco.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.SaleResponse;
import vn.monkey.icco.model.SoldResponse;
import vn.monkey.icco.model.TotalQuantityResponse;
import vn.monkey.icco.model.TypeCoffeeResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class SaleFragment extends Fragment {

    private int PLACE_PICKER_REQUEST = 1;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private Map<String, TotalQuantityResponse.Item> totalQualitiesRes;
    private Map<String, TypeCoffeeResponse.Item> typecoffeesRes;
    private Map<String, SoldResponse.Item> soldsRes;
    private List<String> qualities;
    private List<String> typeCoffees;
    private List<String> solds;
    private ArrayAdapter totalQualitiesAdapter;
    private ArrayAdapter typeCoffeesAdapter;
    private ArrayAdapter soldsAdapter;
    private ProgressBar progressBar;
    private TextView tvLocation, tvPrice;
    private Spinner spinnerTotalQuality, spinnerTypeCoffee, spinnerSolds;
    private ViewPager viewPager;
    private String location;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_sale, container, false);
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
        tvLocation = (TextView) mView.findViewById(R.id.tvLocationExchange);
        tvPrice = (TextView) mView.findViewById(R.id.tvPrice);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_exchange);
        ImageView imgLocationExchange = (ImageView) mView.findViewById(R.id.imgLocationExchange);
        imgLocationExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //
        Button btnExchange = (Button) mView.findViewById(R.id.btnExchange);
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) return;
                int totalQualityId = -1;
                int typeCoffee = -1;
                int soldId = -1;
                String locationName = "";
                int price = 0;
                try {
                    totalQualityId =
                            totalQualitiesRes.get(spinnerTotalQuality.getSelectedItem() + "").id;
                    typeCoffee = typecoffeesRes.get(spinnerTypeCoffee.getSelectedItem() + "").id;
                    soldId = soldsRes.get(spinnerSolds.getSelectedItem() + "").id;
                    locationName = tvLocation.getText().toString();
                    price = Integer.valueOf(tvPrice.getText().toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                exchange(totalQualityId, soldId, typeCoffee, location, locationName, price);
            }
        });

        // total quality
        qualities = new ArrayList<>();
        spinnerTotalQuality = (Spinner) mView.findViewById(R.id.total_quality_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        totalQualitiesAdapter =
                new ArrayAdapter<String>(myApplication, R.layout.spinner_item, qualities);
        // Specify the layout to use when the list of choices appears
        totalQualitiesAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTotalQuality.setAdapter(totalQualitiesAdapter);

        // type coffee
        typeCoffees = new ArrayList<>();
        spinnerTypeCoffee = (Spinner) mView.findViewById(R.id.type_coffee_spinner);
        typeCoffeesAdapter =
                new ArrayAdapter<String>(myApplication, R.layout.spinner_item, typeCoffees);
        typeCoffeesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeCoffee.setAdapter(typeCoffeesAdapter);

        // solds
        solds = new ArrayList<>();
        spinnerSolds = (Spinner) mView.findViewById(R.id.sold_spinner);
        soldsAdapter = new ArrayAdapter<String>(myApplication, R.layout.spinner_item, solds);
        soldsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSolds.setAdapter(soldsAdapter);
    }

    /**
     *
     */
    private void init() {
        initToolbar();
        getTotalQuantities();
        getTypeCoffees();
        getSolds();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(getActivity().getApplicationContext(), data);
            if (resultCode == RESULT_OK) {
                NumberFormat formatter = new DecimalFormat("#0.00");
                LatLng latLng = place.getLatLng();
                String address = place.getAddress().toString();
                location = formatter.format(latLng.latitude) + "," +
                        formatter.format(latLng.longitude);
                if (TextUtils.isEmpty(address)) {
                    tvLocation.setText(location);
                } else {
                    address = address.replace(getString(R.string.un_named_road) + ", ", "");
                    tvLocation.setText(address);
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     *
     */
    private void getTotalQuantities() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<TotalQuantityResponse> call = myApplication.apiService.getTotalQuantity(headerAuthen);
        call.enqueue(new Callback<TotalQuantityResponse>() {
            @Override
            public void onResponse(Call<TotalQuantityResponse> call,
                                   Response<TotalQuantityResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    totalQualitiesRes = new HashMap<String, TotalQuantityResponse.Item>();
                    for (TotalQuantityResponse.Item item : response.body().data.items) {
                        String key = item.min_total_quality + " - " + item.max_total_quality;
                        qualities.add(key);
                        totalQualitiesRes.put(key, item);
                    }
                    totalQualitiesAdapter.notifyDataSetChanged();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<TotalQuantityResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     * get type coffe
     */
    private void getTypeCoffees() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<TypeCoffeeResponse> call = myApplication.apiService.getTypeCoffee(headerAuthen);
        call.enqueue(new Callback<TypeCoffeeResponse>() {
            @Override
            public void onResponse(Call<TypeCoffeeResponse> call,
                                   Response<TypeCoffeeResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    typecoffeesRes = new HashMap<String, TypeCoffeeResponse.Item>();
                    for (TypeCoffeeResponse.Item item : response.body().data.items) {
                        typeCoffees.add(item.name);
                        typecoffeesRes.put(item.name, item);
                    }
                    typeCoffeesAdapter.notifyDataSetChanged();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<TypeCoffeeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     * get solds
     */
    private void getSolds() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<SoldResponse> call = myApplication.apiService.getSold(headerAuthen);
        call.enqueue(new Callback<SoldResponse>() {
            @Override
            public void onResponse(Call<SoldResponse> call, Response<SoldResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    soldsRes = new HashMap<String, SoldResponse.Item>();
                    for (SoldResponse.Item item : response.body().data.items) {
                        String key = item.min_sold + "-" + item.max_sold;
                        solds.add(key);
                        soldsRes.put(key, item);
                    }
                    soldsAdapter.notifyDataSetChanged();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<SoldResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     * validate input
     *
     * @return
     */
    private boolean validateInput() {
        String price = tvPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            Util.showToastMessage(myApplication, getString(R.string.require_price));
            return false;
        }

        try {
            Integer.valueOf(price);
        } catch (Exception ex) {
            Util.showToastMessage(myApplication, getString(R.string.require_type_integer));
            return false;
        }
        return true;
    }

    /**
     * exchange
     *
     * @param totalQuantityId
     * @param soldId
     * @param typeCoffee
     * @param location
     * @param price
     */
    private void exchange(int totalQuantityId, int soldId, int typeCoffee, String location,
                          String locationName, int price) {

        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<SaleResponse> call = myApplication.apiService
                .exchange(headerAuthen, totalQuantityId, soldId, typeCoffee, location, locationName,
                        price);
        call.enqueue(new Callback<SaleResponse>() {
            @Override
            public void onResponse(Call<SaleResponse> call, Response<SaleResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    Util.showToastMessage(myApplication, response.body().message);
                    SaleTabsFragment.isReloadSaleTrans = true;
                    tvPrice.setText("");
                    tvLocation.setText(getString(R.string.choose_location));
                    //                    viewPager.setCurrentItem(2);
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<SaleResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        toolbar.setNavigationIcon(R.drawable.menu_grey_36x36);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    /**
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
