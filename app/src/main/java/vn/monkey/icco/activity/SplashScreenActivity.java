package vn.monkey.icco.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import vn.monkey.icco.R;
import vn.monkey.icco.model.CheckDeviceTokenResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 02-Jun-16.
 */

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    private ImageView ivTop;
    private CustomApplication myApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        myApplication = (CustomApplication) getApplication();
        bindView();
        init();
    }

    private void bindView() {
        ivTop = (ImageView) findViewById(R.id.ivTop);
    }

    private void init() {
        Animation animMoveUp;
        animMoveUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up);
        animMoveUp.setAnimationListener(this);
        ivTop.setAnimation(animMoveUp);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // get user from cache
        Manager.USER = Manager.getUserCached(myApplication);

        // check had firebase token
        String firebaseToken = Manager.getFirebaseToken(myApplication);
        if (TextUtils.isEmpty(firebaseToken)) {
            callSendFirebaseToken();
        }

        // go to welcome screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Manager.isLogin()) {
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, TermActivity.class));
                }
                finish();
            }
        }, 1000);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    /**
     * call api to send firebase token
     */
    public void callSendFirebaseToken() {
        // call check device token
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.getToken());
        final String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Call<CheckDeviceTokenResponse> call = myApplication.apiService
                .checkDeviceToken(headerAuthen, firebaseToken, KeyConstant.ANDROID_CHANEL,
                        getMacMD5());
        call.enqueue(new Callback<CheckDeviceTokenResponse>() {
            @Override
            public void onResponse(Call<CheckDeviceTokenResponse> call,
                                   Response<CheckDeviceTokenResponse> response) {
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    Manager.saveFirebaseToken(myApplication, firebaseToken);
                }
            }

            @Override
            public void onFailure(Call<CheckDeviceTokenResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private String getMacMD5() {
        try {
            Context context = getApplicationContext();
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            return Util.getMD5(info.getMacAddress());
        } catch (Exception ex) {
            Log.e(KeyConstant.APP_CODE, "MAC MD5 fail!", ex);
            return "";
        }
    }
}
