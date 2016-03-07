package project.fightgopher;


import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

public abstract class CountDownTimer {

    private static String TAG = "CountDownTimerTag";


    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mLastTime;
    private long mStopTimeInFuture; //未來停止的時間
    private boolean mCanel = false;
    private boolean pause = false;

    private final int MSG = 1;

    public CountDownTimer(int mMillisInFuture, int mCountdownInterval) {
        
        this.mMillisInFuture = mMillisInFuture;
        this.mCountdownInterval = mCountdownInterval;
        this.mLastTime = mMillisInFuture;
        
    }

    public synchronized void start() {

        this.mStopTimeInFuture = SystemClock.uptimeMillis() + mMillisInFuture;

        
        mHandler.sendMessage(Message.obtain(mHandler, MSG));
        Log.i(TAG, "CountDownTimer Started in " + new Date().toString());
    }

    public synchronized void cancel() {
        mCanel = true;
        mHandler.removeMessages(MSG);
    }

    public synchronized void pause() {
        mHandler.removeMessages(MSG);
        this.pause = true;
    }

    public synchronized void restart() {
        this.pause = false;
        mHandler.sendMessage(Message.obtain(mHandler, MSG));
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountDownTimer.this) {

                if (mCanel)
                    return;


                if (mLastTime <= 0) {
                    onFinish();
                    mCanel = true;
                    this.removeMessages(MSG);

                    Log.e(TAG, "CountDownTimer is Ended in " + new Date().toString());
                } else if (!pause) { //不是暫停狀態才減少時間

                    mLastTime -= mCountdownInterval;
                    onPerIntervalTask();
                    this.sendEmptyMessageDelayed(MSG, mCountdownInterval);

                }
            }
        }
    };

    public abstract void onFinish();

    public abstract void onPerIntervalTask();
}
