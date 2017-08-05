package vn.monkey.icco.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.model.Price;
import vn.monkey.icco.model.PriceDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

public class PriceGraphActivity extends Activity {

    private CustomApplication myApplication;
    private ProgressBar progressBar;
    private List<Price> prices;
    private GraphView graph;
    private TextView tvTitle;
    private TextView tvBack;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_graph);
        myApplication = (CustomApplication) getApplication();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_load_price_detail);
        graph = (GraphView) findViewById(R.id.graph_price);
        tvTitle = (TextView) findViewById(R.id.title_graph);
        tvBack = (TextView) findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvBack.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.black_10));
                finish();
            }
        });
        prices = new ArrayList<>();
        getPriceDetail();
    }

    /**
     * @param graph
     */
    private void initGraph(GraphView graph, List<Price> prices) {

        if (prices.size() <= 1) {
            Util.showToastMessage(myApplication, getString(R.string.data_not_enough));
            return;
        }

        // set grahp
        Collections.sort(prices);
        String title = getString(R.string.coffee_graph_title);
        String to = Util.getDateFromLong(prices.get(0).getCreatedDate(),
                KeyConstant.DATE_FORMAT_DD_MM_YYYY, false);
        String from = Util.getDateFromLong(prices.get(prices.size() - 1).getCreatedDate(),
                KeyConstant.DATE_FORMAT_DD_MM_YYYY, false);
        title = String.format(title, prices.get(0).getProvinceName(), to, from);
        tvTitle.setText(title);

        DataPoint[] dataPoints = new DataPoint[prices.size()];
        String[] labelDate = new String[prices.size()];
        Integer priceMax = 0;
        Integer priceMin = prices.get(0).getPriceAverage();
        for (int i = 0; i < prices.size(); i++) {
            Price price = prices.get(i);
            if (priceMax < price.getPriceAverage()) priceMax = price.getPriceAverage();
            if (priceMin > price.getPriceAverage()) priceMin = price.getPriceAverage();
            dataPoints[i] = new DataPoint(i, price.getPriceAverage());
            labelDate[i] = (i == 0 || (i == prices.size() - 1) || i == (prices.size() / 2)) ?
                    Util.getDateFromLong(price.getCreatedDate(), KeyConstant.DATE_FORMAT_DD_MM,
                            false) : "";
        }

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(labelDate);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getViewport().setMaxY(priceMax + 200);
        graph.getViewport().setMinY(priceMin - 200);
        graph.getViewport().setYAxisBoundsManual(true);

        // second series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setDrawDataPoints(true);
        series.setTitle(getString(R.string.price));
        graph.addSeries(series);

        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    /**
     *
     */
    private void getPriceDetail() {

        Intent intent = getIntent();
        Long coffeeOldId = intent.getLongExtra("coffee_old_id", 0);
        Long createdDate = intent.getLongExtra("created_date", 0);

        String date = Util.getDateFromLong(createdDate, KeyConstant.DATE_FORMAT_DD_MM_YYYY, false);
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<PriceDetailResponse> call =
                myApplication.apiService.getPriceDetail(headerAuthen, coffeeOldId, date);
        call.enqueue(new Callback<PriceDetailResponse>() {
            @Override
            public void onResponse(Call<PriceDetailResponse> call,
                                   Response<PriceDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                prices.clear();
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (PriceDetailResponse.Item item : response.body().data.items) {
                        Price price = new Price();
                        price.setPriceAverage(item.price_average);
                        price.setUnit(item.unit);
                        price.setCreatedDate(item.created_at);
                        price.setProvinceName(item.province_name);
                        price.setOrganisationName(item.organisation_name);
                        prices.add(price);
                    }
                    initGraph(graph, prices);
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<PriceDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }
}
