package com.xvzan.simplemoneytracker;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.ui.balances.Adapter_B2;
import com.xvzan.simplemoneytracker.ui.balances.B2_Calc;

public class BalanceActivity extends AppCompatActivity {

    RecyclerView balances_rv;
    B2_Calc b2Calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        b2Calc.calculate();
        Adapter_B2 adapter_b2 = new Adapter_B2(b2Calc);
        balances_rv.setAdapter(adapter_b2);
    }
}
