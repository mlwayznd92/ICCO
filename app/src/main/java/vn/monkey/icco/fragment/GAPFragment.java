package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.GAPAdapter;
import vn.monkey.icco.event.RecyclerTouchListener;
import vn.monkey.icco.model.GAP;
import vn.monkey.icco.model.GAPResponse;
import vn.monkey.icco.model.OnLoadMoreListener;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class GAPFragment extends Fragment {

    private static boolean isSearching = false;
    private List<GAP> gaps;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private GAPAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private boolean isGetData = false;
    private ProgressBar progressBar;
    private MaterialSearchView searchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_gap, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }

    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_gap);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_gap);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
    }

    /**
     *
     */
    private void init() {
        // check time reload maps
        boolean isLoad = false;
        isSearching = false;
        if (Manager.GAPS != null && Manager.GAPS.size() > 0 && Manager.LAST_RELOAD_GAPS != null &&
            (System.currentTimeMillis() - Manager.LAST_RELOAD_GAPS) / (60 * 1000) <
            AppConfig.TIME_RELOAD_GAPS) {
            gaps = new ArrayList<>(Manager.GAPS);
        } else {
            Manager.LAST_RELOAD_GAPS = System.currentTimeMillis();
            Manager.LAST_PAGE_GAPS = 1;
            Manager.GAPS = new ArrayList<>();
            gaps = new ArrayList<>();
            isLoad = true;
        }

        // init recycler view
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new GAPAdapter(gaps, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        searchListener();
        loadMoreListener();
        clickDetailListener();
        if (isLoad) getGAPData();
    }

    /**
     *
     */
    private void loadMoreListener() {
        mRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isSearching) return;
                gaps.add(null);
                mRecyclerAdapter.notifyItemInserted(gaps.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Remove loading item
                        gaps.remove(gaps.size() - 1);
                        mRecyclerAdapter.notifyItemRemoved(gaps.size());
                        //Load data
                        Manager.LAST_PAGE_GAPS += 1;
                        getGAPData();
                    }
                }, 1000);
            }
        });
    }

    /**
     *
     */
    private void getGAPData() {
        // set load first time
        if (Manager.LAST_PAGE_GAPS == 1) progressBar.setVisibility(View.VISIBLE);
        isGetData = false;
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<GAPResponse> call = myApplication.apiService
                .getGAPs(headerAuthen, Manager.LAST_PAGE_GAPS);
        call.enqueue(new Callback<GAPResponse>() {
            @Override
            public void onResponse(Call<GAPResponse> call, Response<GAPResponse> response) {
                // set load first time
                if (Manager.LAST_PAGE_GAPS == 1) progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (GAPResponse.Item item : response.body().data.items) {
                        GAP gap = new GAP();
                        gap.setId(item.id);
                        gap.setTitle(item.title);
                        gap.setGap(item.gap);
                        gap.setCreatedAt(item.created_at);
                        gaps.add(gap);
                        Manager.GAPS.add(gap);
                        isGetData = true;
                    }
                    if (!isGetData) Manager.LAST_PAGE_GAPS -= 1;
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<GAPResponse> call, Throwable t) {
                // set load first time
                if (Manager.LAST_PAGE_GAPS == 1) progressBar.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private void search(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<GAPResponse> call = myApplication.apiService
                .searchGAP(headerAuthen, keyword);
        call.enqueue(new Callback<GAPResponse>() {
            @Override
            public void onResponse(Call<GAPResponse> call, Response<GAPResponse> response) {
                progressBar.setVisibility(View.GONE);
                gaps.clear();
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (GAPResponse.Item item : response.body().data.items) {
                        GAP gap = new GAP();
                        gap.setId(item.id);
                        gap.setTitle(item.title);
                        gap.setGap(item.gap);
                        gap.setCreatedAt(item.created_at);
                        gaps.add(gap);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<GAPResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private void clickDetailListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(
                getActivity().getApplicationContext(), mRecyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        GAP gap = gaps.get(position);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                       .add(R.id.flContainer, new GAPDetailFragment(gap.getId()))
                                       .addToBackStack(null).commit();
                        searchView.closeSearch();
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));
    }

    /**
     *
     */
    private void searchListener() {
        searchView.setSuggestions(null);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearching = true;
                if(!TextUtils.isEmpty(newText)){
                    search(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setIcon(R.drawable.ic_search_black_24dp);
    }
}
