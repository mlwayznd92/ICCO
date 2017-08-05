package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.News;
import vn.monkey.icco.model.NewsDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class NewsDetailFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private News info;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    public NewsDetailFragment(News info) {
        this.info = info;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.news_detail, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_news_detail);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        initToolbar();
        getNewsDetail();
        return mView;
    }

    /**
     *
     */
    private void initToolbar() {
        setHasOptionsMenu(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                toolbar.setNavigationIcon(R.drawable.menu_grey_36x36);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void getNewsDetail() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<NewsDetailResponse> call = myApplication.apiService
                .getDetailNews(headerAuthen, info.getId());
        call.enqueue(new Callback<NewsDetailResponse>() {
            @Override
            public void onResponse(
                    Call<NewsDetailResponse> call, Response<NewsDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    if(!isAdded()) return;
                    News news = new News();
                    news.setContent(response.body().data.content);
                    news.setCreatedDate(response.body().data.created_at);

                    WebView wv = (WebView) mView.findViewById(R.id.new_detail);
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";

                    String html = context.getString(R.string.news_detail_html);
                    html = html.replace("[content_body]",
                            news.getContent() == null ? "" : news.getContent());
                    html = html.replace("width:", "width_");
                    wv.loadDataWithBaseURL("file:///android_asset/html/css/", html, mimeType,
                            encoding, "");
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<NewsDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
