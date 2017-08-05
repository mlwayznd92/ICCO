package vn.monkey.icco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.monkey.icco.R;
import vn.monkey.icco.event.LockNavigationDrawerEvent;
import vn.monkey.icco.fragment.BuyTabsFragment;
import vn.monkey.icco.fragment.GAPFragment;
import vn.monkey.icco.fragment.MapFragment;
import vn.monkey.icco.fragment.NewsTabsFragment;
import vn.monkey.icco.fragment.PriceFragment;
import vn.monkey.icco.fragment.QuestionAnswerFragment;
import vn.monkey.icco.fragment.SaleTabsFragment;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static int currentNav;
    // private final int LOGIN_FOR_BUY = 1000;
    // private final int LOGIN_FOR_SALE = 1001;
    private final int EDIT_FOR_PROFILE = 1002;
    private Toolbar toolbar;
    private TextView titleToolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ImageButton ibtAlert;
    private TextView tvName;
    private CircleImageView ivAvatar;
    private MaterialSearchView searchView;
    private CustomApplication myApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApplication = (CustomApplication) getApplication();
        bindView();
        init();
    }

    /**
     *
     */
    private void bindView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleToolbar = (TextView) findViewById(R.id.title_toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvName = (TextView) header.findViewById(R.id.tvName);
        ivAvatar = (CircleImageView) header.findViewById(R.id.ivAvatar);
        ibtAlert = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.ibtAlert);
    }


    /**
     *
     */
    private void init() {
        setupToolbar();
        loadUserInformation();
        String type = getIntent().getStringExtra("type");
        if (Manager.USER == null) Manager.USER = Manager.getUserCached(myApplication);
        if (KeyConstant.MENU_WEATHER_INFO.equals(type)) {
            titleToolbar.setText(R.string.weather_info);
            navigationView.setCheckedItem(R.id.nav_weather_info);
            replaceFragment(new MapFragment());
        } else if (KeyConstant.MENU_PRICE.equals(type)) {
            titleToolbar.setText(R.string.price);
            navigationView.setCheckedItem(R.id.nav_price);
            replaceFragment(new PriceFragment());
        } else if (KeyConstant.MENU_SALE.equals(type)) {
            titleToolbar.setText(R.string.sale);
            navigationView.setCheckedItem(R.id.nav_sale);
            replaceFragment(new SaleTabsFragment());
        } else if (KeyConstant.MENU_BUY.equals(type)) {
            titleToolbar.setText(R.string.buy);
            navigationView.setCheckedItem(R.id.nav_buy);
            replaceFragment(new BuyTabsFragment());
        } else if (KeyConstant.MENU_NEWS.equals(type)) {
            navigationView.setCheckedItem(R.id.nav_news);
            titleToolbar.setText(R.string.news);
            replaceFragment(new NewsTabsFragment());
        } else if (KeyConstant.MENU_QUESTION_AND_ANSWER.equals(type)) {
            titleToolbar.setText(R.string.question_and_answer);
            navigationView.setCheckedItem(R.id.nav_question_and_answer);
            replaceFragment(new QuestionAnswerFragment());
        } else if (KeyConstant.MENU_PEST_AND_DISEASE.equals(type)) {
            titleToolbar.setText(R.string.pest_and_disease);
            navigationView.setCheckedItem(R.id.nav_pest_and_disease);
            replaceFragment(new GAPFragment());
        }
        updateMenuNavigator();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() != R.id.nav_buy && item.getItemId() != R.id.nav_sale) {
            currentNav = item.getItemId();
        }
        switch (item.getItemId()) {
            case R.id.nav_weather_info:
                titleToolbar.setText(R.string.weather_info);
                replaceFragment(new MapFragment());
                break;
            case R.id.nav_price:
                titleToolbar.setText(R.string.price);
                replaceFragment(new PriceFragment());
                break;
            case R.id.nav_sale:
                titleToolbar.setText(R.string.sale);
                replaceFragment(new SaleTabsFragment());
                break;
            //            if (Manager.isLogin()) {
            //                titleToolbar.setText(R.string.sale);
            //                replaceFragment(new SaleTabsFragment());
            //                break;
            //            }
            //            // require login
            //            intent = new Intent(myApplication, SignInActivity.class);
            //            startActivityForResult(intent, LOGIN_FOR_SALE);
            //            break;
            case R.id.nav_buy:
                titleToolbar.setText(R.string.buy);
                replaceFragment(new BuyTabsFragment());
                break;
            //            // check user login
            //            if (Manager.isLogin()) {
            //                titleToolbar.setText(R.string.buy);
            //                replaceFragment(new BuyTabsFragment());
            //                break;
            //            }
            //            // require login
            //            intent = new Intent(myApplication, SignInActivity.class);
            //            startActivityForResult(intent, LOGIN_FOR_BUY);
            //            break;
            case R.id.nav_news:
                titleToolbar.setText(R.string.news);
                replaceFragment(new NewsTabsFragment());
                break;
            case R.id.nav_question_and_answer:
                titleToolbar.setText(R.string.question_and_answer);
                replaceFragment(new QuestionAnswerFragment());
                break;
            case R.id.nav_pest_and_disease:
                titleToolbar.setText(R.string.pest_and_disease);
                replaceFragment(new GAPFragment());
                break;
            case R.id.nav_logout:
                Manager.logout(myApplication);
                intent = new Intent(this, TermActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     */
    private void loadUserInformation() {

        // load info
        if (!Manager.isLogin()) {
            tvName.setText(getString(R.string.guest));
            Picasso.with(myApplication).load(R.mipmap.no_user_profile_image).into(ivAvatar);
        } else {
            tvName.setText(
                    !TextUtils.isEmpty(Manager.USER.getFullName()) ? Manager.USER.getFullName() :
                            Manager.USER.getUsername());

            if (!TextUtils.isEmpty(Manager.USER.getAvatarUrl()))
                Picasso.with(myApplication).load(Manager.USER.getAvatarUrl())
                        .error(R.mipmap.no_user_profile_image)
                        .placeholder(R.mipmap.no_user_profile_image).into(ivAvatar);
        }
        updateMenuNavigator();
    }

    /**
     * update menu navigator
     */
    private void updateMenuNavigator() {
        Menu menu = navigationView.getMenu();
        MenuItem nav_logout = menu.findItem(R.id.nav_logout);
        if (Manager.isLogin()) {
            nav_logout.setVisible(true);
        } else {
            nav_logout.setVisible(false);
        }
    }

    /**
     *
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.ibtAlert).setOnClickListener(this);
        header.findViewById(R.id.ivAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Manager.isLogin()) {
                    drawer.closeDrawer(Gravity.LEFT);
                    startActivityForResult(new Intent(myApplication, ProfileActivity.class),
                            EDIT_FOR_PROFILE);
                }
                //                else {
                //                    // require login
                //                    Intent intent = new Intent(myApplication, SignInActivity.class);
                //                    startActivity(intent);
                //                }
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    Util.hiddenKeyboard(focus, myApplication);
                }
                super.onDrawerStateChanged(newState);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    /**
     * @param fragment
     */
    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flContainer, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        //        switch (v.getId()) {
        //            case R.id.ibtAlert:
        //                drawer.closeDrawer(GravityCompat.START);
        //                if (myApplication.loginAsGuest)
        //                    Util.showToastMessage(myApplication, getString(R.string.guest_message));
        //                startActivity(new Intent(MainActivity.this, AlertActivity.class));
        //                break;
        //
        //        }
    }

    @Override
    protected void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        loadUserInformation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }


    @Subscribe
    public void onEvent(LockNavigationDrawerEvent e) {
        if (e.lock) drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView.setMenuItem(itemSearch);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && requestCode != EDIT_FOR_PROFILE) {
            navigationView.setCheckedItem(currentNav);
            return;
        }
        switch (requestCode) {
            //        case LOGIN_FOR_BUY:
            //            titleToolbar.setText(R.string.buy);
            //            replaceFragment(new BuyTabsFragment());
            //            break;
            //        case LOGIN_FOR_SALE:
            //            titleToolbar.setText(R.string.sale);
            //            replaceFragment(new SaleTabsFragment());
            //            break;
            case EDIT_FOR_PROFILE:
                loadUserInformation();
                break;
        }
    }
}
