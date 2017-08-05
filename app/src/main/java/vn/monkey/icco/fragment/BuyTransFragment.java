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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.BuyTransAdapter;
import vn.monkey.icco.adapter.ViewPagerAdapter;
import vn.monkey.icco.model.BuyTrans;
import vn.monkey.icco.model.BuyTransResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class BuyTransFragment extends Fragment {

    private List<BuyTrans> buyTranses;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private BuyTransAdapter mRecyclerAdapter;
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
        mView = inflater.inflate(R.layout.fragment_buy_trans, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        buyTranses = new ArrayList<>();
        bindView();
        init();
        return mView;
    }


    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_buy);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_buy);
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
        mRecyclerAdapter = new BuyTransAdapter(buyTranses, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        getBuyTransData();
    }

    /**
     *
     */
    private void getBuyTransData() {
        // set load first time
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<BuyTransResponse> call = myApplication.apiService
                .getBuyTrans(headerAuthen);
        call.enqueue(new Callback<BuyTransResponse>() {
            @Override
            public void onResponse(
                    Call<BuyTransResponse> call, Response<BuyTransResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    buyTranses.clear();
                    for (BuyTransResponse.Item item : response.body().data.items) {
                        BuyTrans trans = new BuyTrans();
                        trans.setId(item.id);
                        trans.setSubscriberId(item.subscriber_id);
                        trans.setCreatedAt(item.created_at);
                        trans.setUpdatedAt(item.updated_at);
                        trans.setCoffee(item.coffee);
                        trans.setPriceBuy(item.price);
                        trans.setTotalQuantity(item.total_quantity);
                        buyTranses.add(trans);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BuyTransResponse> call, Throwable t) {
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
        if (isVisibleToUser && BuyTabsFragment.isReloadBuyTrans) {
            getBuyTransData();
            BuyTabsFragment.isReloadBuyTrans = false;
            viewPagerAdapter.notifyDataSetChanged();
        }
    }

    public void setViewPagerAdapter(ViewPagerAdapter viewPagerAdapter) {
        this.viewPagerAdapter = viewPagerAdapter;
    }
}
