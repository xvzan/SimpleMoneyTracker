package com.xvzan.simplemoneytracker.ui.home;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TotalManager {
    private static final int KEEP_ALIVE_TIME = 1;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor mThreadPool;
    //private final BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingQueue<Runnable>();
    private Adapter_Single adapter_single;
    public ArrayList<Thread> threads;
    private ProgressBar homeProgress;
    private Handler mHandler;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static TotalManager sInstance = null;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new TotalManager();
    }

    public static TotalManager getInstance() {
        return sInstance;
    }

    void setRecyclerView(Adapter_Single adapterSingle, TotalArray array, ProgressBar home) {
        adapter_single = adapterSingle;
        homeProgress = home;
        mThreadPool.execute(array);
    }

    void arrayCompleteHandle(int state, Long[] longArray) {
        Message complete = mHandler.obtainMessage(state, longArray);
        complete.sendToTarget();
    }

    public void sumCompleteHandle(int state, ArrayList longArray) {
        Message complete = mHandler.obtainMessage(state, longArray);
        complete.sendToTarget();
    }

    static void cancelAll() {
        for (Thread t : sInstance.threads
        ) {
            t.interrupt();
        }
    }

    private TotalManager() {
        threads = new ArrayList<>();
        mThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case 0:
                        break;
                    case 1:
                        Long[] longs = (Long[]) inputMessage.obj;
                        adapter_single.longs = longs;
                        adapter_single.notifyDataSetChanged();
                        homeProgress.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
    }
}
