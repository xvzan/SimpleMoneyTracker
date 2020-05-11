package com.xvzan.simplemoneytracker.ui.exportandimport;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;
import com.xvzan.simplemoneytracker.ui.addaccount.AddAccountDialogFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Objects;

import io.realm.Realm;

public class ImportDialogfragment extends DialogFragment {

    private File csvA;
    private File csvT;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.import_dialog_fragment, container);
        TextView tva = view.findViewById(R.id.tv_import_acc);
        TextView tvt = view.findViewById(R.id.tv_import_trans);
        Button bti = view.findViewById(R.id.bt_import);
        csvA = new File(getContext().getExternalFilesDir(null), "import" + File.separator + "accounts");
        csvT = new File(getContext().getExternalFilesDir(null), "import" + File.separator + "transactions");
        if (csvA.exists()) {
            tva.setText("Import accounts from :" + csvA.getAbsolutePath());
            bti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    importCSV();
                }
            });
        } else {
            tva.setText(csvA.getAbsolutePath() + " not found!");
            tva.setTextColor(Color.RED);
        }
        if (csvT.exists()) {
            tvt.setText("Import transactions from : " + csvT.getAbsolutePath());
        } else {
            tvt.setText(csvT.getAbsolutePath() + " not found");
            tvt.setTextColor(Color.RED);
        }
        return view;
    }

    private void importCSV() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try (Realm realm = Realm.getDefaultInstance()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(csvA));
                String line;
                int s = 0;
                AddAccountDialogFragment.addAccountListener listener = (AddAccountDialogFragment.addAccountListener) getActivity();
                while ((line = bufferedReader.readLine()) != null) {
                    String[] item = line.split("\t");
                    mAccount ma = new mAccount();
                    ma.setAname(item[0]);
                    ma.setAType(Integer.parseInt(item[1]));
                    ma.setOrder(s);
                    realm.beginTransaction();
                    realm.copyToRealm(ma);
                    realm.commitTransaction();
                    s++;
                }
                assert listener != null;
                listener.onAccountsEdited();
                csvA.delete();
                if (csvT.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(csvT));
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] item = line.split("\t");
                        mAccount uu = realm.where(mAccount.class).equalTo("aname", item[2]).findFirst();
                        mAccount bb = realm.where(mAccount.class).equalTo("aname", item[1]).findFirst();
                        mTra ts = new mTra();
                        ts.directSet(uu, bb, Long.parseLong(item[3]), Long.parseLong(item[4]), Long.parseLong(item[5]), sdf.parse(item[0]));
                        if (item.length == 7) {
                            ts.setmNote(item[6]);
                        } else {
                            ts.setmNote("");
                        }
                        realm.beginTransaction();
                        realm.copyToRealm(ts);
                        realm.commitTransaction();
                    }
                    csvT.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getContext(), "Imported", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getDialog()).dismiss();
    }
}
