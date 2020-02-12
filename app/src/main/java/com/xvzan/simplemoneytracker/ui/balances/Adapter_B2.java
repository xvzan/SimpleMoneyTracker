package com.xvzan.simplemoneytracker.ui.balances;

import android.graphics.Color;
import android.icu.util.Currency;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;

import java.text.NumberFormat;
import java.util.Locale;

public class Adapter_B2 extends RecyclerView.Adapter<Adapter_B2.B2_Holder> {

    B2_Calc b2_calc;
    private NumberFormat numberFormat;
    private double d_Double;

    @NonNull
    @Override
    public B2_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        d_Double = Math.pow(10d, (double) Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
        numberFormat = NumberFormat.getCurrencyInstance();
        return new B2_Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.balances_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull B2_Holder holder, int position) {
        if (b2_calc.pairs.get(position).noLong) {
            holder.tvName.setText(b2_calc.pairs.get(position).string);
            holder.tvSum.setText("");
        } else {
            if (b2_calc.pairs.get(position).isTotal)
                holder.tvName.setText(b2_calc.pairs.get(position).string);
            else
                holder.tvName.setText("   " + b2_calc.pairs.get(position).string);
            if (b2_calc.pairs.get(position).aLong < 0)
                holder.tvSum.setTextColor(Color.RED);
            else
                holder.tvSum.setTextColor(holder.tvName.getTextColors());
            holder.tvSum.setText(numberFormat.format(b2_calc.pairs.get(position).aLong / d_Double));
        }
    }

    public Adapter_B2(B2_Calc b) {
        b2_calc = b;
    }

    @Override
    public int getItemCount() {
        return b2_calc.pairs.size();
    }

    class B2_Holder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSum;

        B2_Holder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_b2_name);
            tvSum = itemView.findViewById(R.id.tv_b2_sum);
        }
    }
}
