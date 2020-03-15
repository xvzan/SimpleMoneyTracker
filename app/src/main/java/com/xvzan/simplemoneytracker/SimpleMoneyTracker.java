package com.xvzan.simplemoneytracker;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SimpleMoneyTracker extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("smt.realm").build();
    }
}
