package br.unicamp.ft.h198760_r205541;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private TextView tvReport;

    private double divida = 0;
    private double emprestimo = 0;
    private double report;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        tvReport = v.findViewById(R.id.tvReport);

        final SnapshotParser<Financiamento> parser = new SnapshotParser<Financiamento>() {
            @NonNull
            @Override
            public Financiamento parseSnapshot(@NonNull DataSnapshot snapshot) {

                return null;
            }
        };

        report = emprestimo+divida;

        tvReport.setText(String.valueOf(report));

        emprestimo = 0;
        divida = 0;
        report = 0;

        return v;
    }

}
