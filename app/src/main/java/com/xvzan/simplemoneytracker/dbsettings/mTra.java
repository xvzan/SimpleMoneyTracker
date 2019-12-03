package com.xvzan.simplemoneytracker.dbsettings;


import java.util.Date;
import io.realm.RealmObject;

public class mTra extends RealmObject{
    private boolean editMe;
    private mAccount accU;
    private mAccount accB;
    private long deltaAmount;
    private long uAm;
    private long bAm;
    Date mDate;
    private String mNote;

    public void allSet(mAccount u,mAccount b,long am,Date d){
        accU=u;
        accB=b;
        deltaAmount=am;
        setAllAmount();
        mDate=d;
    }

    public void setAllAmount(){
        if(accU.getBl1()||accU.getBl2()||(accB.getBl1()&&accB.getBl2())){
            uAm=deltaAmount;
        }
        else {
            uAm=-deltaAmount;
            if(accB.getAcct()==4){
                uAm=-uAm;
            }
        }
        bAm=(accU.getBl2()^accB.getBl2())?uAm:-uAm;
    }

    public void setmNote(String note){
        mNote = note;
    }

    public mAccount getAccU(){
        return accU;
    }

    public mAccount getAccB(){
        return accB;
    }

    public Date getmDate(){
        return mDate;
    }

    public long getuAm(){
        return uAm;
    }

    public long getbAm(){
        return bAm;
    }

    public long getDeltaAmount(){return deltaAmount;}

    public String getmNote() {return mNote;}

    public void setEditme(){editMe = true;}

    public void meEdited(){editMe = false;}
}