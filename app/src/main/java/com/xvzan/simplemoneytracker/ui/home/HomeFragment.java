package com.xvzan.simplemoneytracker.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;

import io.realm.Realm;

public class HomeFragment extends Fragment {

    private RecyclerView layt;
    Realm realmInstance;
    ProgressBar homeProgress;
    TotalArray totalArray;
    private String accstr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        realmInstance = Realm.getDefaultInstance();
        layt = root.findViewById(R.id.traRV);
        accstr = getContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("nowAccount", "");
        TotalManager.cancelAll();
        homeProgress = root.findViewById(R.id.homeProgress);
        if (accstr.matches(""))
            showAll();
        else
            showCat();
        FastScroller fastScroller = root.findViewById(R.id.fast_scroller);
        TextView bubble = root.findViewById(R.id.tv_bubble);
        TextView bubble_r = root.findViewById(R.id.tv_bubble_right);
        fastScroller.setRecyclerView(layt, bubble, bubble_r);
        return root;
    }

    private void showCat() {
        homeProgress.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(accstr);
        layt.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter_Single adapter_single = new Adapter_Single(getActivity(), accstr, realmInstance);
        layt.setAdapter(adapter_single);
        int i = adapter_single.getItemCount();
        layt.scrollToPosition(i - 1);
        totalArray = new TotalArray(accstr, i);
        TotalManager.getInstance().setRecyclerView(adapter_single, totalArray, homeProgress);
    }

    private void showAll() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.all_transactions);
        layt.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter_Double adapter_double = new Adapter_Double(getActivity(), realmInstance);
        layt.setAdapter(adapter_double);
        homeProgress.setVisibility(View.INVISIBLE);
        layt.scrollToPosition(adapter_double.getItemCount() - 1);
    }

    @Override
    public void onDestroyView() {
        realmInstance.close();
        super.onDestroyView();
    }
}