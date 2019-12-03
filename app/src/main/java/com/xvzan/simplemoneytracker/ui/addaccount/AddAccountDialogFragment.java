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
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.ui.share.LinearAdapter;
import com.xvzan.simplemoneytracker.ui.share.ShareFragment;

import io.realm.Realm;

public class AddAccountDialogFragment extends DialogFragment {

    EditText nan;
    Spinner saa;
    LinearAdapter adapter;

    public interface addAccountListener
    {
        void onAccountsEdited();
    }

    public AddAccountDialogFragment(LinearAdapter linearAdapter){
        this.adapter = linearAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_account_dialog, container);
        nan = view.findViewById(R.id.editNewAccountName);
        saa = view.findViewById(R.id.spn_AA);
        view.findViewById(R.id.buttonAddA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccountListener listener = (addAccountListener)getActivity();
                String aName =nan.getText().toString();
                if(aName.equals("")){
                    Toast.makeText(getContext(),"Account name required",Toast.LENGTH_SHORT).show();
                    return;
                }
                try (Realm realm = Realm.getDefaultInstance()) {
                    int s= Math.max(realm.where(mAccount.class).findAll().size(),0)+1;
                    if(s==1){
                        mAccount fma = new mAccount();
                        fma.setAname("Equity");
                        fma.setAType(4);
                        fma.setOrder(s);
                        s++;
                        realm.beginTransaction();
                        realm.copyToRealm(fma);
                        realm.commitTransaction();
                    }
                    if(realm.where(mAccount.class).equalTo("aname",aName).findAll().size()!=0){
                        Toast.makeText(getContext(),"Duplicate name not allowed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAccount ma=new mAccount();
                    ma.setAname(aName);
                    ma.setAType(saa.getSelectedItemPosition());
                    ma.setOrder(s);
                    realm.beginTransaction();
                    realm.copyToRealm(ma);
                    realm.commitTransaction();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(),"Added "+aName+" as "+getResources().getStringArray(R.array.account_types)[saa.getSelectedItemPosition()],Toast.LENGTH_SHORT).show();
                }
                listener.onAccountsEdited();
            }
        });
        return view;
    }
}
