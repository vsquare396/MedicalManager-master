package bankzworld.com.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;

public class SharedPreferenceHandler {
    private final static String USER_PREFS = "MEDICAL_PREF";

    //get User token from SharedPreference
    public static String getUserToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String userDetailsAsString = preferences.getString("username", "");
        return userDetailsAsString;
    }

    //Save User token from SharedPreference
    public static void saveUserToken(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString("username", userName).commit();
    }

}
