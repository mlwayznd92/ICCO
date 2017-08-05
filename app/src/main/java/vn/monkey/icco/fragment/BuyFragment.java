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
import vn.monkey.icco.model.BuyResponse;
import vn.monkey.icco.model.TypeCoffeeResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class BuyFragment extends Fragment {

    private int PLACE_PICKER_REQUEST = 1003;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private Map<String, TypeCoffeeResponse.Item> typecoffeesRes;
    private List<String> typeCoffees;
    private ArrayAdapter typeCoffeesAdapter;
    private ProgressBar progressBar;
    private TextView tvLocation, tvPrice, tvTotalQuantity;
    private Spinner spinnerTypeCoffee;
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
        mView = inflater.inflate(R.layout.fragment_buy, container, false);
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
        tvTotalQuantity = (TextView) mView.findViewById(R.id.tvTotalQuantity);
        tvPrice = (TextView) mView.findViewById(R.id.tvPrice);
        tvLocation = (TextView) mView.findViewById(R.id.tvLocationExchange);
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
        Button btnBuy = (Button) mView.findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) return;
                int totalQuantity = -1;
                int typeCoffee = -1;
                int price = 0;
                String locationName = "";
                try {
                    totalQuantity = Integer.valueOf(tvTotalQuantity.getText().toString());
                    typeCoffee = typecoffeesRes.get(spinnerTypeCoffee.getSelectedItem() + "").id;
                    price = Integer.valueOf(tvPrice.getText().toString());
                    locationName = tvLocation.getText().toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                exchangeBuy(totalQuantity, typeCoffee, location, locationName, price);
            }
        });

        // type coffee
        typeCoffees = new ArrayList<>();
        spinnerTypeCoffee = (Spinner) mView.findViewById(R.id.type_coffee_spinner);
        typeCoffeesAdapter =
                new ArrayAdapter<String>(myApplication, R.layout.spinner_item, typeCoffees);
        typeCoffeesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeCoffee.setAdapter(typeCoffeesAdapter);
    }

    /**
     *
     */
    private void init() {
        initToolbar();
        getTypeCoffees();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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
                if (response != null && response.body() != null && response.body().success) {
                    typecoffeesRes = new HashMap<String, TypeCoffeeResponse.Item>();
                    for (TypeCoffeeResponse.Item item : response.body().data.items) {
                        typeCoffees.add(item.name);
                        typecoffeesRes.put(item.name, item);
                    }
                    typeCoffeesAdapter.notifyDataSetChanged();
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
     * validate input
     *
     * @return
     */
    private boolean validateInput() {
        String price = tvPrice.getText().toString();
        String totalQuantity = tvTotalQuantity.getText().toString();

        if (TextUtils.isEmpty(totalQuantity)) {
            Util.showToastMessage(myApplication, getString(R.string.require_total_quantity));
            return false;
        }

        try {
            Integer.valueOf(totalQuantity);
        } catch (Exception ex) {
            Util.showToastMessage(myApplication, getString(R.string.require_type_integer));
            return false;
        }

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
     * @param totalQuantity
     * @param typeCoffee
     * @param price
     */
    private void exchangeBuy(int totalQuantity, int typeCoffee, String location,
                             String locationName, int price) {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<BuyResponse> call = myApplication.apiService
                .exchangeBuy(headerAuthen, totalQuantity, typeCoffee, location, locationName,
                        price);
        call.enqueue(new Callback<BuyResponse>() {
            @Override
            public void onResponse(Call<BuyResponse> call, Response<BuyResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    Util.showToastMessage(myApplication, response.body().message);
                    tvPrice.setText("");
                    tvTotalQuantity.setText("");
                    //                    BuyTabsFragment.isReloadBuyTrans = true;
                    //                    viewPager.setCurrentItem(2);
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<BuyResponse> call, Throwable t) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && data != null) {
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
}
