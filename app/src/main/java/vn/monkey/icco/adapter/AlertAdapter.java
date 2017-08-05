package vn.monkey.icco.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import vn.monkey.icco.R;
import vn.monkey.icco.model.AlertListResponse;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ho2ng on 6/8/2016.
 */

public class AlertAdapter extends BaseAdapter {

    Context context;
    List<AlertListResponse.Item> alerts;

    public AlertAdapter(Context context, List<AlertListResponse.Item> alerts) {
        this.context = context;
        this.alerts = alerts;
    }

    public void addData(List<AlertListResponse.Item> alerts) {
        this.alerts.addAll(alerts);
        notifyDataSetChanged();
    }

    public void swapData(List<AlertListResponse.Item> alerts) {
        this.alerts.clear();
        this.alerts.addAll(alerts);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public AlertListResponse.Item getItem(int position) {
        return alerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlertListResponse.Item alert = alerts.get(position);
        if (!TextUtils.isEmpty(alert.label)) {
            holder.tvTitle.setText(alert.label);
        }
        if (!TextUtils.isEmpty(alert.description)) {
            holder.tvContent.setText(alert.description);
        }
        if (!TextUtils.isEmpty(alert.image_url)) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(alert.image_url)
                    .placeholder(R.mipmap.place_holder_34)
                    .error(R.mipmap.place_holder_34)
                    .fit().centerInside()
                    .into(holder.ivImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvTitle, tvContent;
        ImageView ivImage;
        ProgressBar progressBar;

        public ViewHolder(View v) {
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvContent = (TextView) v.findViewById(R.id.tvContent);
            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

}
