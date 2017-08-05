package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
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
import vn.monkey.icco.model.GAP;
import vn.monkey.icco.model.GAPDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class GAPDetailFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private Long id;
    private ProgressBar progressBar;
    private WebView wv;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    public GAPDetailFragment(Long id) {
        this.id = id;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.gap_detail, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_gap_detail);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        wv = (WebView) mView.findViewById(R.id.gap_detail);
        initToolbar();
        getGapDetail();
        return mView;
    }

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     *
     */
    private void getGapDetail() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<GAPDetailResponse> call = myApplication.apiService
                .getDetailGAP(headerAuthen, id);
        call.enqueue(new Callback<GAPDetailResponse>() {
            @Override
            public void onResponse(
                    Call<GAPDetailResponse> call, Response<GAPDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    if(!isAdded()) return;
                    GAP gap = new GAP();
                    gap.setId(response.body().data.id);
                    gap.setTitle(response.body().data.title);
                    gap.setGap(response.body().data.gap);
                    gap.setCreatedAt(response.body().data.created_at);

                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    String html = context.getResources().getString(R.string.news_detail_html);
                    html = html.replace("[content_body]", gap.getGap());
                    html = html.replace("width:", "width_");
                    wv.loadDataWithBaseURL("file:///android_asset/html/css/", html, mimeType,
                            encoding, "");
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<GAPDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }
}
