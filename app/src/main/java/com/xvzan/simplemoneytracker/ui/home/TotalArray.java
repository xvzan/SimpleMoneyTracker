package com.xvzan.simplemoneytracker.ui.home;

import android.os.Process;

import com.xvzan.simplemoneytracker.dbsettings.mTra;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class TotalArray implements Runnable {

    private String accstr;
    private Realm realmInstance;
    private Thread mCurrentThread;
    //private Adapter_Single adapter_single;
    private int size;

    TotalArray(String str, int i) {
        accstr = str;
        size = i;
    }

    public Thread getCurrentThread() {
        return mCurrentThread;
    }

    /*
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        mCurrentThread = thread;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        setCurrentThread(Thread.currentThread());
        TotalManager.getInstance().threads.add(getCurrentThread());
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try (Realm realm = Realm.getDefaultInstance()) {
                OrderedRealmCollection<mTra> mTraList = realm.where(mTra.class).equalTo("accU.aname", accstr).or().equalTo("accB.aname", accstr).findAll().sort("mDate", Sort.ASCENDING);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                size = mTraList.size();
                Long[] totalList = new Long[size];
                long i = 0;
                for (int a = 0; a < mTraList.size(); a++) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    if (mTraList.get(a).getAccU().getAname().equals(accstr)) {
                        i += mTraList.get(a).getuAm();
                    } else {
                        i += mTraList.get(a).getbAm();
                    }
                    totalList[a] = i;
                }
                TotalManager.getInstance().arrayCompleteHandle(1, totalList);
            }
        } catch (InterruptedException e1) {
            TotalManager.getInstance().threads.remove(getCurrentThread());
        } finally {
            // Clears the Thread's interrupt flag
            TotalManager.getInstance().threads.remove(getCurrentThread());
        }
    }
}
