package org.ituns.google.chrome;

import android.content.ComponentName;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

class ChromeSession extends CustomTabsServiceConnection {
    private CustomTabsClient mTabsClient;
    private CustomTabsSession mTabsSession;

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        if(client != null) {
            client.warmup(0L);
            mTabsClient = client;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        release();
    }

    public CustomTabsSession session() {
        CustomTabsSession tabsSession = mTabsSession;
        if(tabsSession == null) {
            tabsSession = createSession();
            mTabsSession = tabsSession;
        }
        return tabsSession;
    }

    private CustomTabsSession createSession() {
        CustomTabsClient tabsClient = mTabsClient;
        if(tabsClient == null) {
            return null;
        }
        return tabsClient.newSession(null);
    }

    public void release() {
        mTabsClient = null;
        mTabsSession = null;
    }
}
