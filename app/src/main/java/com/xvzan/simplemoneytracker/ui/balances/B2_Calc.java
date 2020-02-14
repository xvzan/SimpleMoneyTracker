package com.xvzan.simplemoneytracker.ui.balances;

import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.Sort;

public class B2_Calc {

    ArrayList<slPair> pairs;
    private String[] accountTypes;
    public Date startDate;
    public Date endDate;
    private boolean noTransactions;

    public B2_Calc(String[] strings) {
        accountTypes = strings;
        Calendar cld = Calendar.getInstance();
        Date lastTransactionDate = cld.getTime();
        try (Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(mTra.class).findFirst() == null)
                noTransactions = true;
            else
                lastTransactionDate = realm.where((mTra.class)).sort("mDate", Sort.DESCENDING).findFirst().getmDate();
        }
        cld.setTime(lastTransactionDate);
        cld.set(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), 1);
        startDate = cld.getTime();
        cld.set(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endDate = cld.getTime();
    }

    public String getDateString(boolean isStartDate) {
        return DateFormat.getDateInstance().format(isStartDate ? startDate : endDate);
    }

    public void setStartDate(Date date) {
        startDate = date;
        calculate();
    }

    public void setEndDate(Date date) {
        endDate = date;
        calculate();
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
            if (noTransactions) {
                pairs.add(new slPair("No Data"));
                return;
            }
            Long sumEquity = 0L;
            for (int i = 0; i < 2; i++) {
                pairs.add(new slPair(accountTypes[i]));
                Long sumsum = 0L;
                for (mAccount account : realm.where(mAccount.class).equalTo("acct", i).findAllAsync().sort("order", Sort.ASCENDING)) {
                    String name = account.getAname();
                    Long sumlong = realm.where(mTra.class).lessThanOrEqualTo("mDate", endDate).equalTo("accU.aname", name).findAllAsync().sum("uAm").longValue() + realm.where(mTra.class).lessThanOrEqualTo("mDate", endDate).equalTo("accB.aname", name).findAllAsync().sum("bAm").longValue();
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
                for (mAccount account : realm.where(mAccount.class).equalTo("acct", i).findAllAsync().sort("order", Sort.ASCENDING)) {
                    String name = account.getAname();
                    Long sumlong = realm.where(mTra.class).between("mDate", startDate, endDate).equalTo("accU.aname", name).findAllAsync().sum("uAm").longValue() + realm.where(mTra.class).between("mDate", startDate, endDate).equalTo("accB.aname", name).findAllAsync().sum("bAm").longValue();
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
