package com.xvzan.simplemoneytracker.dbsettings;


import java.util.Date;

import io.realm.RealmObject;

public class mTra extends RealmObject {
    private boolean editMe;
    private mAccount accU;
    private mAccount accB;
    private long deltaAmount;
    private long uAm;
    private long bAm;
    private Date mDate;
    private String mNote;

    public void allSet(mAccount u, mAccount b, long am, Date d) {
        accU = u;
        accB = b;
        deltaAmount = am;
        setAllAmount();
        mDate = d;
    }

    public void directSet(mAccount u, mAccount b, long dam, long uam, long bam, Date d) {
        accU = u;
        accB = b;
        deltaAmount = dam;
        uAm = uam;
        bAm = bam;
        mDate = d;
    }

    public void setAllAmount() {
        if (accB.getAcct() == 4) {
            uAm = deltaAmount;
            if (accU.getBl1() && !accU.getBl2()) {
                bAm = -deltaAmount;
            } else {
                bAm = deltaAmount;
            }
            return;
        }
        if (accU.getAcct() == 4) {
            if (accB.getBl1() && accB.getBl2())
                bAm = deltaAmount;
            else
                bAm = -deltaAmount;
            if (accB.getBl1())
                uAm = deltaAmount;
            else
                uAm = -deltaAmount;
            return;
        }
        if (accU.getBl1() || accU.getBl2() || (accB.getBl1() && accB.getBl2())) {
            uAm = deltaAmount;
        } else {
            uAm = -deltaAmount;
        }
        bAm = (accU.getBl2() ^ accB.getBl2()) ? uAm : -uAm;
    }

    public void setmNote(String note) {
        mNote = note;
    }

    public mAccount getAccU() {
        return accU;
    }

    public mAccount getAccB() {
        return accB;
    }

    public Date getmDate() {
        return mDate;
    }

    public long getuAm() {
        return uAm;
    }

    public long getbAm() {
        return bAm;
    }

    public long getDeltaAmount() {
        return deltaAmount;
    }

    public String getmNote() {
        return mNote;
    }

    public void setEditme() {
        editMe = true;
    }

    public void meEdited() {
        editMe = false;
    }
}