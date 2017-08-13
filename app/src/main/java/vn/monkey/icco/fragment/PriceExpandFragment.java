package vn.monkey.icco.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.activity.PriceGraphActivity;
import vn.monkey.icco.adapter.PriceExpandAdapter;
import vn.monkey.icco.event.RecyclerTouchListener;
import vn.monkey.icco.model.Price;
import vn.monkey.icco.model.PriceExpand;
import vn.monkey.icco.model.PriceExpandResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class PriceExpandFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private List<PriceExpand> prices;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private ExpandableListView expandableListView;
    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private PriceExpandAdapter priceExpandAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_prices_expand, container, false);
        textView = (TextView) mView.findViewById(R.id.price_date);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_price);
        imageView = (ImageView) mView.findViewById(R.id.imgDateChoose);
        prices = new ArrayList<>();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) mView.findViewById(R.id.expand_prices);
        // Init top level data
        prices = new ArrayList<>();
        priceExpandAdapter = new PriceExpandAdapter(mView.getContext(), prices);
        expandableListView.setAdapter(priceExpandAdapter);
        clickDetailListener();
        initToolbar();
        getPricesData(
                Util.getStringFromDate(new Date(), KeyConstant.DATE_FORMAT_DD_MM_YYYY, false));
    }

    /**
     * @param date
     */
    private void getPricesData(String date) {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(date);
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.getToken());
        Call<PriceExpandResponse> call =
                myApplication.apiService.getPricesExpand(headerAuthen, date);
        call.enqueue(new Callback<PriceExpandResponse>() {
            @Override
            public void onResponse(Call<PriceExpandResponse> call,
                                   Response<PriceExpandResponse> response) {
                progressBar.setVisibility(View.GONE);
                prices.clear();
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (PriceExpandResponse.Item item : response.body().data) {
                        PriceExpand priceExpand = new PriceExpand();
                        priceExpand.setProvinceName(item.province_name);
                        List<Price> priceList = new ArrayList<Price>();
                        for (PriceExpandResponse.PriceItem priceItem : item.price) {
                            Price price = new Price();
                            price.setId(priceItem.id);
                            price.setOrganisationName(priceItem.organisation_name);
                            price.setUnit(priceItem.unit);
                            price.setCreatedDate(priceItem.created_at);
                            price.setCoffeeOldId(priceItem.coffee_old_id);
                            price.setProvinceName(priceItem.province_name);
                            price.setPriceAverage(priceItem.price_average);
                            price.setType(priceItem.type_coffee.name_coffee);
                            price.setCompany(priceItem.type_coffee.company);
                            priceList.add(price);
                        }
                        priceExpand.setPrices(priceList);
                        prices.add(priceExpand);
                    }

                    for (int i = 0; i < prices.size(); i++) {
                        expandableListView.expandGroup(i);
                    }
                    priceExpandAdapter.notifyDataSetChanged();
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<PriceExpandResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar pickup = Calendar.getInstance();
        pickup.set(year, month, dayOfMonth);
        if (pickup.after(Calendar.getInstance())) {
            Util.showToastMessage(myApplication, getString(R.string.over_current_date));
            return;
        }
        month = month + 1;
        String date =
                (dayOfMonth < 10 ? "0" : "") + dayOfMonth + "/" + (month < 10 ? "0" : "") + month +
                        "/" + year;
        textView.setText(date);
        getPricesData(date);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     *
     */
    private void clickDetailListener() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(getActivity(), PriceExpandFragment.this, year, month,
                                day);
                datePickerDialog.show();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view,
                                        int groupPosition, int childPosition, long l) {
                Price price = prices.get(groupPosition).getPrices().get(childPosition);
                Intent intent = new Intent(myApplication, PriceGraphActivity.class);
                intent.putExtra("coffee_old_id", price.getCoffeeOldId());
                intent.putExtra("created_date", price.getCreatedDate());
                startActivity(intent);
                return true;
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
}
