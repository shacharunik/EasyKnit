package com.shacharunik.EasyKnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

public class SignalGenerator {

    @SuppressLint("StaticFieldLeak")
    private static SignalGenerator instance;
    private final Context context;
    private Toast toast;


    private SignalGenerator(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SignalGenerator(context);
        }
    }

    public static SignalGenerator getInstance() {
        return instance;
    }

    public void showToast(String text, int length) {
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);

        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(length, 1000) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };

        toast.show();
        toastCountDown.start();
    }
}
