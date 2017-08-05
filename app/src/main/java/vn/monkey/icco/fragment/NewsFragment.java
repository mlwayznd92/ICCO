package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.NewsAdapter;
import vn.monkey.icco.event.RecyclerTouchListener;
import vn.monkey.icco.model.News;
import vn.monkey.icco.model.NewsResponse;
import vn.monkey.icco.model.OnLoadMoreListener;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class NewsFragment extends Fragment {

    public  boolean isSearching = false;
    private List<News> news;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private NewsAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private boolean isGetData = false;
    private ProgressBar progressBar;
    private Long categoryId;
    private Integer lastPage;
    private FragmentManager fragmentManager;
    private MaterialSearchView searchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    public NewsFragment(Long categoryId, FragmentManager fragmentManager,
                        MaterialSearchView searchView) {
        this.categoryId = categoryId;
        this.fragmentManager = fragmentManager;
        this.searchView = searchView;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_news, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }


    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_news);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_news);
    }

    /**
     *
     */
    private void init() {
        // check time reload maps
        boolean isLoad = false;
        isSearching = false;
        if (Manager.NEWS != null && Manager.NEWS.size() > 0 && Manager.LAST_RELOAD_NEWS != null &&
                (System.currentTimeMillis() - Manager.LAST_RELOAD_NEWS) / (60 * 1000) <
                        AppConfig.TIME_RELOAD_NEWS) {
            news = new ArrayList<>();
            for (News info : Manager.NEWS){
                if (info.getCategoryId() ==  categoryId) news.add(info);
            }
            lastPage = Manager.LAST_PAGE_NEWS.get(categoryId);
        } else {
            Manager.LAST_RELOAD_NEWS = System.currentTimeMillis();
            Manager.LAST_PAGE_NEWS = new HashMap<>();
            Manager.NEWS = new ArrayList<>();
            lastPage = 1;
            Manager.LAST_PAGE_NEWS.put(categoryId, lastPage);
            news = new ArrayList<>();
            isLoad = true;
        }

        // init recycler view
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new NewsAdapter(news, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        loadMoreListener();
        clickDetailListener();
        if (isLoad) getNewsData();
    }

    /**
     *
     */
    private void loadMoreListener() {
        mRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isSearching) return;
                news.add(null);
                mRecyclerAdapter.notifyItemInserted(news.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Remove loading item
                        news.remove(news.size() - 1);
                        mRecyclerAdapter.notifyItemRemoved(news.size());
                        //Load data
                        lastPage += 1;
                        Manager.LAST_PAGE_NEWS.put(categoryId, lastPage);
                        getNewsData();
                    }
                }, 1000);
            }
        });
    }

    /**
     *
     */
    private void getNewsData() {
        // set load first time
        if (lastPage == 1) progressBar.setVisibility(View.VISIBLE);
        isGetData = false;
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<NewsResponse> call = myApplication.apiService
                .getNews(headerAuthen, categoryId, lastPage);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                // set load first time
                if (lastPage == 1) progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (NewsResponse.Data.Item items : response.body().data.items) {
                        News info = new News();
                        info.setId(items.id);
                        info.setTitle(items.title);
                        info.setCreatedDate(items.created_at);
                        info.setShortDescription(items.short_description);
                        info.setImage(items.image);
                        info.setCategoryId(items.category_id);
                        news.add(info);
                        Manager.NEWS.add(info);
                        isGetData = true;
                    }
                    if (!isGetData) {
                        lastPage -= 1;
                        Manager.LAST_PAGE_NEWS.put(categoryId, lastPage);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                // set load first time
                if (lastPage == 1) progressBar.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    /**
     *
     */
    public void search(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<NewsResponse> call = myApplication.apiService.searchNews(headerAuthen, keyword);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                news.clear();
                if (response.body().success) {
                    for (NewsResponse.Data.Item items : response.body().data.items) {
                        News info = new News();
                        info.setId(items.id);
                        info.setTitle(items.title);
                        info.setCreatedDate(items.created_at);
                        info.setShortDescription(items.short_description);
                        info.setImage(items.image);
                        info.setCategoryId(items.category_id);
                        if(categoryId == items.category_id) {
                            news.add(info);
                        }
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.VISIBLE);
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
                        News info = news.get(position);
                        fragmentManager.beginTransaction()
                                .add(R.id.flContainer, new NewsDetailFragment(info))
                                .addToBackStack(null).commit();
                        searchView.closeSearch();
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));
    }
}
