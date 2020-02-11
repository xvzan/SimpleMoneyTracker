package com.xvzan.simplemoneytracker.ui.balances;

import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.util.ArrayList;

import io.realm.Realm;

public class B2_Calc {

    public ArrayList<slPair> pairs;
    private String[] accountTypes;

    public B2_Calc(String[] strings) {
        accountTypes = strings;
    }

    class slPair {
        String string;
        Long aLong;
        boolean noLong = false;
        boolean isTotal = false;

        slPair(String s, Long l, boolean bool) {
            string = s;
            aLong = l;
            isTotal = bool;
        }

        slPair(String s) {
            string = s;
            noLong = true;
        }
    }

    public void calculate() {
        pairs = new ArrayList<>();
        try (Realm realm = Realm.getDefaultInstance()) {
            Long sumEquity = 0L;
            for (int i = 0; i < 2; i++) {
                pairs.add(new slPair(accountTypes[i]));
                Long sumsum = 0L;
                for (mAccount account : realm.where(mAccount.class).equalTo("acct", i).findAllAsync()) {
                    String name = account.getAname();
                    Long sumlong = realm.where(mTra.class).equalTo("accU.aname", name).findAllAsync().sum("uAm").longValue() + realm.where(mTra.class).equalTo("accB.aname", name).findAllAsync().sum("bAm").longValue();
                    sumsum += sumlong;
                    pairs.add(new slPair(name, sumlong, false));
                }
                pairs.add(new slPair("Total", sumsum, true));
                if (i == 0)
                    sumEquity += sumsum;
                else
                    sumEquity -= sumsum;
                pairs.add(new slPair(""));
            }
            pairs.add(new slPair("Equity", sumEquity, true));
            pairs.add(new slPair(""));
            sumEquity = 0L;
            for (int i = 2; i < 4; i++) {
                pairs.add(new slPair(accountTypes[i]));
                Long sumsum = 0L;
                for (mAccount account : realm.where(mAccount.class).equalTo("acct", i).findAllAsync()) {
                    String name = account.getAname();
                    Long sumlong = realm.where(mTra.class).equalTo("accU.aname", name).findAllAsync().sum("uAm").longValue() + realm.where(mTra.class).equalTo("accB.aname", name).findAllAsync().sum("bAm").longValue();
                    sumsum += sumlong;
                    pairs.add(new slPair(name, sumlong, false));
                }
                pairs.add(new slPair("Total", sumsum, true));
                if (i == 2)
                    sumEquity += sumsum;
                else
                    sumEquity -= sumsum;
                pairs.add(new slPair(""));
            }
            pairs.add(new slPair("Surplus", sumEquity, true));
        }
    }
}
