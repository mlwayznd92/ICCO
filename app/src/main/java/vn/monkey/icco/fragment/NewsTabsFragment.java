package vn.monkey.icco.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.ViewPagerAdapter;
import vn.monkey.icco.model.Category;
import vn.monkey.icco.model.CategoryResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsTabsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View mView;
    private ViewPagerAdapter viewPagerAdapter;
    private ProgressBar progressBar;
    private CustomApplication myApplication;
    private List<Category> categoryList;
    private MaterialSearchView searchView;
    private int tabIndex;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_news_tabs, container, false);
        myApplication = (CustomApplication) mView.getContext().getApplicationContext();
        bindView();
        init();
        return mView;
    }

    /**
     *
     */
    private void bindView() {
        viewPager = (ViewPager) mView.findViewById(R.id.viewPagerNews);
        tabLayout = (TabLayout) mView.findViewById(R.id.tabLayoutNews);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
    }

    /**
     *
     */
    private void init() {
        setHasOptionsMenu(true);
        searchListener();
        categoryList = new ArrayList<>();
        loadCategory();
    }

    private void initTabs() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                //TextView tabTextView = ;

                TextView tabTextView = (TextView) LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tab_item, tabLayout, false);
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.black));
                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                    tabTextView.setTextColor(
                            ContextCompat.getColor(mView.getContext(), R.color.blue_tabs));
                }
            }
        }


        //
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                tabTextView.setTypeface(null, Typeface.BOLD);
                tabTextView.setTextColor(
                        ContextCompat.getColor(mView.getContext(), R.color.blue_tabs));
                tabIndex = tab.getPosition();
                searchView.closeSearch();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                tabTextView.setTypeface(null, Typeface.NORMAL);
                tabTextView.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.black));
                searchView.closeSearch();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {

        if (categoryList == null || categoryList.size() < 1) return;
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        tabIndex = 0;
        for (Category category : categoryList) {
            viewPagerAdapter.addFragment(
                    new NewsFragment(category.getId(), getFragmentManager(), searchView),
                    category.getDisplayName());
        }
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(categoryList.size());
    }


    /**
     * load user info
     */
    private void loadCategory() {
        Util.disableTouch(progressBar, getActivity().getWindow());
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<CategoryResponse> call = myApplication.apiService.getCategory(headerAuthen);
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call,
                                   Response<CategoryResponse> response) {
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (CategoryResponse.Item item : response.body().data.items) {
                        Category category = new Category();
                        category.setId(item.id);
                        category.setDisplayName(item.display_name);
                        categoryList.add(category);
                    }
                    initTabs();
                }
                Util.enableTouch(progressBar, getActivity().getWindow());
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getActivity().getWindow());
                t.printStackTrace();
            }
        });
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
            public boolean onQueryTextChange(String query) {
                NewsFragment newsFragment = (NewsFragment) viewPagerAdapter.getItem(tabIndex);
                newsFragment.isSearching = true;
                if (!TextUtils.isEmpty(query)) {
                    newsFragment.search(query);
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
