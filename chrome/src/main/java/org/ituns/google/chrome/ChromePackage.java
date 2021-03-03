package org.ituns.google.chrome;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

class ChromePackage {
    private static final String PACKAGE_STABLE = "com.android.chrome";
    private static final String PACKAGE_BETA = "com.chrome.beta";
    private static final String PACKAGE_DEV = "com.chrome.dev";
    private static final String PACKAGE_LOCAL = "com.google.android.apps.chrome";
    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private String chromePackageName = null;

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is <strong>not</strong> threadsafe.
     *
     * @param context {@link Context} to use for accessing {@link PackageManager}.
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    public String getPackageName(Context context) {
        if (chromePackageName != null) {
            return chromePackageName;
        }

        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(intent, MATCH_DEFAULT_ONLY);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(intent, MATCH_DEFAULT_ONLY);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            chromePackageName = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            chromePackageName = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, intent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            chromePackageName = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_STABLE)) {
            chromePackageName = PACKAGE_STABLE;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_BETA)) {
            chromePackageName = PACKAGE_BETA;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_DEV)) {
            chromePackageName = PACKAGE_DEV;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_LOCAL)) {
            chromePackageName = PACKAGE_LOCAL;
        }
        return chromePackageName;
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(intent,
                    PackageManager.GET_RESOLVED_FILTER);
            if (handlers == null || handlers.size() == 0) {
                return false;
            }

            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) {
                    continue;
                }

                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                    continue;
                }

                if (resolveInfo.activityInfo == null) {
                    continue;
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
