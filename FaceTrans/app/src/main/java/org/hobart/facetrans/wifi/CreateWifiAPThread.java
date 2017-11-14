package org.hobart.facetrans.wifi;

import android.os.CountDownTimer;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.ApCreateEvent;

/**
 * 创建Wi-Fi热点线程
 * Created by huzeyin on 2017/11/6.
 */

public class CreateWifiAPThread implements Runnable {

    private static final int WIFI_CREATE_TIME_OUT = 15 * 1000;

    public CreateWifiAPThread() {
    }

    @Override
    public void run() {
        mDownTimer.start();
    }

    CountDownTimer mDownTimer = new CountDownTimer(WIFI_CREATE_TIME_OUT, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished / 1000 > 0) {
                checkApCreateSuccess();
            }
        }

        @Override
        public void onFinish() {
            apCreateFailed();
        }
    };

    private void checkApCreateSuccess() {
        if (ApWifiHelper.getInstance().isWifiApEnabled()) {
            ApCreateEvent event = new ApCreateEvent();
            event.status = ApCreateEvent.SUCCESS;
            EventBus.getDefault().post(event);
            mDownTimer.cancel();
        }
    }

    private void apCreateFailed() {
        ApCreateEvent event = new ApCreateEvent();
        event.status = ApCreateEvent.FAILED;
        EventBus.getDefault().post(event);
    }

    public void cancelDownTimer() {
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
    }
}
