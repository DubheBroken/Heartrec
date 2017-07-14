package com.zdk.pojun.heartrec.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zdk.pojun.heartrec.R;


/**
 * Created by Developer on 2017/7/3.
 */

public class Bottom_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_layout, container, false);
        return view;
    }
}
