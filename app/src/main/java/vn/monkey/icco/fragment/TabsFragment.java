package vn.monkey.icco.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.adapter.ViewPagerAdapter;
import vn.monkey.icco.util.LocaleHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private CustomApplication myApplication;
    private View mView;

    public TabsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tabs, container, false);
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
        viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);
    }

    /**
     *
     */
    private void init() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        //        adapter.addFragment(new SaleFragment(), getString(R.string.sale_tab_name));
        //        adapter.addFragment(new SaleTransFragment(), getString(R.string.sale_trans_tab_name));
        viewPager.setAdapter(adapter);
    }

}
