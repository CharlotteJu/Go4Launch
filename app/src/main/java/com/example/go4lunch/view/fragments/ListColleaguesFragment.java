package com.example.go4lunch.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;

public class ListColleaguesFragment extends Fragment {


    public ListColleaguesFragment() {
        // Required empty public constructor
    }


    public static ListColleaguesFragment newInstance() {
        ListColleaguesFragment fragment = new ListColleaguesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_colleagues, container, false);
    }

}

