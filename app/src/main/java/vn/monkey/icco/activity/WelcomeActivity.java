package vn.monkey.icco.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.BuildConfig;
import vn.monkey.icco.R;
import vn.monkey.icco.model.VersionAppResponse;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

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

        getVersionApp();

        // weather info
        findViewById(R.id.btnWeatherInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(KeyConstant.MENU_WEATHER_INFO);
            }
        });

        // soil fertility
        findViewById(R.id.btnSoilFertility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showToastMessage(myApplication, getString(R.string.function_developing));
                //changeActivity(KeyConstant.MENU_SOIL_FERTILITY);
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

    /**
     * call api to check update
     */
    public void getVersionApp() {
        Call<VersionAppResponse> call = myApplication.apiService.getVersion();
        call.enqueue(new Callback<VersionAppResponse>() {
            @Override
            public void onResponse(Call<VersionAppResponse> call,
                                   Response<VersionAppResponse> response) {
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    VersionAppResponse.Item  version = response.body().data.items;
                    String versionName = BuildConfig.VERSION_NAME;
                    if(versionName.compareTo(version.version) < 0) {
                        showSettingsAlert(response.body().message, version.link);
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionAppResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert(String message, final String link) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WelcomeActivity.this);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.green_coffee_update));

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.next),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        }
                        finish();
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
}
