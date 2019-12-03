package com.xvzan.simplemoneytracker.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class TotalManager {
    private static final int KEEP_ALIVE_TIME = 1;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor mThreadPool;
    private final BlockingQueue<Runnable> mWorkQueue;
    private Adapter_Single adapter_single;
    ArrayList<Thread> threads;
    private RecyclerView recyclerView;
    private ProgressBar homeProgress;
    private Context context;
    private Handler mHandler;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static TotalManager sInstance = null;
    private Realm realm;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new TotalManager();
    }

    static TotalManager getInstance(){
        return sInstance;
    }

    void setRecyclerView(RecyclerView rv,Context c,TotalArray array,Realm instance,ProgressBar home){
        recyclerView = rv;
        context = c;
        realm = instance;
        homeProgress = home;
        mThreadPool.execute(array);
        adapter_single = new Adapter_Single(context,array,realm);
    }

    void setDoubleView(RecyclerView rv,Context c){
        recyclerView = rv;
        context = c;
    }

    void arrayCompleteHandle(int state, TotalArray totalArray){
        Message complete = mHandler.obtainMessage(state,totalArray);
        complete.sendToTarget();
    }

    static void cancelAll() {
        for (Thread t:sInstance.threads
             ) {
            t.interrupt();
        }
    }

    private TotalManager(){
        mWorkQueue = new LinkedBlockingQueue<Runnable>();
        threads = new ArrayList<>();
        mThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mWorkQueue);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                TotalArray totalArray = (TotalArray) inputMessage.obj;
                //recyclerView.setLayoutManager(new LinearLayoutManager(context));
                switch (inputMessage.what){
                    case 0:
                        /*
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        Adapter_Double adapter_double = new Adapter_Double(context,totalArray);
                        recyclerView.setAdapter(adapter_double);
                        recyclerView.scrollToPosition(adapter_double.getItemCount()-1);
                         */
                        break;
                    case 1:
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(adapter_single);
                        recyclerView.scrollToPosition(adapter_single.getItemCount()-1);
                        homeProgress.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
    }
}
