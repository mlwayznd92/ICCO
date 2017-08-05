package vn.monkey.icco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import vn.monkey.icco.adapter.QuestionAnswerAdapter;
import vn.monkey.icco.event.RecyclerTouchListener;
import vn.monkey.icco.model.OnLoadMoreListener;
import vn.monkey.icco.model.QuestionAnswer;
import vn.monkey.icco.model.QuestionAnswerResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class QuestionAnswerFragment extends Fragment {

    private static boolean isSearching = false;
    private List<QuestionAnswer> questionAnswers;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private QuestionAnswerAdapter mRecyclerAdapter;
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
        mView = inflater.inflate(R.layout.fragment_question_answer, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }

    private void bindView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_question_answer);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar_load_question_answer);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
    }

    /**
     *
     */
    private void init() {
        // check time reload maps
        boolean isLoad = false;
        isSearching = false;
        if (Manager.QAS != null && Manager.QAS.size() > 0 && Manager.LAST_RELOAD_QAS != null &&
                (System.currentTimeMillis() - Manager.LAST_RELOAD_QAS) / (60 * 1000) <
                        AppConfig.TIME_RELOAD_QAS) {
            questionAnswers = new ArrayList<>(Manager.QAS);
        } else {
            Manager.LAST_RELOAD_QAS = System.currentTimeMillis();
            Manager.LAST_PAGE_QAS = 1;
            Manager.QAS = new ArrayList<>();
            questionAnswers = new ArrayList<>();
            isLoad = true;
        }

        // init recycler view
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new QuestionAnswerAdapter(questionAnswers, mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, new QuestionFragment())
                        .addToBackStack(null).commit();
                searchView.closeSearch();
            }
        });

        searchListener();
        loadMoreListener();
        clickDetailListener();
        if (isLoad) getQAData();
    }

    /**
     *
     */
    private void loadMoreListener() {
        mRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isSearching) return;
                questionAnswers.add(null);
                mRecyclerAdapter.notifyItemInserted(questionAnswers.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Remove loading item
                        questionAnswers.remove(questionAnswers.size() - 1);
                        mRecyclerAdapter.notifyItemRemoved(questionAnswers.size());
                        //Load data
                        Manager.LAST_PAGE_QAS += 1;
                        getQAData();
                    }
                }, 1000);
            }
        });
    }

    /**
     *
     */
    private void getQAData() {
        // set load first time
        if (Manager.LAST_PAGE_QAS == 1) progressBar.setVisibility(View.VISIBLE);
        isGetData = false;
        String headerAuthen = String
                .format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<QuestionAnswerResponse> call = myApplication.apiService
                .getQAs(headerAuthen, Manager.LAST_PAGE_QAS);
        call.enqueue(new Callback<QuestionAnswerResponse>() {
            @Override
            public void onResponse(
                    Call<QuestionAnswerResponse> call, Response<QuestionAnswerResponse> response) {
                // set load first time
                if (Manager.LAST_PAGE_QAS == 1) progressBar.setVisibility(View.GONE);
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (QuestionAnswerResponse.Item item : response.body().data.items) {
                        QuestionAnswer questionAnswer = new QuestionAnswer();
                        questionAnswer.setId(item.id);
                        questionAnswer.setQuestion(item.question);
                        questionAnswer.setCreatedAt(item.created_at);
                        questionAnswers.add(questionAnswer);
                        Manager.QAS.add(questionAnswer);
                        isGetData = true;
                    }
                    if (!isGetData) Manager.LAST_PAGE_QAS -= 1;
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<QuestionAnswerResponse> call, Throwable t) {
                // set load first time
                if (Manager.LAST_PAGE_QAS == 1) progressBar.setVisibility(View.VISIBLE);
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
        Call<QuestionAnswerResponse> call = myApplication.apiService
                .searchQA(headerAuthen, keyword);
        call.enqueue(new Callback<QuestionAnswerResponse>() {
            @Override
            public void onResponse(
                    Call<QuestionAnswerResponse> call, Response<QuestionAnswerResponse> response) {
                progressBar.setVisibility(View.GONE);
                questionAnswers.clear();
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    for (QuestionAnswerResponse.Item item : response.body().data.items) {
                        QuestionAnswer questionAnswer = new QuestionAnswer();
                        questionAnswer.setId(item.id);
                        questionAnswer.setQuestion(item.question);
                        questionAnswer.setCreatedAt(item.created_at);
                        questionAnswers.add(questionAnswer);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerAdapter.setLoaded();
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<QuestionAnswerResponse> call, Throwable t) {
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
                        QuestionAnswer questionAnswer = questionAnswers.get(position);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().add(R.id.flContainer,
                                new QADetailFragment(questionAnswer.getId())).addToBackStack(null)
                                .commit();
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
        MaterialSearchView searchView = (MaterialSearchView) getActivity()
                .findViewById(R.id.search_view);
        searchView.setSuggestions(null);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearching = true;
                if (!TextUtils.isEmpty(newText)) {
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
