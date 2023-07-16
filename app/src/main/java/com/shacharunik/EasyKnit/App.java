package com.shacharunik.EasyKnit;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataController.init(this);
        SignalGenerator.init(this);
    }
}
