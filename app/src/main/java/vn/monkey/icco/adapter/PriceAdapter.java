package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.model.Price;
import vn.monkey.icco.util.Util;


/**
 * Created by Mlwayz on 5/28/2017.
 */

public class PriceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM_ODD = 0;
    private final int VIEW_TYPE_ITEM_EVEN = 1;
    private RecyclerView mRecyclerView;
    private List<Price> prices;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PriceAdapter(List<Price> prices, RecyclerView mRecyclerView) {
        this.prices = prices;
        this.mRecyclerView = mRecyclerView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return prices == null ? 0 : prices.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? VIEW_TYPE_ITEM_ODD : VIEW_TYPE_ITEM_EVEN;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.row_price, parent, false);

        if (viewType == VIEW_TYPE_ITEM_EVEN) {
            view.setBackgroundColor(
                    ContextCompat.getColor(parent.getContext(), R.color.background_grey));
        }

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Price price = prices.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        myViewHolder.tvProvinceName.setText(price.getProvinceName());
        myViewHolder.tvAverage.setText(Util.formatNumber(price.getPriceAverage()));
        myViewHolder.tvOrganisation.setText(price.getOrganisationName());
        myViewHolder.tvUnit.setText(price.getUnit());
    }

    /**
     *
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProvinceName, tvAverage, tvOrganisation, tvUnit;
        public Context context;

        public MyViewHolder(View view) {
            super(view);
            tvProvinceName = (TextView) view.findViewById(R.id.price_province_name);
            tvAverage = (TextView) view.findViewById(R.id.price_average);
            tvOrganisation = (TextView) view.findViewById(R.id.price_organisation);
            tvUnit = (TextView) view.findViewById(R.id.price_unit);
            context = view.getContext();
        }
    }
}
