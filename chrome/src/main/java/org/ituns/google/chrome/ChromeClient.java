package org.ituns.google.chrome;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsSession;

public final class ChromeClient {
    private final Context mContext;
    private final ChromePackage mChromePackage;
    private final ChromeSession mChromeSession;

    public ChromeClient(Context context) {
        mContext = getApplicationContext(context);
        mChromePackage = new ChromePackage();
        mChromeSession = new ChromeSession();
    }

    private Context getApplicationContext(Context context) {
        if(context == null) {
            throw new IllegalArgumentException("context == null");
        }

        if(context instanceof Application) {
            return context;
        } else {
            return context.getApplicationContext();
        }
    }

    public void openChrome(String url) throws ChromeException {
        this.openChrome(mContext, url);
    }

    public void openChrome(Context context, String url) throws ChromeException {
        Uri uri = Uri.parse(url);
        if(uri == null) {
            throw new ChromeException("uri is null");
        }

        String packageName = mChromePackage.getPackageName(context);
        if(TextUtils.isEmpty(packageName)) {
            throw new ChromeException("chrome package name is empty");
        }

        try {
            // Build session
            CustomTabsSession session = mChromeSession.session();
            if(session == null) {
                bindCustomTabService();
            } else {
                session.mayLaunchUrl(uri, null, null);
            }

            // Build custom tabs intent
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
            builder.setShowTitle(true);
            builder.setToolbarColor(Color.WHITE);
            builder.setSecondaryToolbarColor(Color.WHITE);
            builder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ituns_chrome_back));

            // Launch uri by custom tabs intent
            CustomTabsIntent tabsIntent = builder.build();
            tabsIntent.intent.setPackage(packageName);
            if(!(context instanceof Activity)) {
                tabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            tabsIntent.intent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, false);
            tabsIntent.launchUrl(context, uri);
        } catch (Exception e) {
            throw new ChromeException("exception when luanche uri:", e);
        }
    }

    private void bindCustomTabService() {
        try {
            String packageName = mChromePackage.getPackageName(mContext);
            CustomTabsClient.bindCustomTabsService(mContext, packageName, mChromeSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindCustomTabService() {
        try {
            mContext.unbindService(mChromeSession);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mChromeSession.release();
        }
    }

    public void release() {
        unbindCustomTabService();
    }
}
