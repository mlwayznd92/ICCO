package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.model.GAP;
import vn.monkey.icco.model.OnLoadMoreListener;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Util;


/**
 * Created by Mlwayz on 5/28/2017.
 */

public class GAPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    private List<GAP> gaps;

    // Provide a suitable constructor (depends on the kind of dataset)
    public GAPAdapter(List<GAP> gaps, RecyclerView mRecyclerView) {
        this.gaps = gaps;
        this.mRecyclerView = mRecyclerView;

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return gaps == null ? 0 : gaps.size();
    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return gaps.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_gap, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            GAP gap = gaps.get(position);
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            myViewHolder.title.setText(gap.getTitle());
            myViewHolder.tvCreateDate.setText(Util.getDateFromLong(gap.getCreatedAt(),
                    KeyConstant.DATE_FORMAT_DD_MM_YYYY_HH_MI, false));
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, tvCreateDate;
        public Context context;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_gap);
            tvCreateDate = (TextView) view.findViewById(R.id.tv_create_date);
            context = view.getContext();
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
