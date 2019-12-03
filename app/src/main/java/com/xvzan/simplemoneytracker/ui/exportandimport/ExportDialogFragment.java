package com.xvzan.simplemoneytracker.ui.exportandimport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class ExportDialogFragment extends DialogFragment {

    File csvA;
    File csvT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.export_dialog_gragment, container);
        TextView tvf = view.findViewById(R.id.tv_export_folder);
        csvA = new File(getContext().getExternalFilesDir(null),"accounts.csv");
        csvT = new File(getContext().getExternalFilesDir(null),"transactions.csv");
        tvf.setText("Output Folder: "+getContext().getExternalFilesDir(null).getAbsolutePath());
        Button bte = view.findViewById(R.id.bt_export);
        bte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCSV();
            }
        });
        return view;
    }

    private void exportCSV(){
        if(csvA.exists())
            csvA.delete();
        csvA = new File(getContext().getExternalFilesDir(null),"accounts");
        if(csvT.exists())
            csvT.delete();
        csvT = new File(getContext().getExternalFilesDir(null),"transactions");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvA,true);
            StringBuilder stringBuilder = new StringBuilder();
            try(Realm realm = Realm.getDefaultInstance()){
                OrderedRealmCollection<mAccount> mAList = realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
                for (mAccount ma : mAList){
                    stringBuilder.append(ma.getAname()).append("\t").append(ma.getAcct());
                    stringBuilder.append("\n");
                }
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvT,true);
            StringBuilder stringBuilder = new StringBuilder();
            try(Realm realm = Realm.getDefaultInstance()){
                OrderedRealmCollection<mTra> mTraList = realm.where(mTra.class).findAll().sort("mDate", Sort.ASCENDING);
                for (mTra mt : mTraList){
                    stringBuilder.append(sdf.format(mt.getmDate())).append("\t").append(mt.getAccB().getAname()).append("\t").append(mt.getAccU().getAname()).append("\t").append(mt.getDeltaAmount()).append("\t").append(mt.getmNote());
                    stringBuilder.append("\n");
                }
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        getDialog().dismiss();
    }
}
