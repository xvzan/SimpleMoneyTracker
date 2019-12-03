package com.xvzan.simplemoneytracker.ui.home;

import android.os.Process;

import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class TotalArray implements Runnable{

    private String accstr;
    private Realm realmInstance;
    private OrderedRealmCollection<mTra> mTraList;
    private Thread mCurrentThread;
    private TotalManager totalManager;
    private Long[] totalList;
    private int size;

    TotalArray(String accs){
        accstr = accs;
        totalManager = TotalManager.getInstance();
    }

    TotalArray(Realm instance){
        realmInstance = instance;
        mTraList = realmInstance.where(mTra.class).findAllAsync().sort("mDate", Sort.ASCENDING);
        size = mTraList.size();
        TotalManager.getInstance().arrayCompleteHandle(0,this);
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

    String getAccstr(){
        return accstr;
    }

    @Override
    public void run() {
        //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        setCurrentThread(Thread.currentThread());
        TotalManager.getInstance().threads.add(getCurrentThread());
        try {
            if (Thread.currentThread().interrupted()) {
                throw new InterruptedException();
            }
            try(Realm realm = Realm.getDefaultInstance()){
                mTraList = Realm.getDefaultInstance().where(mTra.class).equalTo("accU.aname", accstr).or().equalTo("accB.aname",accstr).findAll().sort("mDate", Sort.ASCENDING);
                if (Thread.currentThread().interrupted()) {
                    throw new InterruptedException();
                }
                totalList = new Long[mTraList.size()];
                size = mTraList.size();
                long i = 0;
                for (int a=0;a<mTraList.size();a++) {
                    if (Thread.currentThread().interrupted()) {
                        throw new InterruptedException();
                    }
                    if(mTraList.get(a).getAccU().getAname().matches(accstr)){
                        i+=mTraList.get(a).getuAm();
                    }
                    else {
                        i+=mTraList.get(a).getbAm();
                    }
                    totalList[a]=i;
                }
            }
            TotalManager.getInstance().arrayCompleteHandle(1,this);
        } catch (InterruptedException e1) {
            TotalManager.getInstance().threads.remove(getCurrentThread());
            Thread.interrupted();
            return;
        } finally {
            // Clears the Thread's interrupt flag
            TotalManager.getInstance().threads.remove(getCurrentThread());
            Thread.interrupted();
        }

        /*
        if(accstr.matches("")){
            findAll();
        }
        else{
            findCat();
        }

         */
    }

    void findCat(){
        //mTraList = realmInstance.where(mTra.class).equalTo("accU.aname", accstr).or().equalTo("accB.aname",accstr).findAll().sort("mDate", Sort.ASCENDING);
        totalList = new Long[mTraList.size()];
        /*
        long i = 0;
        for (int a=0;a<mTraList.size();a++) {
            if(mTraList.get(a).getAccU().getAname().matches(accstr)){
                i+=mTraList.get(a).getuAm();
            }
            else {
                i+=mTraList.get(a).getbAm();
            }
            totalList[a]=i;
        }

         */
        TotalManager.getInstance().arrayCompleteHandle(1,this);
    }

    void findAll(){
        //mTraList = realmInstance.where(mTra.class).findAllAsync().sort("mDate", Sort.ASCENDING);
        totalList = null;
        TotalManager.getInstance().arrayCompleteHandle(0,this);
    }

    int getSize(){
        return size;
    }

    mTra getMT(int position){
        return mTraList.get(position);
    }

    Long getTotalA(int position){
        if(totalList==null)
            return null;
        return totalList[position];
    }

    Date getDate(int position){
        return mTraList.get(position).getmDate();
    }

    String getAccUN(int position){
        return mTraList.get(position).getAccU().getAname();
    }

    String getAccBN(int position){
        return mTraList.get(position).getAccB().getAname();
    }

    Long getUAM(int position){
        return mTraList.get(position).getuAm();
    }
}
