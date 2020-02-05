package com.xvzan.simplemoneytracker.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Currency;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class Adapter_Single extends RecyclerView.Adapter<Adapter_Single.SingleTraHolder> {

    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
    private NumberFormat numberFormat;
    private double d_Double;
    private OrderedRealmCollection<mTra> mTraList;
    Long[] longs;
    String accstr;
    private Realm realminstance;

    Adapter_Single(Context context, String str, Realm instance) {
        this.mContext = context;
        accstr = str;
        numberFormat = NumberFormat.getCurrencyInstance();
        d_Double = Math.pow(10d, (double) Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
        realminstance = instance;
        mTraList = realminstance.where(mTra.class).equalTo("accU.aname", accstr).or().equalTo("accB.aname", accstr).findAll().sort("mDate", Sort.ASCENDING);
    }

    @Override
    public Adapter_Single.SingleTraHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SingleTraHolder(LayoutInflater.from(mContext).inflate(R.layout.transaction_single, parent, false));
    }

    @Override
    public void onBindViewHolder(Adapter_Single.SingleTraHolder holder, final int position) {
        if (mTraList.get(position).getAccU().getAname().matches(accstr)) {
            holder.tsAccount.setText(mTraList.get(position).getAccB().getAname());
            holder.tsAmount.setText(numberFormat.format(mTraList.get(position).getuAm() / d_Double));
            if (mTraList.get(position).getuAm() < 0)
                holder.tsAmount.setTextColor(Color.RED);
        } else {
            holder.tsAccount.setText(mTraList.get(position).getAccU().getAname());
            holder.tsAmount.setText(numberFormat.format(mTraList.get(position).getbAm() / d_Double));
            if (mTraList.get(position).getbAm() < 0)
                holder.tsAmount.setTextColor(Color.RED);
        }
        if (longs != null && longs[position] != null) {
            if (longs[position] < 0)
                holder.tsTotal.setTextColor(Color.RED);
            holder.tsTotal.setText(numberFormat.format(longs[position] / d_Double));
        }
        holder.tsDate.setText(sdf.format(mTraList.get(position).getmDate()));
        holder.tsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (Realm realminstance = Realm.getDefaultInstance()) {
                    realminstance.beginTransaction();
                    mTraList.get(position).setEditme();
                    realminstance.commitTransaction();
                }
                Navigation.findNavController(v).navigate(R.id.nav_edit_tran);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTraList.size();
    }

    void setTotalArray() {
        TotalManager totalManager = TotalManager.getInstance();
    }

    class SingleTraHolder extends RecyclerView.ViewHolder {
        TextView tsDate;
        TextView tsAccount;
        TextView tsAmount;
        TextView tsTotal;
        ImageButton tsEdit;

        SingleTraHolder(View itemView) {
            super(itemView);
            tsDate = itemView.findViewById(R.id.tsDate);
            tsAccount = itemView.findViewById(R.id.tsAccount);
            tsAmount = itemView.findViewById(R.id.tsAmount);
            tsTotal = itemView.findViewById(R.id.tsTotal);
            tsEdit = itemView.findViewById(R.id.bt_ts_edit);
        }
    }
}
