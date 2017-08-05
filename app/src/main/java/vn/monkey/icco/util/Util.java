package vn.monkey.icco.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import vn.monkey.icco.R;
import vn.monkey.icco.activity.WelcomeActivity;
import vn.monkey.icco.model.User;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class Util {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * get user cached
     *
     * @param context
     * @return
     */
    public static User getUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        User user = new User();
        user.setUsername(settings.getString(KeyConstant.APP_CODE + "username", ""));
        user.setToken(settings.getString(KeyConstant.APP_CODE + "token", ""));
        user.setFullName(settings.getString(KeyConstant.APP_CODE + "full_name", ""));
        user.setAvatarUrl(settings.getString(KeyConstant.APP_CODE + "avatar", ""));
        Log.d("cache token", user.getToken());
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
     * get MD5
     *
     * @param sMessage
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(String sMessage) throws NoSuchAlgorithmException {
        byte[] defaultBytes = sMessage.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {
            return null;
        }
    }

    public static void showToastMessage(Context context, String mess) {
        mess = mess.replace("\\n", "\r");
        Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
    }

    /**
     * get authen token
     *
     * @param context
     * @return
     */
    public static String getAuthenToken(Context context) {
        return String.format(AppConfig.HEADER_VALUE, getUser(context).getToken());
    }


    /**
     * check is valid email
     *
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    /**
     * get welcome data
     *
     * @param myApplication
     * @return
     */
    public static String getWelcomeData(Context myApplication) {
        SharedPreferences settings =
                myApplication.getSharedPreferences("APP", Context.MODE_PRIVATE);
        return settings.getString(KeyConstant.APP_CODE + "welcome-data", "");
    }

    public static void saveCurrentPassword(Context context, String pass) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        settings.edit().putString("password", pass).apply();
    }

    public static String getCurrentPass(Context context) {
        SharedPreferences settings = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        return settings.getString("password", "");
    }

    public static void backToMain(Context context, Activity activity) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Manager.clearCacheUser(context);
        context.startActivity(intent);
        activity.finish();
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static String getDateFromLong(Long date, String format, boolean isGMT) {
        if (date == null) return null;
        try {
            SimpleDateFormat fm = new SimpleDateFormat(format);
            if (isGMT) fm.setTimeZone(TimeZone.getTimeZone("GMT"));
            return fm.format(new Date(date * 1000));
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static String getStringFromDate(Date date, String format, boolean isGMT) {
        if (date == null) return null;
        try {
            SimpleDateFormat fm = new SimpleDateFormat(format);
            if (isGMT) fm.setTimeZone(TimeZone.getTimeZone("GMT"));
            return fm.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     *
     */
    public static void disableTouch(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     *
     */
    public static void disableTouch(ProgressBar progressBar, Window window) {
        progressBar.setVisibility(View.VISIBLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     *
     */
    public static void enableTouch(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     *
     */
    public static void enableTouch(ProgressBar progressBar, Window window) {
        progressBar.setVisibility(View.GONE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    /**
     * @param number
     * @return
     */
    public static String formatNumber(Integer number) {
        if (number == null) return "";
        try {
            return String.format("%,d", number);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param context
     * @param time
     * @return
     */
    public static String getDayMonthYear(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time * 1000);
        String day = "";
        try {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            Log.d(KeyConstant.APP_CODE, hour + "");
            String[] monthOfYear = context.getResources().getStringArray(R.array.month_of_year);
            day = calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    monthOfYear[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR) +
                    ", " + (hour > 12 ? (hour - 12) + " PM" : hour + " AM");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return day;
    }

    /**
     * @param context
     * @param time
     * @return
     */
    public static String getDateDayMonth(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time * 1000);
        String day = "";
        try {
            String[] dayOfWeek = context.getResources().getStringArray(R.array.date_of_week);
            String[] monthOfYear = context.getResources().getStringArray(R.array.month_of_year);
            day = dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " +
                    calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    monthOfYear[calendar.get(Calendar.MONTH)];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return day;
    }

    /**
     * @param context
     * @param dp
     * @return
     */
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * @param v
     */
    public static void hiddenKeyboard(View v, Context context) {
        InputMethodManager keyboard =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
