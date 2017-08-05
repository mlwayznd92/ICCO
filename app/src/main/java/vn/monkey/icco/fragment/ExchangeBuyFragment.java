package vn.monkey.icco.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import vn.monkey.icco.activity.ProfileActivity;
import vn.monkey.icco.activity.SignInActivity;
import vn.monkey.icco.activity.TermActivity;
import vn.monkey.icco.adapter.ExchangeBuyAdapter;
import vn.monkey.icco.model.ExchangeBuy;
import vn.monkey.icco.model.ExchangeBuyResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class ExchangeBuyFragment extends Fragment {

    private List<ExchangeBuy> exchangeBuys;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private ExchangeBuyAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    // private Integer LOGIN_FOR_EXCHANGE = 1000;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_exchange_buy, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        exchangeBuys = new ArrayList<>();
        bindView();
        init();
        return mView;
    }


    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_exhange_buy);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_exchange_buy);
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
        mRecyclerAdapter = new ExchangeBuyAdapter(exchangeBuys, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        getExchangeBuyData();
    }

    /**
     *
     */
    private void getExchangeBuyData() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<ExchangeBuyResponse> call = myApplication.apiService
                .getExchangeBuy(headerAuthen);
        call.enqueue(new Callback<ExchangeBuyResponse>() {
            @Override
            public void onResponse(
                    Call<ExchangeBuyResponse> call, Response<ExchangeBuyResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    exchangeBuys.clear();
                    for (ExchangeBuyResponse.Item item : response.body().data.items) {
                        ExchangeBuy trans = new ExchangeBuy();
                        trans.setId(item.id);
                        trans.setSubscriberId(item.subscriber_id);
                        trans.setCreatedAt(item.created_at);
                        trans.setUpdatedAt(item.updated_at);
                        trans.setCoffee(item.coffee);
                        trans.setPriceBuy(item.price);
                        trans.setTotalQuantity(item.total_quantity);
                        trans.setSubcriberName(item.subscriber_name);
                        trans.setLocationName(item.location_name);
                        exchangeBuys.add(trans);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    return;
                }

                if (response.body().message != null)
                    Util.showToastMessage(myApplication, response.body().message);
                if (response.body().statusCode == KeyConstant.ERROR_CODE_AUTHEN) {
                    Manager.logout(myApplication);
                    // Intent intent = new Intent(myApplication, SignInActivity.class);
                    // startActivityForResult(intent, LOGIN_FOR_EXCHANGE);
                    Intent intent = new Intent(myApplication, TermActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ExchangeBuyResponse> call, Throwable t) {
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == LOGIN_FOR_EXCHANGE) {
//            if (resultCode != Activity.RESULT_OK) {
//                getActivity().finish();
//                return;
//            }
//            getExchangeBuyData();
//        }
//    }
}