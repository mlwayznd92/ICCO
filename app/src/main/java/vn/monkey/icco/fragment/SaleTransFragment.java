package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.SaleTransAdapter;
import vn.monkey.icco.adapter.ViewPagerAdapter;
import vn.monkey.icco.model.SaleTrans;
import vn.monkey.icco.model.SaleTransResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class SaleTransFragment extends Fragment {

    private List<SaleTrans> exchangeTranses;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private SaleTransAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_sale_trans, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        exchangeTranses = new ArrayList<>();
        bindView();
        init();
        return mView;
    }


    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_sale_trans);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_sale_trans);
    }

    /**
     *
     */
    private void init() {
        // init recycler view
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new SaleTransAdapter(exchangeTranses, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        getSaleTransData();
    }

    /**
     *
     */
    private void getSaleTransData() {
        // set load first time
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<SaleTransResponse> call = myApplication.apiService
                .getExchangeTrans(headerAuthen);
        call.enqueue(new Callback<SaleTransResponse>() {
            @Override
            public void onResponse(
                    Call<SaleTransResponse> call, Response<SaleTransResponse> response) {
                // set load first time
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null && response.body().success) {
                    exchangeTranses.clear();
                    for (SaleTransResponse.Item item : response.body().data.items) {
                        SaleTrans trans = new SaleTrans();
                        trans.setId(item.id);
                        trans.setSubscriberId(item.subscriber_id);
                        try {
                            trans.setLocation(URLDecoder.decode(item.location, "utf-8"));
                        } catch (Exception ex) {
                        }
                        trans.setCreatedAt(item.created_at);
                        trans.setUpdatedAt(item.updated_at);
                        trans.setSold(item.sold);
                        trans.setCoffee(item.coffee);
                        trans.setPrice(item.price);
                        trans.setTotalQuantity(item.total_quantity);
                        exchangeTranses.add(trans);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SaleTransResponse> call, Throwable t) {
                progressBar.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && SaleTabsFragment.isReloadSaleTrans) {
            getSaleTransData();
            SaleTabsFragment.isReloadSaleTrans = false;
            viewPagerAdapter.notifyDataSetChanged();
        }
    }

    public void setViewPagerAdapter(ViewPagerAdapter viewPagerAdapter) {
        this.viewPagerAdapter = viewPagerAdapter;
    }
}
