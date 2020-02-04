package com.xvzan.simplemoneytracker.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        realmInstance = Realm.getDefaultInstance();
        layt = root.findViewById(R.id.traRV);
        accstr = getContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("nowAccount","");
        TotalManager.cancelAll();
        homeProgress = root.findViewById(R.id.homeProgress);
        if(accstr.matches(""))
            showAll();
        else
            showCat();
        //TotalArray array = new TotalArray(accstr,realmInstance);
        //TotalManager.getInstance().setRecyclerView(layt, getContext(), array);
        return root;
    }

    void showCat(){
        homeProgress.setVisibility(View.VISIBLE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(accstr);
        totalArray = new TotalArray(accstr);
        TotalManager.getInstance().setRecyclerView(layt, getContext(), totalArray, realmInstance, homeProgress);
    }

    void showAll(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_transactions);
        layt.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter_Double adapter_double = new Adapter_Double(getActivity(),realmInstance);
        layt.setAdapter(adapter_double);
        homeProgress.setVisibility(View.INVISIBLE);
        layt.scrollToPosition(adapter_double.getItemCount()-1);
    }

    @Override
    public void onDestroyView() {
        realmInstance.close();
        super.onDestroyView();
    }
}