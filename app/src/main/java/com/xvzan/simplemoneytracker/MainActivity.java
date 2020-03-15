package com.xvzan.simplemoneytracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.ui.addaccount.AddAccountDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.xvzan.simplemoneytracker.ui.exportandimport.ExportDialogFragment;
import com.xvzan.simplemoneytracker.ui.exportandimport.ImportDialogfragment;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements AddAccountDialogFragment.addAccountListener {

    private AppBarConfiguration mAppBarConfiguration;
    public NavController navController;
    NavigationView navigationView;
    //public static String nowAccount;
    //Realm realmInstance;
    List<MenuItem> addItems;
    SharedPreferences sharedPref;
    SharedPreferences.Editor spEditor;
    boolean noEquity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navSetUP();
        if ("com.xvzan.simplemoneytracker.NewTransaction".equals(getIntent().getAction())) {
            navController.navigate(R.id.nav_new_tran);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void navSetUP() {
        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        spEditor = sharedPref.edit();
        reItems();
    }

    public void reItems() {
        addItems = new ArrayList<>();
        MenuItem menuItem = navigationView.getMenu().add(R.id.groupB, Menu.NONE, 0, R.string.all_transactions).setCheckable(true);
        menuItem.setIcon(R.drawable.ic_all_inclusive_black_24dp);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                spEditor.putString("nowAccount", "");
                spEditor.commit();
                //nowAccount = item.getTitle().toString();
                navController.navigate(R.id.nav_empty);//GTMD曲线救国
                navController.navigate(R.id.action_nav_empty_to_nav_home);//GTMD曲线救国
                return false;
            }
        });
        addItems.add(menuItem);
        try (final Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(mAccount.class).findAll().size() == 0) {
                noEquity = true;
                return;
            }
            for (mAccount ma : realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING)) {
                if (ma.getAcct() == 4) {
                    MenuItem m = navigationView.getMenu().add(R.id.groupB, Menu.NONE, 1, ma.getAname()).setCheckable(true);
                    m.setIcon(R.drawable.ic_account_balance_wallet_black_24dp);
                    m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            spEditor.putString("nowAccount", item.getTitle().toString());
                            spEditor.commit();
                            //nowAccount = item.getTitle().toString();
                            navController.navigate(R.id.nav_empty);//GTMD曲线救国
                            navController.navigate(R.id.action_nav_empty_to_nav_home);//GTMD曲线救国
                            return false;
                        }
                    });
                    addItems.add(m);
                    continue;
                }
                MenuItem m = navigationView.getMenu().add(R.id.groupA, Menu.NONE, 0, ma.getAname()).setCheckable(true);
                switch (ma.getAcct()) {
                    case 0:
                        m.setIcon(R.drawable.ic_monetization_on_black_24dp);
                        break;
                    case 1:
                        m.setIcon(R.drawable.ic_credit_card_black_24dp);
                        break;
                    case 2:
                        m.setIcon(R.drawable.ic_archive_black_24dp);
                        break;
                    case 3:
                        m.setIcon(R.drawable.ic_unarchive_black_24dp);
                        break;
                }
                m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        spEditor.putString("nowAccount", item.getTitle().toString());
                        spEditor.commit();
                        navController.navigate(R.id.nav_empty);//GTMD曲线救国
                        navController.navigate(R.id.action_nav_empty_to_nav_home);//GTMD曲线救国
                        return false;
                    }
                });
                addItems.add(m);
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void openNewTransactionMenu(View view) {
        //NewTransaction nt = new NewTransaction();
        navController.navigate(R.id.nav_new_tran);
    }

    @Override
    public void onAccountsEdited() {
        for (MenuItem item : addItems) {
            navigationView.getMenu().removeItem(item.getItemId());
        }
        reItems();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_io:
                boolean hasaccount = false;
                try (Realm realm = Realm.getDefaultInstance()) {
                    if (realm.where(mAccount.class).findAll().size() > 0) {
                        hasaccount = true;
                    }
                }
                if (hasaccount) {
                    ExportDialogFragment exportDialogFragment = new ExportDialogFragment();
                    exportDialogFragment.show(getSupportFragmentManager(), "export_dialog");
                } else {
                    ImportDialogfragment importDialogfragment = new ImportDialogfragment();
                    importDialogfragment.show(getSupportFragmentManager(), "import_dialog");
                }
                return true;
            case R.id.action_balances:
                Intent intent = new Intent(this, BalanceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
