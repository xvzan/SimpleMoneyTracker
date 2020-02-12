package com.xvzan.simplemoneytracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.ui.balances.Adapter_B2;
import com.xvzan.simplemoneytracker.ui.balances.B2_Calc;

import java.text.DateFormat;
import java.util.Calendar;

public class BalanceActivity extends AppCompatActivity {

    RecyclerView balances_rv;
    Adapter_B2 adapter_b2;
    B2_Calc b2Calc;
    Button bt_sDate;
    Button bt_eDate;
    Calendar cld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cld = Calendar.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                findViewById(R.id.toolbar_balances);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        balances_rv = findViewById(R.id.recyclerview_balances);
        balances_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        balances_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        String[] strings = getResources().getStringArray(R.array.account_types);
        b2Calc = new B2_Calc(strings);
        bt_sDate = findViewById(R.id.bt_start_date);
        bt_eDate = findViewById(R.id.bt_end_date);
        setDateButtons(b2Calc.getDateString(true), b2Calc.getDateString(false));
        b2Calc.calculate();
        adapter_b2 = new Adapter_B2(b2Calc);
        balances_rv.setAdapter(adapter_b2);
        bt_sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(bt_sDate.getContext());
                cld.setTime(b2Calc.startDate);
                dpd.getDatePicker().init(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

                    }
                });
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cld.set(year, month, dayOfMonth, 0, 0, 0);
                        b2Calc.setStartDate(cld.getTime());
                        bt_sDate.setText(DateFormat.getDateInstance().format(cld.getTime()));
                        adapter_b2.notifyDataSetChanged();
                    }
                });
                dpd.show();
            }
        });
        bt_eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(bt_eDate.getContext());
                cld.setTime(b2Calc.endDate);
                dpd.getDatePicker().init(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

                    }
                });
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cld.set(year, month, dayOfMonth, 23, 59, 59);
                        b2Calc.setEndDate(cld.getTime());
                        bt_eDate.setText(DateFormat.getDateInstance().format(cld.getTime()));
                        adapter_b2.notifyDataSetChanged();
                    }
                });
                dpd.show();
            }
        });
    }

    void setDateButtons(String startDate, String endDate) {
        bt_sDate.setText(startDate);
        bt_eDate.setText(endDate);
    }
}
