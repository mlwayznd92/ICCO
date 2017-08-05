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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.QuestionAnswer;
import vn.monkey.icco.model.QuestionAnswerDetailResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class QADetailFragment extends Fragment {

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

    public QADetailFragment(Long id) {
        this.id = id;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.question_answer_detail, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        wv = (WebView) mView.findViewById(R.id.wv_answer_detail);
        progressBar = (ProgressBar) mView
                .findViewById(R.id.progress_bar_load_question_answer_detail);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        WebSettings webSettings = wv.getSettings();
        webSettings.setDefaultFontSize(18);
        initToolbar();
        getQADetail();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


    /**
     *
     */
    private void getQADetail() {
        progressBar.setVisibility(View.VISIBLE);
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<QuestionAnswerDetailResponse> call = myApplication.apiService
                .getDetailQA(headerAuthen, id);
        call.enqueue(new Callback<QuestionAnswerDetailResponse>() {
            @Override
            public void onResponse(
                    Call<QuestionAnswerDetailResponse> call,
                    Response<QuestionAnswerDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    if(!isAdded()) return;
                    QuestionAnswer questionAnswer = new QuestionAnswer();
                    questionAnswer.setId(response.body().data.id);
                    questionAnswer.setQuestion(response.body().data.question);
                    questionAnswer.setAnswer(response.body().data.answer);
                    questionAnswer.setStatus(response.body().data.status);
                    questionAnswer.setCreatedAt(response.body().data.created_at);
                    questionAnswer.setImage(response.body().data.image);

                    String mimeType = "text/html";
                    String encoding = "UTF-8";
                    String answer = questionAnswer.getAnswer() == null ? "" :
                                    questionAnswer.getAnswer();
                    String image = questionAnswer.getImage() == null ? "" :
                                   questionAnswer.getImage();
                    String html = context.getString(R.string.question_answer_detail_html);
                    html = html.replace("[question]", questionAnswer.getQuestion());
                    html = html.replace("[answer_label]",
                            answer.equals("") ? "" : getString(R.string.answer_label));
                    html = html.replace("[image]", image.equals("") ? "" :
                                                   "<img src=\"" + image +
                                                   "\" height=\"100%\" width=\"100%\" ");
                    html = html.replace("[content_body]", answer);
                    html = html.replace("width:", "width_");
                    wv.loadDataWithBaseURL("file:///android_asset/html/css/", html, mimeType,
                            encoding, "");
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<QuestionAnswerDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

}
