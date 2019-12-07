package com.alegangames.master.util.preference;

import android.content.Context;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobRequest;

public class UtilPreference {

    public static boolean isFirstLaunch(Context context) {
        return SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 0) == 1;
    }

    public static boolean isSecondLaunch(Context context) {
        return SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 0) == 2;
    }

    public static void addLaunchPref(Context context) {
        int i = SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 0);
        SharedPreferenceManager.getInstance(context).putInt(context.getString(R.string.launch_pref), ++i);
    }

    /**
     * @param bool Пользователь ответил на опрос о возрасте
     */
    public static void setUnderAgePref(Context context, boolean bool) {
        SharedPreferenceManager.getInstance(context).putBoolean(context.getString(R.string.under_age_pref), bool);
    }

    public static void setAge(Context context, int age){
        SharedPreferenceManager.getInstance(context).putInt("age", age);
    }

    public static int getAge(Context context){
        return SharedPreferenceManager.getInstance(context).getInt("age", -1);
    }

    /**under_age_pref
     * @@return bool Пользователь ответил на опрос о возрасте
     */
    public static boolean getUnderAgePref(Context context) {
        return SharedPreferenceManager.getInstance(context).getBoolean(context.getString(R.string.under_age_pref), false);
    }

    /**
     * Получить метку возраста согласия пользователя в Европейском экономическом союзе (EEA).
     *
     * @param context
     * @return true если пользователь ниже возраста согласия, меньше 18.
     * false если пользователь выше возраста согласия, больше 18.
     */
    public static boolean isUserUnderAgeOfConsentPref(Context context) {
        return SharedPreferenceManager.getInstance(context).getBoolean(context.getString(R.string.under_age_of_consent_pref), false);
    }

    /**
     * Вы можете пометить пользователей в Европейском экономическом союзе (EEA) в возрасте до достижения ими возраста согласия.
     *
     * @param context
     * @param bool    true если пользователь ниже возраста согласия, меньше 18.
     *                false если пользователь выше возраста согласия, больше 18.
     */
    public static void setUserUnderAgeOfConsent(Context context, boolean bool) {
        SharedPreferenceManager.getInstance(context).putBoolean(context.getString(R.string.under_age_of_consent_pref), bool);
    }

    public static String getMaxAdContentRatingPref(Context context) {
        return SharedPreferenceManager.getInstance(context).getString(context.getString(R.string.max_ad_content_rating_pref), "");
    }

    public static void setMaxAdContentRatingPref(Context context, String value) {
        SharedPreferenceManager.getInstance(context).putString(context.getString(R.string.max_ad_content_rating_pref), value);
    }

    public static void setNonPersonAdsPref(Context context, String value) {
        SharedPreferenceManager.getInstance(context).putString(context.getString(R.string.non_person_ads_pref), value);
    }

    public static String getNonPersonAdsPref(Context context) {
        return SharedPreferenceManager.getInstance(context).getString(context.getString(R.string.non_person_ads_pref), "0");
    }

    /**
     * Для целей закона о защите конфиденциальности детей в Интернете (COPPA).
     *
     * @return true ваш контент рассматривается как ориентированный на детей для целей COPPA,
     * false ваш контент не рассматривается как ориентированный на детей для целей COPPA
     */
    public static boolean isChildDirectedTreatmentPref(Context context) {
        return SharedPreferenceManager.getInstance(context).getBoolean(context.getString(R.string.child_directed_treatment_pref), false);
    }

    /**
     * Для целей закона о защите конфиденциальности детей в Интернете (COPPA).
     *
     * @param bool указать true, если вы хотите, чтобы ваш контент рассматривался как ориентированный на детей для целей COPPA
     *             указать false если вы не хотите, чтобы ваш контент рассматривался как ориентированный на детей для целей COPPA.
     */
    public static void setChildDirectedTreatmentPref(Context context, boolean bool) {
        SharedPreferenceManager.getInstance(context).putBoolean(context.getString(R.string.child_directed_treatment_pref), bool);
    }
}
