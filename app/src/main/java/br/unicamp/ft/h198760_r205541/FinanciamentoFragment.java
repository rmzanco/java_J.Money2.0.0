package br.unicamp.ft.h198760_r205541;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Interfaces.OnEditRequest;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinanciamentoFragment extends Fragment {


    private RecyclerView    recyclerView;
    private Button          button;
    private EditText        etNome;
    private EditText        etValue;
    private RadioGroup      rgType;
    private CheckBox        cbTerm;
    private Spinner         spinner;

    private AdapterDoMal mAdapter;
    private OnEditRequest onEditRequest;

    private DatabaseReference mFirebaseDatabaseReference;

    public FinanciamentoFragment() {
        // Required empty public constructor
    }

    //criação de um holder p/ reciclerView dos metodos do firebase


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_financiamento, container, false);

        recyclerView    = v.findViewById(R.id.rvFinanciamentos);
        button          = v.findViewById(R.id.btAdd);
        etNome          = v.findViewById(R.id.etNome);
        etValue         = v.findViewById(R.id.etValue);
        rgType          = v.findViewById(R.id.rgType);
        cbTerm          = v.findViewById(R.id.cbTerm);
        spinner         = v.findViewById(R.id.spinner);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        spinner.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new AdapterDoMal(Financiamentos.financiamentos);
        recyclerView.setAdapter(mAdapter);

        cbTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinner.setVisibility(View.VISIBLE);
                }else{
                    spinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date d = new Date();
                String date = dateFormat.format(d);

                int id = rgType.getCheckedRadioButtonId();

                try{

                    double  value    = Double.parseDouble((etValue.getText().toString()));
                    String  name     = etNome.getText().toString().toLowerCase();
                    String  type;
                    int     term     = 0;

                    if(cbTerm.isChecked()){
                        term = Integer.parseInt(spinner.getSelectedItem().toString());
                    }

                    if(id == R.id.rbDivida){
                        type = "divida";
                    }else{
                        type = "emprestimo";
                    }

                    String strTerm = String.valueOf(term);
                    String strValue = String.valueOf(value);

                    //Enviar info ao DB
                    Resposta resposta = new Resposta(date, name, strTerm, strValue, type);
                    mFirebaseDatabaseReference.child("Dados_do_Usuario").push().setValue(resposta);

                    //Acessar info do DB
                    mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot remoteRespostas : dataSnapshot.getChildren()) {
                                for (DataSnapshot remoteResposta : remoteRespostas.getChildren()){
                                    Resposta resposta = remoteResposta.getValue(Resposta.class);
                                    Log.v("DATASET","Nome: " + resposta.getNome_env()+" - Data: " + resposta.getDate());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG,"Failed to read value.", databaseError.toException());
                        }
                    });

                    //consulta por limite de no max. 10
                    mFirebaseDatabaseReference.limitToLast(10);

                    //inserção antiga
                    if(mAdapter.addItem(value, name, type, term, date)){
                        Toast.makeText(getContext(), "ADD", Toast.LENGTH_SHORT).show();
                        clearEditText();
                    }

                }catch (Exception err){
                    err.printStackTrace();
                    Toast.makeText(getContext(), "VALORES INVALIDOS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //remoçao antiga
        mAdapter.setMyOnLongClickListener(new AdapterDoMal.MyOnLongClickListener() {
            @Override
            public void MyOnLongClick(int position) {
                if(mAdapter.removeItem(position)){
                    Toast.makeText(getContext(), "REMOVIDO", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setMyOnItemClickListener(new AdapterDoMal.MyOnItemClickListener() {
            @Override
            public void MyOnItemClick(int position) {
                if(onEditRequest != null){
                    onEditRequest.OnEditRequest(position);
                }
            }
        });

        return v;
    }

    public void setOnEditRequest(OnEditRequest onEditRequest){
        this.onEditRequest = onEditRequest;
    }

    public void clearEditText(){
        etValue.getText().clear();
        etNome.getText().clear();
    }

}
