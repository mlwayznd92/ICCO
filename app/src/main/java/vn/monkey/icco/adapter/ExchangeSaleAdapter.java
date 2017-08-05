package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.model.ExchangeSale;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Util;


/**
 * Created by Mlwayz on 5/28/2017.
 */

public class ExchangeSaleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView mRecyclerView;
    private List<ExchangeSale> exchangeSales;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExchangeSaleAdapter(List<ExchangeSale> exchangeSales, RecyclerView mRecyclerView) {
        this.exchangeSales = exchangeSales;
        this.mRecyclerView = mRecyclerView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return exchangeSales == null ? 0 : exchangeSales.size();
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.row_exchange_sale, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExchangeSale exchangeSale = exchangeSales.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        myViewHolder.tvSubcriber.setText(exchangeSale.getSubcriberName());
        myViewHolder.tvDate.setText(Util.getDateFromLong(exchangeSale.getCreatedAt(),
                KeyConstant.DATE_FORMAT_DD_MM_YYYY_HH_MI, false));
        myViewHolder.tvCoffee.setText(exchangeSale.getCoffee());
        myViewHolder.tvSold.setText(exchangeSale.getSold());
        myViewHolder.tvPrice.setText(String.valueOf(exchangeSale.getPrice()));
        myViewHolder.tvLocation.setText(exchangeSale.getLocationName());
        myViewHolder.tvTotalQuantity.setText(exchangeSale.getTotalQuantity());
    }

    /**
     *
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPrice, tvSold, tvTotalQuantity, tvCoffee, tvDate, tvSubcriber, tvLocation;
        public Context context;

        public MyViewHolder(View view) {
            super(view);
            tvSubcriber = (TextView) view.findViewById(R.id.tvSucriber);
            tvLocation = (TextView) view.findViewById(R.id.tvLocationExchangeSale);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvCoffee = (TextView) view.findViewById(R.id.tvCoffee);
            tvSold = (TextView) view.findViewById(R.id.tvSold);
            tvTotalQuantity = (TextView) view.findViewById(R.id.tvTotalQuantity);
            context = view.getContext();
        }
    }
}
