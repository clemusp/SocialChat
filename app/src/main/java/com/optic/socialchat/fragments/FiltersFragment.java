package com.optic.socialchat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optic.socialchat.R;
import com.optic.socialchat.activities.FiltersActivity;


public class FiltersFragment extends Fragment {

    View mView;
    CardView mCardViewColegio;
    CardView mCardViewMaestros;
    CardView mCardViewAlumnos;
    CardView mCardViewPadres;


    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        mCardViewAlumnos = mView.findViewById(R.id.cardViewAlumnos);
        mCardViewMaestros = mView.findViewById(R.id.cardViewMaestros);
        mCardViewPadres = mView.findViewById(R.id.cardViewPadres);
        mCardViewColegio = mView.findViewById(R.id.cardViewColegio);

        mCardViewAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Alumnos");
            }
        });

        mCardViewPadres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Padres");
            }
        });

        mCardViewMaestros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Maestros");
            }
        });

        mCardViewColegio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Colegio");
            }
        });

        return mView;
    }

    private void goToFilterActivity(String category){
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

}