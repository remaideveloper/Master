package com.alegangames.master.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alegangames.master.R;
import com.alegangames.master.fragment.FragmentViewPager;
import com.alegangames.master.model.enums.JsonItemFragmentEnum;

import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_BANNER;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_COLUMN;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_DATA;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_INTERSTITIAL;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_SHUFFLE;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_TITLE;

public class FragmentUtil {

    private static final String TAG = FragmentUtil.class.getSimpleName();

    /**
     * @return Возвращает true если в стеке текущего активити есть более одного фрагмента
     */
    public static boolean isNotLastFragment(FragmentActivity activity) {
        return activity.getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    /**
     * @return Возвращает true если в стеке текущего активити один фрагмент
     */
    public static boolean isLastFragment(FragmentActivity activity) {
        return activity.getSupportFragmentManager().getBackStackEntryCount() == 1;
    }

    public static void onTransactionFragmentByItem(@Nullable FragmentActivity activity, @IdRes int containerViewId, @Nullable JsonItemFragmentEnum item) {
        try {
            if (activity != null && item != null && item.getFragment() != null) {
                Fragment fragment = new FragmentViewPager();
                if (fragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FRAGMENT_TITLE, item.getTitle());
                    bundle.putString(FRAGMENT_DATA, item.getData());
                    bundle.putBoolean(FRAGMENT_BANNER, item.isBanner());
                    bundle.putBoolean(FRAGMENT_INTERSTITIAL, item.isInterstitial());
                    bundle.putBoolean(FRAGMENT_SHUFFLE, item.isShuffle());
                    bundle.putInt(FRAGMENT_COLUMN, item.getColumn());
//                    bundle.putString(FRAGMENT_SETTINGS, item.getSettings());
                    fragment.setArguments(bundle);

                    onTransactionFragment(activity, containerViewId, fragment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    public static void onTransactionFragment(@Nullable FragmentActivity activity, @IdRes int containerViewId, @Nullable Fragment fragment) {
        try {
            if (activity != null && fragment != null) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(containerViewId, fragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    /**
     * Возвращает предыдущий фрагмент
     */
    public static void onBackFragment(@Nullable FragmentActivity activity, @IdRes int containerViewId) {
        try {
            if (activity != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentById(containerViewId);
                if (fragment != null && fragment.isVisible()) {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentManager.popBackStackImmediate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    public static void onTransactionFragmentByName(@Nullable FragmentActivity activity, @Nullable String fragmentName) {
        Log.d(TAG, "onTransactionFragmentByName: " + fragmentName);
        if (activity != null && fragmentName != null) {
            switch (fragmentName) {
//                case "#SkinsEditor":
//                    activity.startActivity(new Intent(activity, ActivitySkinEditor.class));
//                    break;
//                case "#CraftGuide":
//                    activity.startActivity(new Intent(activity, ActivityCraftGuide.class));
//                    break;
                default:
                    FragmentUtil.onTransactionFragmentByItem(
                            activity,
                            R.id.main_container,
                            getNavItemByTitle(fragmentName));
                    break;
            }
        }
    }

    /**
     * @param title Заголовок
     * @return JsonItemFragment с совпадающим заголовком
     */
    @Nullable
    private static JsonItemFragmentEnum getNavItemByTitle(@Nullable String title) {
        for (JsonItemFragmentEnum item : JsonItemFragmentEnum.values()) {
            if (title != null && title.equals(item.getTitle())) {
                return item;
            }
        }
        return null;
    }

}
