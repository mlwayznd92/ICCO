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
import android.widget.ProgressBar;
import android.widget.TextView;

import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.util.LocaleHelper;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class MapAdviceFragment extends Fragment {

    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView tvMapAdvice;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_advice, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        initToolbar();
        getAdvice();
        return mView;
    }

    private void bindView() {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        tvMapAdvice = (TextView) mView.findViewById(R.id.tvMapAdvice);
    }

    private void getAdvice() {
        tvMapAdvice.setText("Nhiệt Độ: 15 - 21\n" +
                            "Dự Báo: Có Mưa Rải Rác\n" +
                            "Độ Ẩm: 89%\n" +
                            "Lượng Thoát Bốc Hơi Nước: 8mml\n" +
                            "Lượng Mưa: 2mml\n" +
                            "Số Giờ Mưa: 0.5 - 1h\n" +
                            "Theo tính toán của các doanh nghiệp, với mức giá này chỉ ngang bằng hoặc thấp hơn giá thành, làm cho người nông dân thua thiệt. Đắc Lắc cũng như các doanh nghiệp kinh doanh xuất khẩu cà phê khuyến cáo bà con nông dân hạn chế bán cà phê nhân ra thị trường, để chờ giá lên. Ông Nguyễn Xuân Thái, Giám đốc Công ty cà phê Thắng Lợi khuyên bà con nông dân không nên bán cà phê ra ồ ạt trong lúc này khi giá cả xuống thấp hơn giá thành thì sẽ càng làm cho giá cà phê thấp hơn nữa.");
    }

    /**
     *
     */
    private void initToolbar() {
        setHasOptionsMenu(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                toolbar.setNavigationIcon(R.drawable.menu_grey_36x36);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                });
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
