package com.surya.onspot.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static SharedPreferences mAppPreferences;
    public static SharedPreferences.Editor mEditor;
    private static boolean printLogs = true;

    public static void showGPSDisabledAlertToUser(final AppCompatActivity actionBarActivity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(actionBarActivity);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it.")
                .setCancelable(false)
                .setPositiveButton("Location Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                actionBarActivity.startActivity(callGPSSettingIntent);
                            }
                        });
//        alertDialogBuilder.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private static TelephonyManager getTelephonyManager(Context ctx) {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager;
    }

    public static String getDeviceID(Context ctx) {
        return getTelephonyManager(ctx).getDeviceId();
    }

    public static void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, String Message, String Title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        alert.setTitle(Title);
        alert.setMessage(Message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onPositiveButtonClick();
            }
        });
        alert.show();
    }

    public static void showToast(Context ctx, String Message) {
        Toast.makeText(ctx, Message, Toast.LENGTH_SHORT).show();
    }

    public static String addPreference(Context context, String pref_field, String pref_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mAppPreferences.edit();
        mEditor.putString(pref_field, pref_value);
        mEditor.commit();
        return mAppPreferences.getString(pref_field, null);
    }

    public static int addPreferenceInt(Context context, String pref_field, int pref_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mAppPreferences.edit();
        mEditor.putInt(pref_field, pref_value);
        mEditor.commit();
        return mAppPreferences.getInt(pref_field, 0);
    }

    public static boolean addPreferenceBoolean(Context context, String pref_field, Boolean pref_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mAppPreferences.edit();
        mEditor.putBoolean(pref_field, pref_value);
        mEditor.commit();
        return mAppPreferences.getBoolean(pref_field, false);
    }

    public static String getPreference(Context context, String pref_field, String def_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return mAppPreferences.getString(pref_field, def_value);
    }

    public static int getPreferenceInt(Context context, String pref_field, int def_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return mAppPreferences.getInt(pref_field, def_value);
    }

    public static boolean getPreferenceBoolean(Context context, String pref_field, Boolean def_value) {
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return mAppPreferences.getBoolean(pref_field, false);
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatePhoneNumber(String mMobile) {
        boolean isValid = false;
        if (mMobile.length() != 10)
            return false;
        char firstChar = mMobile.charAt(0);
        switch (firstChar) {
            case '9':
                isValid = !mMobile.equalsIgnoreCase("9999999999");
                break;
            case '8':
                isValid = !mMobile.equalsIgnoreCase("8888888888");
                break;
            case '7':
                isValid = !mMobile.equalsIgnoreCase("7777777777");
                break;
            default:
                isValid = false;
        }
        return isValid;
    }

    public static void AlertDialog(final Context context, String Title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(Title);
        alert.setMessage(Message);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //				if((Utils.getPreference(context, PreferenceKeys.USER_ID_TO_LOGIN, "")).equalsIgnoreCase("")){
                //					context.finish();
                //				}
            }
        });
        alert.show();
    }

    public static void AlertDialogFinish(final Activity context, String Title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(Title);
        alert.setMessage(Message);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        //
        //			@Override
        //			public void onClick(DialogInterface dialog, int which) {
        //				dialog.dismiss();
        //			}
        //		});
        alert.show();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static void createIconHashMap() {

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /*
     * getting screen width
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context mContext) {
        int columnWidth;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public static File getThumbnail(String mPath) {
        // TODO Auto-generated method stub
        String thumbPath = mPath.replace("MyRecordImage_", ".thumbnails/MyRecordImage_");
        return new File(thumbPath);
    }

    /**
     * @param tag     = TAG for filtering log
     * @param message = Message to be displayed in logcat
     */
    public static void out(String tag, String message) {
        if (printLogs) {
            int maxLogSize = 4000;
            for (int i = 0; i <= message.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > message.length() ? message.length() : end;
                android.util.Log.d(tag, message.substring(start, end));
            }
        }
    }

    /**
     * @param message = Message to be displayed in logcat
     */
    public static void out(String message) {
        if (printLogs) {
            int maxLogSize = 4000;
            for (int i = 0; i <= message.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > message.length() ? message.length() : end;
                System.out.println(message.substring(start, end));
            }
        }
    }

    public static void setLocale(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static String getCurrentDate() {
//        return new SimpleDateFormat("dd/mm/yyyy hh:mm a").format(new Date()).toString();
        Utils.out("UTILS CURRENT DATE : " + new SimpleDateFormat("yyyy-MMM-dd").format(new Date()));
        return new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
    }

/*    My Address info :
      102, Sector 20 Belapur, Mumbai Navi, Mumbai
      19.0215338,73.0163664,15*/

    public static String getCurrentTime() {
//        return new SimpleDateFormat("dd/mm/yyyy hh:mm a").format(new Date()).toString();
        Utils.out("UTILS CURRENT TIME : " + new SimpleDateFormat("hh:mm a").format(new Date()));
        return new SimpleDateFormat("hh:mm a").format(new Date());
    }

    public static String getFormatedMonth(int monthOfYear) {
        if (monthOfYear <= 9) {
            return "0" + monthOfYear;
        } else
            return String.valueOf(monthOfYear);
    }

    public static String getFormatedDay(int monthOfYear) {
        if (monthOfYear <= 9) {
            return "0" + monthOfYear;
        } else
            return String.valueOf(monthOfYear);
    }

    public interface InterfaceAlertDissmiss {
        void onPositiveButtonClick();
    }
}
