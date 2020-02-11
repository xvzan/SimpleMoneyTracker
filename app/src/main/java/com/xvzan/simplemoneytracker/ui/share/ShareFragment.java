package com.xvzan.simplemoneytracker.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.ui.addaccount.AddAccountDialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;

public class ShareFragment extends Fragment implements StartDragListener{

    ItemTouchHelper touchHelper;
    private View root;
    RecyclerView alvg;
    //AccountBarAdapter mAccountBarAdapter;
    //private OrderedRealmCollection<mAccount> mAList;
    Realm realm;
    //LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_share, container, false);
        realm = Realm.getDefaultInstance();
        //mAList = realm.where(mAccount.class).findAll();
        alvg=root.findViewById(R.id.accRV);
        alvg.setLayoutManager(new LinearLayoutManager(getContext()));
        final LinearAdapter la = new LinearAdapter(getActivity(),this, realm);
        //先实例化Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(la);
        //用Callback构造ItemtouchHelper
        touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(alvg);
        alvg.setAdapter(la);
        alvg.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {

            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                AddAccountDialogFragment.addAccountListener listener = (AddAccountDialogFragment.addAccountListener)getActivity();
                listener.onAccountsEdited();
            }
        });
        Button aa = root.findViewById(R.id.buttonAddAccount);
        aa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAccountDialogFragment adf=new AddAccountDialogFragment(la);
                adf.show(getActivity().getSupportFragmentManager(),"add_account_dialog");
            }
        });
        return root;
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDestroyView() {
        realm.close();
        super.onDestroyView();
    }
}