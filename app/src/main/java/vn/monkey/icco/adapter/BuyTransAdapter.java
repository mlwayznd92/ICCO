package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.model.BuyTrans;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Util;


/**
 * Created by Mlwayz on 5/28/2017.
 */

public class BuyTransAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView mRecyclerView;
    private List<BuyTrans> buyTranses;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BuyTransAdapter(List<BuyTrans> buyTranses, RecyclerView mRecyclerView) {
        this.buyTranses = buyTranses;
        this.mRecyclerView = mRecyclerView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return buyTranses == null ? 0 : buyTranses.size();
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_buy_trans, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BuyTrans buyTrans = buyTranses.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        myViewHolder.tvDate.setText(Util.getDateFromLong(buyTrans.getCreatedAt(),
                KeyConstant.DATE_FORMAT_DD_MM_YYYY_HH_MI, false));
        myViewHolder.tvCoffee.setText(buyTrans.getCoffee());
        myViewHolder.tvPrice.setText(String.valueOf(buyTrans.getPriceBuy()));
        myViewHolder.tvTotalQuantity.setText(buyTrans.getTotalQuantity());
    }

    /**
     *
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPrice, tvTotalQuantity, tvCoffee, tvDate;
        public Context context;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvCoffee = (TextView) view.findViewById(R.id.tvCoffee);
            tvTotalQuantity = (TextView) view.findViewById(R.id.tvTotalQuantity);
            context = view.getContext();
        }
    }
}
