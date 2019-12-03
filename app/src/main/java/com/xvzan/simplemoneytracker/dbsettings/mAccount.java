package com.xvzan.simplemoneytracker.dbsettings;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class mAccount extends RealmObject {
    private boolean editme;
    private int acct;
    private boolean bl1;
    private boolean bl2;
    private int order;
    private boolean hideonpopups;
    private String ainfo;

    //@PrimaryKey
    private String aname;//Realm不允许编辑PrimaryKey，为了编辑名称，只能取消PrimaryKey标注

    public void setAname(String name){
        aname=name;
    }
    public void setAType(int tts){
        switch(tts){
            case 0://Asset
                bl1=false;
                bl2=true;
                break;
            case 1://Liability
            case 4://Equity
                bl1=false;
                bl2=false;
                break;
            case 2://Income
                bl1=true;
                bl2=false;
                break;
            case 3://Expense
                bl1=true;
                bl2=true;
                break;
            default:
                    return;
        }
        acct=tts;
    }
    public String getAname(){return aname;}
    public int getAcct(){return acct;}
    public boolean getBl1(){return bl1;}
    public boolean getBl2(){return bl2;}
    public void setOrder(int i){
        order=i;
    }
    //public boolean getXOR(){return bl1^bl2;}
    public void setEditme(){editme = true;}
    public void meEdited(){editme = false;}
}
