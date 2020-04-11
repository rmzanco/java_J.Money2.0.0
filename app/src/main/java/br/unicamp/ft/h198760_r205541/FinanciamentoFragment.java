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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinanciamentoFragment extends Fragment {

    private Button          button;
    private EditText        etNome;
    private EditText        etValue;
    private RadioGroup      rgType;
    private CheckBox        cbTerm;
    private EditText        parcelas;
    private ImageView       imageView;

    private DatabaseReference mFirebaseDatabaseReference;

    public FinanciamentoFragment() {
        // Required empty public constructor
    }

    //criação de um holder p/ reciclerView dos metodos do firebase
    public static class FinancimentoViewHolder extends RecyclerView.ViewHolder{
        TextView txtValue;
        TextView txtName;
        TextView txtType;
        TextView txtTerm;
        TextView txtDate;
        ImageView imageView;

        public FinancimentoViewHolder(@NonNull View v) {
            super(v);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            txtName =  (TextView) itemView.findViewById(R.id.txtName);
            txtType =  (TextView) itemView.findViewById(R.id.txtType);
            txtTerm =  (TextView) itemView.findViewById(R.id.txtTerm);
            txtDate =  (TextView) itemView.findViewById(R.id.txtDate);
            imageView = v.findViewById(R.id.image);

        }

    }

    private FirebaseRecyclerAdapter<Financiamento, FinancimentoViewHolder> mFirebaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_financiamento, container, false);

        button          = v.findViewById(R.id.btAdd);
        etNome          = v.findViewById(R.id.etNome);
        etValue         = v.findViewById(R.id.etValue);
        rgType          = v.findViewById(R.id.rgType);
        cbTerm          = v.findViewById(R.id.cbTerm);
        parcelas        = v.findViewById(R.id.parcelas);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<Financiamento> parser = new SnapshotParser<Financiamento>() {
            @NonNull
            @Override
            public Financiamento parseSnapshot(@NonNull DataSnapshot snapshot) {
                Financiamento financiamento = snapshot.getValue(Financiamento.class);
                return financiamento;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("Dados_do_Usuario");
        FirebaseRecyclerOptions<Financiamento> options = new FirebaseRecyclerOptions.Builder<Financiamento>()
                .setQuery(messagesRef, parser).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Financiamento, FinancimentoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final FinancimentoViewHolder viewHolder, int position, @NonNull Financiamento financiamento) {

                try {

                    try {
                        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                mFirebaseAdapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getRef().removeValue();
                                notifyItemRemoved(viewHolder.getAdapterPosition());
                                notifyDataSetChanged();
                                return true;
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getContext(), "Erro", Toast.LENGTH_SHORT).show();
                    }

                    viewHolder.txtValue.setText(financiamento.getValor());
                    viewHolder.txtName.setText(financiamento.getNome_env());
                    viewHolder.txtType.setText(financiamento.getTipo());
                    viewHolder.txtTerm.setText(financiamento.getParcela());
                    viewHolder.txtDate.setText(financiamento.getDate());

                    if(financiamento.getTipo().equalsIgnoreCase("divida")){
                        viewHolder.imageView.setImageResource(R.drawable.ic_action_divida);
                    }else {
                        viewHolder.imageView.setImageResource(R.drawable.ic_action_emprestimo);
                    }


                }catch (NullPointerException err){
                    err.printStackTrace();
                    Toast.makeText(getContext(), "Ñ foi possivel resgatar info do db!", Toast.LENGTH_SHORT).show();
                }

            }

            @NonNull
            @Override
            public FinancimentoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new
                        FinancimentoViewHolder(inflater.inflate(R.layout.item_messagem,
                        viewGroup,false));
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation((LinearLayoutManager.VERTICAL));
        RecyclerView mRecycler = ((RecyclerView)v.findViewById(R.id.rvFinanciamentos));
        mRecycler.setLayoutManager(llm);
        mRecycler.setAdapter(mFirebaseAdapter);

        parcelas.setVisibility(View.INVISIBLE);

        cbTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean parceladoIsChecked) {
                if(parceladoIsChecked){
                    parcelas.setVisibility(View.VISIBLE);
                }else{
                    parcelas.setVisibility(View.INVISIBLE);
                }
            }
        });

        button.setOnClickListener(  new View.OnClickListener()  {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date d = new Date();
                String date = dateFormat.format(d);

                int id = rgType.getCheckedRadioButtonId();

                try{

                    double  value      = Double.parseDouble((etValue.getText().toString()));
                    String  name       = etNome.getText().toString();
                    String  type;
                    String strParcelas = parcelas.getText().toString();

                    if(isDivida(id)){
                        type = "divida";
                    }else{
                        type = "emprestimo";
                    }

                    String strValue = String.valueOf(value);

                    //Enviar info ao DB
                    Financiamento financiamento = new Financiamento(date, name, strParcelas, strValue, type);
                    mFirebaseDatabaseReference.child("Dados_do_Usuario").push().setValue(financiamento);

                    //Acessar info do DB
                    mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot remoteRespostas : dataSnapshot.getChildren()) {
                                for (DataSnapshot remoteResposta : remoteRespostas.getChildren()){
                                    Financiamento financiamento = remoteResposta.getValue(Financiamento.class);
                                    assert financiamento != null;
                                    Log.v("DATASET","Nome: "
                                            + financiamento.getNome_env()
                                            + " - Valor: "
                                            + financiamento.getValor()
                                            + " - Data: "
                                            + financiamento.getDate());
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

                    clearEditText(); Toast.makeText(getContext(), "Adicionado", Toast.LENGTH_SHORT).show();

                }catch (Exception err){
                    err.printStackTrace();
                    Toast.makeText(getContext(), "VALORES INVALIDOS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private boolean isDivida(int id) {
        return id == R.id.rbDivida;
    }

    public void clearEditText(){
        etValue.getText().clear();
        etNome.getText().clear();
    }

    public void onPause(){
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }
}
