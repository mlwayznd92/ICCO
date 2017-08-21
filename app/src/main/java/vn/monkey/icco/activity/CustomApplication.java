package vn.monkey.icco.activity;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.monkey.icco.network.ApiService;
import vn.monkey.icco.network.PersistentCookieStore;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.LocaleHelper;


/**
 * Created by ANHTH on 02-Jun-16.
 */

public class CustomApplication extends MultiDexApplication {
    public static OkHttpClient okHttpClient;
    public Retrofit retrofit;
    public ApiService apiService;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
        apiService = retrofit.create(ApiService.class);

    }

    private void initRetrofit() {
        Cache cache = null;
        try {
            cache = new Cache(getCacheDir(), 10 * 1024 * 1024);
        } catch (Exception ex) {
            Log.e("OkHttp", "Could not create http cache", ex);
        }
        Log.d("CustomApplication", "cache :" + cache);
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (this.okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .writeTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)
                    .cache(cache)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    //.addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();
                            requestBuilder.header("Content-Type", "application/json");
                            requestBuilder.header("X-Api-Key", AppConfig.X_API_KEY);
                            Request request = requestBuilder.build();
                            Response response = chain.proceed(request);
                            return response;
                        }
                    })
                    .build();

        }

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AppConfig.HOST)
                .client(okHttpClient)
                .build();
    }
}
