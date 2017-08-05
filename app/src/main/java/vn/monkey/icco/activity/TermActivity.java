package vn.monkey.icco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.model.TermResponse;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

public class TermActivity extends AppCompatActivity {

    private WebView wvPolicyAndTerms;
    private Button btnSkip;
    private ProgressBar progressBar;
    private CustomApplication myApplication;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        myApplication = (CustomApplication) getApplicationContext();
        bindView();
        init();
    }

    /**
     *
     */
    private void bindView() {
        wvPolicyAndTerms = (WebView) findViewById(R.id.wvPolicyAndTerms);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    /**
     *
     */
    private void init() {

        getTerm();

        // language
        findViewById(R.id.btn_vn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("vn");
            }
        });

        // language
        findViewById(R.id.btn_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TermActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    /**
     * @param lang
     */
    public void setLocale(String lang) {
        LocaleHelper.setLocale(this, lang);
        Intent intent = new Intent(TermActivity.this, TermActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     *
     */
    private void getTerm() {
        progressBar.setVisibility(View.VISIBLE);
        Call<TermResponse> call = myApplication.apiService.getTerm();
        call.enqueue(new Callback<TermResponse>() {
            @Override
            public void onResponse(Call<TermResponse> call, Response<TermResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;

                if (response.body().success) {
                    String term = "";
                    for (TermResponse.Item item : response.body().data.items) {
                        term += item.term;
                    }

                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";

                    String html = getString(R.string.news_detail_html);
                    html = html.replace("[content_body]", term);
                    html = html.replace("width:", "width_");
                    wvPolicyAndTerms
                            .loadDataWithBaseURL("file:///android_asset/html/css/", html, mimeType,
                                    encoding, "");
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<TermResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }
}
