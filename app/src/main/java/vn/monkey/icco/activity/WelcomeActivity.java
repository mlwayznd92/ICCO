package vn.monkey.icco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import vn.monkey.icco.R;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;

/**
 * Created by ANHTH on 15-Jun-16.
 */

public class WelcomeActivity extends AppCompatActivity {

//    private final int LOGIN_FOR_BUY = 1000;
//    private final int LOGIN_FOR_SALE = 1001;
    private CustomApplication myApplication;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        myApplication = (CustomApplication) getApplication();
        init();
    }

    /**
     * init
     */
    private void init() {

        // weather info
        findViewById(R.id.btnWeatherInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_WEATHER_INFO);
            }
        });

        // price
        findViewById(R.id.btnPrice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_PRICE);
            }
        });

        //sale
        findViewById(R.id.btnSale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (Manager.isLogin()) {
                //    changeActivity(KeyConstant.MENU_SALE);
                //    return;
                // }
                // Intent intent = new Intent(myApplication, SignInActivity.class);
                // startActivityForResult(intent, LOGIN_FOR_SALE);
                changeActivity(KeyConstant.MENU_SALE);
            }
        });

        //buy
        findViewById(R.id.btnBuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (Manager.isLogin()) {
                // changeActivity(KeyConstant.MENU_BUY);
                //    return;
                // }
                // Intent intent = new Intent(myApplication, SignInActivity.class);
                // startActivityForResult(intent, LOGIN_FOR_BUY);
                changeActivity(KeyConstant.MENU_BUY);
            }
        });

        // new
        findViewById(R.id.btnNews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_NEWS);
            }
        });

        // question and answer
        findViewById(R.id.btnQuestionAndAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_QUESTION_AND_ANSWER);
            }
        });

        // gap
        findViewById(R.id.btnPestAndDisease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_PEST_AND_DISEASE);
            }
        });
    }

    /**
     * @param type
     */
    private void changeActivity(String type) {
        Intent intent = new Intent(myApplication, MainActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) return;
//        switch (requestCode) {
//        case LOGIN_FOR_BUY:
//            changeActivity(KeyConstant.MENU_BUY);
//            break;
//        case LOGIN_FOR_SALE:
//            changeActivity(KeyConstant.MENU_SALE);
//            break;
//        }
//    }
}
