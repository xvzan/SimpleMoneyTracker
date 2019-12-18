package com.xvzan.simplemoneytracker.ui.newtransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Currency;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.xvzan.simplemoneytracker.R;
import com.xvzan.simplemoneytracker.dbsettings.mAccount;
import com.xvzan.simplemoneytracker.dbsettings.mTra;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.Sort;

public class NewTransactionDialog extends Fragment {

    private List<String> nameList;
    private Spinner aU;
    private Spinner aB;
    private TextView tU;
    private TextView tB;
    private EditText am;
    private EditText note;
    private Button dt;
    private Button tm;
    private Calendar cld;
    private List<Integer> typeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.new_transaction_dialog, container, false);
        cld = Calendar.getInstance();
        aU=root.findViewById(R.id.spn_nt_aU);
        aB=root.findViewById(R.id.spn_nt_aB);
        tU=root.findViewById(R.id.tv_nt_aU);
        tB=root.findViewById(R.id.tv_nt_aB);
        try (Realm realm = Realm.getDefaultInstance()){
            List<mAccount> accList = realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
            nameList = new ArrayList<>();
            nameList.add("");
            typeList = new ArrayList<>();
            typeList.add(-1);
            for (mAccount ma : accList){
                nameList.add(ma.getAname());
                typeList.add(ma.getAcct());
            }
            ArrayAdapter<String> maaa = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,nameList);
            aU.setAdapter(maaa);
            aB.setAdapter(maaa);
            String accstr = getContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("nowAccount","");
            if(!accstr.matches("") && nameList.contains(accstr)){
                int mmm = nameList.indexOf(accstr);
                if(accList.get(mmm-1).getBl1())
                    if(accList.get(mmm-1).getAcct()==4)
                        aB.setSelection(mmm);
                    else
                        aU.setSelection(mmm);
                else
                    aB.setSelection(mmm);
            }
        }
        AdapterView.OnItemSelectedListener spln = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setHintTextViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        aU.setOnItemSelectedListener(spln);
        aB.setOnItemSelectedListener(spln);
        am = root.findViewById(R.id.et_nt_am);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(root,InputMethodManager.SHOW_IMPLICIT);
        am.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(hasFocus){
                    imm.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        });
        dt = root.findViewById(R.id.bt_nt_Date);
        tm = root.findViewById(R.id.bt_nt_Time);
        note = root.findViewById(R.id.et_nt_note);
        LinearLayout btll = root.findViewById(R.id.ll_nt_bottomll);
        btll.removeViewAt(0);
        Button ntbt = root.findViewById(R.id.bt_nt);
        ntbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amstr = am.getText().toString();
                long amint;
                if(amstr.matches("")) return;
                if(amstr.contains("."))
                    amint = (long) (Double.parseDouble(amstr)*Math.pow(10d,(double) Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits()));
                else
                    amint = Long.parseLong(amstr);
                String tNote = note.getText().toString();
                try (Realm realm = Realm.getDefaultInstance()){
                    if(aU.getSelectedItemPosition()==0||aB.getSelectedItemPosition()==0||aU.getSelectedItemPosition()==aB.getSelectedItemPosition())
                        return;
                    mAccount uu = realm.where(mAccount.class).equalTo("aname",nameList.get(aU.getSelectedItemPosition())).findFirst();
                    mAccount bb = realm.where(mAccount.class).equalTo("aname",nameList.get(aB.getSelectedItemPosition())).findFirst();
                    mTra ts = new mTra();
                    ts.allSet(uu,bb,amint,cld.getTime());
                    ts.setmNote(tNote);
                    realm.beginTransaction();
                    realm.copyToRealm(ts);
                    realm.commitTransaction();
                }
                Navigation.findNavController(root).navigate(R.id.action_nav_new_tran_to_nav_home);
            }
        });
        dt.setText(DateFormat.getDateInstance().format(cld.getTime()));
        dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getContext());
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cld.set(year,month,dayOfMonth);
                        dt.setText(DateFormat.getDateInstance().format(cld.getTime()));
                    }
                });
                dpd.show();
            }
        });
        tm.setText(DateFormat.getTimeInstance().format(cld.getTime()));
        tm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        cld.set(Calendar.HOUR_OF_DAY, hourOfDay);cld.set(Calendar.MINUTE,minute);
                        tm.setText(DateFormat.getTimeInstance().format(cld.getTime()));
                    }
                },cld.get(Calendar.HOUR_OF_DAY),cld.get(Calendar.MINUTE),true).show();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        am.requestFocus();
        super.onViewCreated(view, savedInstanceState);
    }

    void setHintTextViews(){
        if(typeList.get(aU.getSelectedItemPosition())==-1||typeList.get(aB.getSelectedItemPosition())==-1){
            tU.setText(R.string.category);
            tB.setText(R.string.account);
            return;
        }
        switch (typeList.get(aB.getSelectedItemPosition())){
            case 2:
            case 3:
                tU.setText(R.string.transfer_to);
                tB.setText(R.string.from_account);
                break;
            default:
                switch (typeList.get(aU.getSelectedItemPosition())){
                    case 2:
                        tU.setText(R.string.credit_income);
                        tB.setText(R.string.to_account);
                        break;
                    case 3:
                        tU.setText(R.string.pay_expense);
                        tB.setText(R.string.from_account);
                        break;
                    default:
                        tU.setText(R.string.transfer_to);
                        tB.setText(R.string.from_account);
                }
        }
    }
}
