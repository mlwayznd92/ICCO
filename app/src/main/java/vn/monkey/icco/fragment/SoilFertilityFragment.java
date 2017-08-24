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

import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.util.LocaleHelper;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class SoilFertilityFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_soil_fertiity, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        return mView;
    }
}
