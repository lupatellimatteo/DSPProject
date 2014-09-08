package com.matteolupatelli.dspproject.app;

import android.app.Application;
import com.baasbox.android.BaasBox;

/**
 * Created by matteo on 04/09/14.
 */


public class RecipeApplication extends android.app.Application {


    private BaasBox client;
    private static RecipeApplication self;

    @Override
    public void onCreate() {
        super.onCreate();
        BaasBox.Builder b =
                new BaasBox.Builder(this);
        client = b.setApiDomain(Configuration.API_DOMAIN)
                .setAppCode(Configuration.APPCODE)
                .setPort(Configuration.PORT)
                .init();
    }

    public static final RecipeApplication app(){
        return self;
    }

    public static final BaasBox box(){
        return self.client;
    }
}
