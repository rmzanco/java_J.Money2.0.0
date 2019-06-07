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
import android.widget.EditText;
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

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private EditText etSearch;
    private Button button;
    private TextView tvStatus;
    private TextView tvResult;
    private RecyclerView rvSearch;

    private String nameToSearch;
    private String result = "";

    Financiamento current;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Resposta, SearchViewHolder> mFirebaseAdapter;

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView txtValue;
        TextView txtName;
        TextView txtType;
        TextView txtTerm;
        TextView txtDate;

        public SearchViewHolder(View v){
            super(v);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            txtName =  (TextView) itemView.findViewById(R.id.txtName);
            txtType =  (TextView) itemView.findViewById(R.id.txtType);
            txtTerm =  (TextView) itemView.findViewById(R.id.txtTerm);
            txtDate =  (TextView) itemView.findViewById(R.id.txtDate);
        }
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        etSearch    = v.findViewById(R.id.etSearch);
        button      = v.findViewById(R.id.btSearch);
        tvStatus    = v.findViewById(R.id.tvStatus);
        tvResult    = v.findViewById(R.id.tvResult);
        rvSearch    = v.findViewById(R.id.rvSearch);

        SnapshotParser<Resposta> parser = new SnapshotParser<Resposta>() {
            @NonNull
            @Override
            public Resposta parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                Resposta resposta = dataSnapshot.getValue(Resposta.class);
                return resposta;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("Dados_do_Usuario");
        FirebaseRecyclerOptions<Resposta> options =
                new FirebaseRecyclerOptions.Builder<Resposta>().setQuery(messagesRef,parser)
                .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Resposta, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final SearchViewHolder viewHolder, int position, Resposta resposta) {
                try {
                    viewHolder.txtValue.setText(resposta.getValor());
                    viewHolder.txtName.setText(resposta.getNome_env());
                    viewHolder.txtType.setText(resposta.getTipo());
                    viewHolder.txtTerm.setText(resposta.getParcela());
                    viewHolder.txtDate.setText(resposta.getDate());
                }catch (NullPointerException err){
                    err.printStackTrace();
                    Toast.makeText(getContext(), "Ñ foi possivel resgatar info do db!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new SearchViewHolder(inflater.inflate(R.layout.item_messagem,
                        viewGroup,false));
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView mRecycler = (RecyclerView)v.findViewById(R.id.rvSearch);
        mRecycler.setLayoutManager(llm);
        mRecycler.setAdapter(mFirebaseAdapter);

        //Resposta resposta = new Resposta(date, name, strTerm, strValue, type);
        //mFirebaseDatabaseReference.child("Dados_do_Usuario").push().setValue(resposta);

        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot remote1 : dataSnapshot.getChildren()){
                    for(DataSnapshot remote2 : remote1.getChildren()){
                        Resposta resposta = remote2.getValue(Resposta.class);
                        Log.v("DATASET","Nome: "
                                + resposta.getNome_env()
                                + " - Valor: "
                                + resposta.getValor()
                                + " - Data: "
                                + resposta.getDate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Failed to read value.", databaseError.toException());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1º Passo - Atualizar base atual
                //2º Passo - Listar dados da sabe (usar mesma xml da recycler)

                /* //Consulta antiga
                nameToSearch = etSearch.getText().toString().toLowerCase();

                for (int i = 0; i < Financiamentos.financiamentos.size(); i++){

                    current = Financiamentos.financiamentos.get(i);

                    if(nameToSearch.equals(current.getName())){
                        result += String.valueOf(current.getValue()) + "\n";
                        result += String.valueOf(current.getTerm()) + "\n";
                        result += current.getName() + "\n";
                        result += current.getTerm();

                        tvResult.setText(result);

                        break;
                    }
                }

                if(result.equals("")){
                    tvResult.setText("404 Not Found");
                }

                result = "";

                */


            }
        });

        return v;
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }
}
