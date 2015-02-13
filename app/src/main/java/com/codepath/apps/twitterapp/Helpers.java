package com.codepath.apps.twitterapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.codepath.apps.twitterapp.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Helpers {
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";

        Date date;
        try {
            date = sf.parse(rawJsonDate);
            relativeDate = getTimeString(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getTimeString(Date fromdate) {

        long then;
        then = fromdate.getTime();
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        if (days == 0) {

            int second = minuteBetween(calendar.getTime(), now.getTime());
            if (minutes > 60) {

                if (hours >= 1 && hours <= 24) {
                    dateStr.append(hours).append("h");
                }

            } else {

                if (second <= 10) {
                    dateStr.append("Now");
                } else if (second > 10 && second <= 30) {
                    dateStr.append("few seconds ago");
                } else if (second > 30 && second <= 60) {
                    dateStr.append(second).append("s");
                } else if (second >= 60 && minutes <= 60) {
                    dateStr.append(minutes).append("m");
                }
            }
        } else

        if (hours > 24 && days <= 7) {
            dateStr.append(days).append("d");
        } else {
            dateStr.append(DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        }

        return dateStr.toString();
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public static void storeProfileInSharedPrefs(Context context, User user) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong("currentUserId", user.getUid());
        edit.putString("currentUserName", user.getName());
        edit.putString("currentUserScreenName", user.getScreenName());
        edit.putString("currentUserProfileImage", user.getProfileImageUrl());
        edit.putString("currentUserTagline", user.getTagline());
        edit.putInt("currentUserFollowersCount", user.getFollowersCount());
        edit.putInt("currentUserFollowingCount", user.getFollowingCount());
        edit.apply();
    }

    public static User fetchProfileFromSharedPrefs(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        User user = new User();

        user.setUid(pref.getLong("currentUserId", 0));
        user.setName(pref.getString("currentUserName", ""));
        user.setScreenName(pref.getString("currentUserScreenName", ""));
        user.setProfileImageUrl(pref.getString("currentUserProfileImage", ""));
        user.setTagline(pref.getString("currentUserTagline", ""));
        user.setFollowersCount(pref.getInt("currentUserFollowersCount", 0));
        user.setFollowingCount(pref.getInt("currentUserFollowingCount", 0));
        
        return user;
    }

    public static boolean iAmOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
    }

}
