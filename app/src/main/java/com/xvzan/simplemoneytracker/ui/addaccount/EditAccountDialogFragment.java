package com.xvzan.simplemoneytracker.ui.addaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;
import com.xvzan.simplemoneytracker.ui.share.LinearAdapter;

import io.realm.Realm;

public class EditAccountDialogFragment extends DialogFragment {

    private LinearAdapter linearAdapter;
    private String nameBefore;
    private int typeBefore;
    private EditText nan;
    private Spinner saa;

    public EditAccountDialogFragment(String nameb, int typeb, LinearAdapter adapter){
        nameBefore = nameb;
        typeBefore = typeb;
        linearAdapter = adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_account_dialog, container);
        nan = view.findViewById(R.id.editNewAccountName);
        nan.setText(nameBefore);
        saa = view.findViewById(R.id.spn_AA);
        saa.setSelection(typeBefore);
        view.findViewById(R.id.buttonAddA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAccountDialogFragment.addAccountListener listener = (AddAccountDialogFragment.addAccountListener)getActivity();
                String aName =nan.getText().toString();
                try (Realm realm = Realm.getDefaultInstance()) {
                    mAccount ma = realm.where(mAccount.class).equalTo("aname",nameBefore).findFirst();
                    realm.beginTransaction();
                    if(!aName.equals(nameBefore)){
                        if(realm.where(mAccount.class).equalTo("aname",aName).findAll().size()!=0){
                            Toast.makeText(getContext(),"Duplicate name not allowed",Toast.LENGTH_SHORT).show();
                            realm.commitTransaction();
                            return;
                        }
                        else {
                            ma.setAname(aName);
                        }
                    }
                    if(saa.getSelectedItemPosition() != ma.getAcct()){
                        ma.setAType(saa.getSelectedItemPosition());
                        for (mTra m : realm.where(mTra.class).equalTo("accU.aname",ma.getAname()).or().equalTo("accB.aname",ma.getAname()).findAll()){
                            m.setAllAmount();
                        }
                    }
                    realm.commitTransaction();
                    Toast.makeText(getContext(),"Edited "+aName+" as "+getResources().getStringArray(R.array.account_types)[saa.getSelectedItemPosition()],Toast.LENGTH_SHORT).show();
                }
                listener.onAccountsEdited();
                linearAdapter.notifyDataSetChanged();
                getDialog().dismiss();
            }
        });
        return view;
    }
}
