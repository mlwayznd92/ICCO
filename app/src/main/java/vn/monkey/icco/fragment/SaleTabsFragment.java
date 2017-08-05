package vn.monkey.icco.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.ViewPagerAdapter;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaleTabsFragment extends Fragment {

    public static boolean isReloadSaleTrans = false;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sale_tabs, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }

    /**
     *
     */
    private void bindView() {
        viewPager = (ViewPager) mView.findViewById(R.id.viewPagerSale);
        tabLayout = (TabLayout) mView.findViewById(R.id.tabLayoutSale);
    }

    /**
     *
     */
    private void init() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                //TextView tabTextView = ;

                TextView tabTextView = (TextView) LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tab_item, tabLayout, false);
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.black));
                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                    tabTextView.setTextColor(
                            ContextCompat.getColor(mView.getContext(), R.color.blue_tabs));
                }
            }
        }


        //
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                tabTextView.setTypeface(null, Typeface.BOLD);
                tabTextView.setTextColor(
                        ContextCompat.getColor(mView.getContext(), R.color.blue_tabs));
                View focus = getActivity().getCurrentFocus();
                if (focus != null) {
                    Util.hiddenKeyboard(focus, mView.getContext());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                tabTextView.setTypeface(null, Typeface.NORMAL);
                tabTextView.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.black));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        //        SaleTransFragment saleTransFragment = new SaleTransFragment();
        SaleFragment saleFragment = new SaleFragment();
        ExchangeBuyFragment exchangeBuyFragment = new ExchangeBuyFragment();
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        saleFragment.setViewPager(viewPager);
        viewPagerAdapter.addFragment(exchangeBuyFragment, getString(R.string.buy_tab_name));
        viewPagerAdapter.addFragment(saleFragment, getString(R.string.sale_tab_name));
        //        saleTransFragment.setViewPagerAdapter(viewPagerAdapter);
        //        viewPagerAdapter.addFragment(saleTransFragment, getString(R.string.sale_trans_tab_name));
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }
}
