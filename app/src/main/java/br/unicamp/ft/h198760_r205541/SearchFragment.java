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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Financiamento, SearchViewHolder> mFirebaseAdapter;

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView txtValue;
        TextView txtName;
        TextView txtType;
        TextView txtTerm;
        TextView txtDate;
        ImageView imageView;

        public SearchViewHolder(View v){
            super(v);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            txtName =  (TextView) itemView.findViewById(R.id.txtName);
            txtType =  (TextView) itemView.findViewById(R.id.txtType);
            txtTerm =  (TextView) itemView.findViewById(R.id.txtTerm);
            txtDate =  (TextView) itemView.findViewById(R.id.txtDate);
            imageView = v.findViewById(R.id.image);
        }
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        etSearch    = v.findViewById(R.id.etSearch);
        button      = v.findViewById(R.id.btSearch);
        tvStatus    = v.findViewById(R.id.tvStatus);
        tvResult    = v.findViewById(R.id.tvResult);

        SnapshotParser<Financiamento> parser = new SnapshotParser<Financiamento>() {
            @NonNull
            @Override
            public Financiamento parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                Financiamento financiamento = dataSnapshot.getValue(Financiamento.class);
                return financiamento;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("Dados_do_Usuario");
        final FirebaseRecyclerOptions<Financiamento> options =
                new FirebaseRecyclerOptions.Builder<Financiamento>().setQuery(messagesRef,parser)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Financiamento, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final SearchViewHolder viewHolder, int position, Financiamento financiamento) {
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
                        Toast.makeText(getContext(), "Erro ao deletar!", Toast.LENGTH_SHORT).show();
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

            @Override
            public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new SearchViewHolder(inflater.inflate(R.layout.item_messagem,
                        viewGroup,false));
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearch = (RecyclerView)v.findViewById(R.id.rvSearch);
        rvSearch.setLayoutManager(llm);
        rvSearch.setAdapter(mFirebaseAdapter);

        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot remote1 : dataSnapshot.getChildren()){
                    for(DataSnapshot remote2 : remote1.getChildren()){
                        Financiamento financiamento = remote2.getValue(Financiamento.class);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                try {

                    nameToSearch = etSearch.getText().toString().toLowerCase();
                    Toast.makeText(getContext(), "Pesquisando no DB por " + nameToSearch, Toast.LENGTH_SHORT).show();

                    tvStatus.setText(String.format("procurando por: %s", nameToSearch));
                    tvResult.setText(String.valueOf(nameToSearch));

                    firebaseSearch(nameToSearch);

                }catch (Exception err){
                    err.printStackTrace();
                    Toast.makeText(getContext(), "NOME INVALIDO!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    //search data
    private void firebaseSearch(String searchText){
        Query firebaseSearchQuery = mFirebaseDatabaseReference.orderByChild("nome_env")
                .startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Financiamento> options =
                new FirebaseRecyclerOptions.Builder<Financiamento>()
                .setQuery(firebaseSearchQuery, Financiamento.class)
                .build();

        FirebaseRecyclerAdapter<Financiamento,SearchViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Financiamento, SearchViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(final SearchViewHolder viewHolder,
                                                    int position,
                                                    Financiamento resposta) {


                        viewHolder.txtValue.setText(resposta.getValor());
                        viewHolder.txtName.setText(resposta.getNome_env());
                        viewHolder.txtType.setText(resposta.getTipo());
                        viewHolder.txtTerm.setText(resposta.getParcela());
                        viewHolder.txtDate.setText(resposta.getDate());
                    }

                    @NonNull
                    @Override
                    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                        return new SearchViewHolder(inflater.inflate(R.layout.item_messagem,
                                viewGroup,false));
                    }
                };

        rvSearch.setAdapter(firebaseRecyclerAdapter);
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
