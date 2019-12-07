package com.alegangames.master.util.rules;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alegangames.master.util.preference.UtilPreference;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.alegangames.master.Config;
import com.alegangames.master.ads.admob.AdMobRequest;
import com.alegangames.master.util.ToastUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Политика согласия сбора данных для Европейских пользователей
 */
public class GDPRHelper {

    private static final String TAG = GDPRHelper.class.getSimpleName();
    private Context mContext;
    private ConsentForm mConsentForm;
    private ConsentInformation mConsentInformation;


    private GDPRHelper(Context context) {
        mContext = context;
        mConsentInformation = ConsentInformation.getInstance(context);
//        mConsentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
    }

    public static boolean isRequestLocationInEeaOrUnknown(Context context) {
        return new GDPRHelper(context).mConsentInformation.isRequestLocationInEeaOrUnknown();
    }

    public static void getRequestConsentInfo(Context context) {
        new GDPRHelper(context).getRequestConsentInfo();
    }

    public static void getConsentForm(Context context) {
        try {
            new GDPRHelper(context).getConsentForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Узнать является ли пользователь гражданином Европейского союза
     * Пометить что пользователь, не достиг возраста согласия
     */
    public static void setTagForUnderAgeOfConsent(Context context) {
        if (isRequestLocationInEeaOrUnknown(context)) {
            new GDPRHelper(context).setTagForUnderAgeOfConsent();
            UtilPreference.setUserUnderAgeOfConsent(context, true);
        }

    }

    private void setTagForUnderAgeOfConsent() {
        if (mConsentInformation.isRequestLocationInEeaOrUnknown()) {
            mConsentInformation.setTagForUnderAgeOfConsent(true);
        }
    }

    /**
     * Отправляем запрос согласия пользователя
     */
    private void getRequestConsentInfo() {
        String[] publisherIds = {Config.ADMOB_PUB_ID};

        mConsentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, "onConsentInfoUpdated: ConsentStatus " + consentStatus);
                //Если пользователь находится в Европейской экономической зоне
                if (mConsentInformation.isRequestLocationInEeaOrUnknown()) {
                    // User's consent status successfully updated.
                    switch (consentStatus) {
                        case PERSONALIZED:
                            //Пользователь уже дал согласие
                            //По умолчанию рекалама персонализированная
                            break;
                        case NON_PERSONALIZED:
                            //Пользователь уже дал согласие
                            //Переслать согласие на SDK Google Mobile Ads
                            //Пользователь выбрал не персоналиризованную реламу
                            setNonPersonAds();
                            break;
                        case UNKNOWN:
                            //Запросить диалоговое окно
                            try {
                                if (UtilPreference.getAge(mContext) >= 16)
                                    getConsentForm();
                                else
                                    setNonPersonAds();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Log.d(TAG, "onFailedToUpdateConsentInfo: ErrorDescription " + errorDescription);
                ToastUtil.show(mContext, errorDescription);
            }
        });
    }

    /**
     * Показать диалог согласия
     */
    private void getConsentForm() {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(Config.PRIVATE_POLICY_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        mConsentForm = new ConsentForm.Builder(mContext, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        Log.d(TAG, "onConsentFormLoaded");
                        if (!((Activity) mContext).isFinishing()) {
                            //show dialog
                            mConsentForm.show();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        Log.d(TAG, "onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "onConsentFormClosed: ConsentStatus " + consentStatus.name());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                //Пользователь уже дал согласие
                                //Переслать согласие на SDK Google Mobile Ads
                                //По умолчанию рекалама персонализированная
                                break;
                            case NON_PERSONALIZED:
                                //Пользователь уже дал согласие
                                //Переслать согласие на SDK Google Mobile Ads
                                //Пользователь выбрал не персоналиризованную реламу
                                setNonPersonAds();
                                break;
                            case UNKNOWN:
                                break;
                        }

                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d(TAG, "onConsentFormError: ErrorDescription " + errorDescription);
                        ToastUtil.show(mContext, errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
//                .withAdFreeOption()
                .build();
        mConsentForm.load();
    }

    private void setNonPersonAds() {
        UtilPreference.setNonPersonAdsPref(mContext, AdMobRequest.EXTRA_NPA_VALUE);
    }

}
