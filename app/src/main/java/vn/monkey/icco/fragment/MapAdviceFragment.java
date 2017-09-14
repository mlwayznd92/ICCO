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
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.Advice;
import vn.monkey.icco.model.AdviceResponse;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.model.News;
import vn.monkey.icco.model.NewsDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class MapAdviceFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private WebView wvMapAdvice;
    private Integer tem, pre, wind;


    public MapAdviceFragment(Marker marker) {
        try {
            String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
            Location location = Manager.MAPS.get(key);
            this.tem = (int) Math
                    .round((Float.valueOf(location.gettMax()) + Float.valueOf(location.gettMin())) /
                            2.0);
            this.pre = location.getPrecipitation();
            this.wind = location.getWndspd();
        } catch (Exception ex) {
            this.tem = 0;
            this.pre = 0;
            this.wind = 0;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_advice, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        initToolbar();
        getAdvice(tem, pre, wind);
        return mView;
    }

    private void bindView() {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        wvMapAdvice = (WebView) mView.findViewById(R.id.wvMapAdvice);
    }

    /**
     *
     */
    private void getAdvice(Integer tem, Integer pre, Integer wind) {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.getToken());
        Call<AdviceResponse> call =
                myApplication.apiService.getAdvice(headerAuthen, tem, pre, wind);
        call.enqueue(new Callback<AdviceResponse>() {
            @Override
            public void onResponse(Call<AdviceResponse> call, Response<AdviceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    AdviceResponse.Data adv = response.body().data;
                    if (!isAdded()) return;
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";

                    String html = context.getString(R.string.adv_detail_html);
                    html = html.replace("[title]", adv.title == null ? "" : adv.title);
                    html = html.replace("[content_body]", adv.gap == null ? "" : adv.gap);
                    html = html.replace("width:", "width_");
                    wvMapAdvice
                            .loadDataWithBaseURL("file:///android_asset/html/css/", html, mimeType,
                                    encoding, "");

                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<AdviceResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
