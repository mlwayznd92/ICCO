package vn.monkey.icco.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.GAP;
import vn.monkey.icco.model.Location;
import vn.monkey.icco.model.News;
import vn.monkey.icco.model.QuestionAnswer;
import vn.monkey.icco.model.User;
import vn.monkey.icco.model.UserResponse;

/**
 * Created by Mlwayz on 6/24/2017.
 */

public class Manager {

    public static boolean isChooseGPS = false;
    public static User USER;
    public static HashMap<String, Location> MAPS;
    public static List<News> NEWS;
    public static List<GAP> GAPS;
    public static List<QuestionAnswer> QAS;

    public static Long LAST_RELOAD_MAPS;
    public static Long LAST_RELOAD_NEWS;
    public static Long LAST_RELOAD_GAPS;
    public static Long LAST_RELOAD_QAS;

    public static HashMap<Long, Integer> LAST_PAGE_NEWS;
    public static Integer LAST_PAGE_GAPS;
    public static Integer LAST_PAGE_QAS;


    /**
     * call api to load user information
     */
    public static void loadUserInformation(final CustomApplication customApplication) {
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<UserResponse> call =
                customApplication.apiService.getUserInfo(headerAuthen, KeyConstant.PHONE_REGISTER);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response == null || response.body() == null) {
                    Log.e(KeyConstant.APP_CODE, "load user response null!");
                    return;
                }

                if (response.body().success) {
                    UserResponse userResponse = response.body();
                    User user = new User();
                    user.setId(userResponse.data.id);
                    user.setMsisdn(userResponse.data.msisdn);
                    user.setUsername(userResponse.data.username);
                    user.setToken(Manager.USER.getToken());
                    user.setEmail(userResponse.data.email);
                    user.setFullName(userResponse.data.full_name);
                    user.setBirthday(userResponse.data.birthday);
                    user.setSex(userResponse.data.sex);
                    user.setLastLogin(userResponse.data.last_login_at);
                    user.setCreatedDate(userResponse.data.created_at);
                    user.setUpdatedDate(userResponse.data.updated_at);
                    user.setAvatarUrl(userResponse.data.avatar);
                    user.setAddress(userResponse.data.address);
                    Manager.USER = user;
                    Manager.cacheUser(customApplication, user);
                } else {
                    Manager.logout(customApplication);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * get user cached
     *
     * @param context
     * @return
     */
    public static User getUserCached(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        User user = new User();
        user.setUsername(settings.getString(KeyConstant.APP_CODE + "username", ""));
        user.setToken(settings.getString(KeyConstant.APP_CODE + "token", ""));
        user.setFullName(settings.getString(KeyConstant.APP_CODE + "full_name", ""));
        user.setAvatarUrl(settings.getString(KeyConstant.APP_CODE + "avatar", ""));
        return user;
    }

    /**
     * cache user
     *
     * @param context
     * @param user
     */
    public static void cacheUser(Context context, User user) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        settings.edit().putString(KeyConstant.APP_CODE + "username", user.getUsername()).apply();
        settings.edit().putString(KeyConstant.APP_CODE + "token", user.getToken()).apply();
        settings.edit().putString(KeyConstant.APP_CODE + "full_name", user.getFullName()).apply();
        settings.edit().putString(KeyConstant.APP_CODE + "avatar", user.getAvatarUrl()).apply();
    }

    /**
     * get language
     *
     * @param context
     * @return
     */
    public static String getLanguage(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        return settings.getString(KeyConstant.APP_CODE + "language", "");
    }

    /**
     * save language
     *
     * @param context
     * @return
     */
    public static void saveLanguage(Context context, String language) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        settings.edit().putString(KeyConstant.APP_CODE + "language", language).apply();
    }

    /**
     * clear cache user
     *
     * @param context
     */
    public static void clearCacheUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        settings.edit().putString(KeyConstant.APP_CODE + "username", "").apply();
        settings.edit().putString(KeyConstant.APP_CODE + "token", "").apply();
        settings.edit().putString(KeyConstant.APP_CODE + "full_name", "").apply();
        settings.edit().putString(KeyConstant.APP_CODE + "avatar", "").apply();
    }


    /**
     * save firebase token
     *
     * @param context
     * @param firebaseToken
     */
    public static void saveFirebaseToken(Context context, String firebaseToken) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        settings.edit().putString(KeyConstant.APP_CODE + "firebase_token", firebaseToken).apply();
    }

    /**
     * get firebase token
     *
     * @param context
     * @return
     */
    public static String getFirebaseToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        return settings.getString(KeyConstant.APP_CODE + "firebase_token", "");
    }

    /**
     * check user login
     *
     * @return
     */
    public static boolean isLogin() {
        if (Manager.USER != null && Manager.USER.getToken() != null &&
                !TextUtils.isEmpty(Manager.USER.getToken())) {
            return true;
        }
        return false;
    }


    /**
     * logout
     */
    public static void logout(Context context) {
        Manager.USER = null;
        clearCacheUser(context);
    }

    /**
     * get user token
     */
    public static String getToken() {
        if (Manager.USER != null) return Manager.USER.getToken();
        return "";
    }
}
