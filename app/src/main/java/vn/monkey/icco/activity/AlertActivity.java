package vn.monkey.icco.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.adapter.AlertAdapter;
import vn.monkey.icco.model.AlertListResponse;
import vn.monkey.icco.model.BaseResponse;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ho2ng on 6/8/2016.
 */

public class AlertActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lvAlert;
    AlertAdapter adapter;
    private ProgressBar progressBar;
    Toolbar toolbar;
    TextView tvTitle;
    ImageButton ibtBack;
    private CustomApplication myApplication;
    private int page = 1;
    private int total = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        myApplication = (CustomApplication) getApplication();
        bindview();
        init();
    }

    private void init() {
        setupToolbar();
        adapter = new AlertAdapter(this, new ArrayList<AlertListResponse.Item>());
        lvAlert.setAdapter(adapter);
        lvAlert.setOnItemClickListener(this);
        lvAlert.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                    if (progressBar.getVisibility() != View.VISIBLE) {
                        if (adapter.getCount() > 0 && adapter.getCount() < total) {
                            page++;
                            loadListAlert();
                        }
                    }
                }
            }
        });

        loadListAlert();
    }

    private void loadListAlert() {
        progressBar.setVisibility(View.VISIBLE);
        Call<AlertListResponse> call = myApplication.apiService
                .getAlertList(Util.getAuthenToken(myApplication), page, 20);
        call.enqueue(new Callback<AlertListResponse>() {
            @Override
            public void onResponse(Call<AlertListResponse> call,
                    Response<AlertListResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 401) {
                    Log.d("MainActivity", "401");
                    if (response.errorBody() != null) {
                        Converter<ResponseBody, BaseResponse> errorConverter
                                = myApplication.retrofit
                                .responseBodyConverter(BaseResponse.class, new Annotation[0]);
                        BaseResponse error = null;
                        try {
                            error = errorConverter.convert(response.errorBody());
                            //                            if (!TextUtils.isEmpty(error.message))
                            //                                Util.showToastMessage(myApplication, error.message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Util.backToMain(myApplication, AlertActivity.this);
                    return;
                }
                if (response.body() != null) {
                    if (!response.body().items.isEmpty()) {
                        if (page == 1)
                            adapter.swapData(response.body().items);
                        else
                            adapter.addData(response.body().items);

                        total = response.body().total;
                    }
                }
            }

            @Override
            public void onFailure(Call<AlertListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                //                Util.showToastMessage(myApplication, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void setupToolbar() {
        tvTitle.setText(getString(R.string.alerts));
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void bindview() {
        lvAlert = (ListView) findViewById(R.id.lvAlert);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertListResponse.Item tmp = adapter.getItem(position);
        //        if (tmp.click_type == 0) {
        //
        //        } else if (tmp.click_type == 1) {
        //            Intent intent = new Intent(myApplication, ProductDetailActivity.class);
        //            Bundle bundle = new Bundle();
        //            int productId;
        //            try {
        //                productId = Integer.parseInt(tmp.click_value);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //                productId = 0;
        //            }
        //            bundle.putSerializable(KeyConstant.KEY_PRODUCT, new ProductResponse(productId));
        //            intent.putExtra(KeyConstant.BUNDLE_KEY, bundle);
        //            startActivity(intent);
        //        } else if (tmp.click_type == 2) {
        //
        //        } else if (tmp.click_type == 3) {
        //            Intent intent = new Intent(myApplication, BrandDetailActivity.class);
        //            Bundle bundle = new Bundle();
        //            int brandId;
        //            try {
        //                brandId = Integer.parseInt(tmp.click_value);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //                brandId = 0;
        //            }
        //            bundle.putSerializable(KeyConstant.KEY_BRAND, new BrandResponse(brandId));
        //            intent.putExtra(KeyConstant.BUNDLE_KEY, bundle);
        //            startActivity(intent);
        //        } else if (tmp.click_type == 4) {
        //
        //            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(tmp.click_value));
        //            try {
        //                startActivity(i);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //
        //        }
    }

}
