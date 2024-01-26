package com.xvzan.simplemoneytracker.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;

import java.util.Objects;

import io.realm.Realm;

public class HomeFragment extends Fragment {

    private RecyclerView layt;
    private Realm realmInstance;
    private ProgressBar homeProgress;
    private String accstr;

    private boolean showAll;

    MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.main, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            return false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().addMenuProvider(menuProvider);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().removeMenuProvider(menuProvider);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        realmInstance = Realm.getDefaultInstance();
        layt = root.findViewById(R.id.traRV);
        accstr = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("nowAccount", "");
        TotalManager.cancelAll();
        homeProgress = root.findViewById(R.id.homeProgress);
        if (accstr.equals(""))
            showAll();
        else
            showCat();
        FastScroller fastScroller = root.findViewById(R.id.fast_scroller);
        TextView bubble = root.findViewById(R.id.tv_bubble);
        TextView bubble_r = root.findViewById(R.id.tv_bubble_right);
        fastScroller.setRecyclerView(layt, bubble, bubble_r);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showAll)
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.all_transactions);
        else
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(accstr);
    }

    private void showCat() {
        homeProgress.setVisibility(View.VISIBLE);
        layt.setLayoutManager(new LinearLayoutManager(getContext()));
        int accOrder;
        accOrder = Objects.requireNonNull(realmInstance.where(mAccount.class).equalTo("aname", accstr).findFirst()).getOrder();
        Adapter_Single adapter_single = new Adapter_Single(getActivity(), accOrder, realmInstance);
        layt.setAdapter(adapter_single);
        int i = adapter_single.getItemCount();
        layt.scrollToPosition(i - 1);
        TotalArray totalArray = new TotalArray(accOrder, i);
        TotalManager.getInstance().setRecyclerView(adapter_single, totalArray, homeProgress);
        showAll = false;
    }

    private void showAll() {
        layt.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter_Double adapter_double = new Adapter_Double(getActivity(), realmInstance);
        layt.setAdapter(adapter_double);
        homeProgress.setVisibility(View.INVISIBLE);
        layt.scrollToPosition(adapter_double.getItemCount() - 1);
        showAll = true;
    }

    @Override
    public void onDestroyView() {
        realmInstance.close();
        super.onDestroyView();
    }
}