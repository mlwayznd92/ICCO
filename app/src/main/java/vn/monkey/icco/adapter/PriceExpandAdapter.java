package vn.monkey.icco.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.HeterogeneousExpandableList;
import android.widget.TextView;

import java.util.List;

import vn.monkey.icco.R;
import vn.monkey.icco.model.Price;
import vn.monkey.icco.model.PriceExpand;
import vn.monkey.icco.util.Util;

/**
 * Created by Yellow Code Books on 11/3/16.
 */

public class PriceExpandAdapter extends BaseExpandableListAdapter
        implements HeterogeneousExpandableList {

    private Context context;
    private List<PriceExpand> companyModels;
    private final int VIEW_TYPE_ITEM_ODD = 0;
    private final int VIEW_TYPE_ITEM_EVEN = 1;

    public PriceExpandAdapter(Context context, List<PriceExpand> companyModels) {
        this.context = context;
        this.companyModels = companyModels;
    }

    @Override
    public int getGroupCount() {
        return companyModels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return companyModels.get(groupPosition).getPrices().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return companyModels.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return companyModels.get(groupPosition).getPrices().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {

        String companyName = ((PriceExpand) getGroup(groupPosition)).getProvinceName();

        if (convertView == null) {
            LayoutInflater infalInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_province_list, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.province_name);
        tvName.setText(companyName);

        return convertView;
    }


    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return childPosition % 2 == 0 ? VIEW_TYPE_ITEM_ODD : VIEW_TYPE_ITEM_EVEN;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView,
                             ViewGroup viewGroup) {
        // get data
        Price price = (Price) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_commune_list, null);
            int childType = getChildType(groupPosition, childPosition);
            if (childType == VIEW_TYPE_ITEM_EVEN) {
                convertView.setBackgroundColor(
                        ContextCompat.getColor(viewGroup.getContext(), R.color.background_grey));
            }
        }

        // bind view
        TextView tvProvince = (TextView) convertView.findViewById(R.id.province_name);
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.price_average);
        TextView tvCompany = (TextView) convertView.findViewById(R.id.company);

        // set content
        tvProvince.setText(price.getProvinceName());
        tvType.setText(price.getType());
        tvPrice.setText(Util.formatNumber(price.getPriceAverage()) + "(" + price.getUnit() + ")");
        tvCompany.setText(price.getCompany());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
