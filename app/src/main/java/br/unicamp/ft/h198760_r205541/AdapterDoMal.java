package br.unicamp.ft.h198760_r205541;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

//ADAPTER UTILIZADO ANTES DA IMPLEMENTAÇÃO DO FIREBASE
public class AdapterDoMal extends RecyclerView.Adapter {

    private ArrayList<Finan2> finan2ArrayList;
    private MyOnLongClickListener myOnLongClickListener;
    private MyOnItemClickListener myOnItemClickListener;
    private int currentPos;

    public AdapterDoMal(ArrayList<Finan2> finan2ArrayList) {
        this.finan2ArrayList = finan2ArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item, viewGroup, false);

        final ViewHolderDoMal holderDoMal = new ViewHolderDoMal(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOnItemClickListener.MyOnItemClick(holderDoMal.getHolderPos());
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(myOnLongClickListener != null){
                    currentPos = holderDoMal.getAdapterPosition();
                    removeItem(currentPos);
                    myOnLongClickListener.MyOnLongClick(currentPos);
                }
                return true;
            }
        });

        return holderDoMal;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolderDoMal)viewHolder).bind(finan2ArrayList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return finan2ArrayList.size();
    }

    public class ViewHolderDoMal extends RecyclerView.ViewHolder{

        private TextView textViewValue;
        private TextView textViewTerm;
        private TextView textViewName;
        private TextView textViewType;
        private TextView textViewDate;

        private int      position;

        public ViewHolderDoMal(@NonNull View itemView) {
            super(itemView);
            textViewValue   = itemView.findViewById(R.id.tvValue);
            textViewTerm    = itemView.findViewById(R.id.tvTerm);
            textViewName    = itemView.findViewById(R.id.tvName);
            textViewType    = itemView.findViewById(R.id.tvType);
            textViewDate    = itemView.findViewById(R.id.tvDate);
        }


        public void bind(Finan2 finan2, int pos){

            textViewValue.setText(String.valueOf(finan2.getValue()));
            textViewTerm.setText(String.valueOf(finan2.getTerm()));
            textViewName.setText(finan2.getName());
            textViewType.setText(finan2.getType());
            textViewDate.setText(finan2.getDate());
            this.position = pos;
        }

        public int getHolderPos(){
            return position;
        }

    }

    public boolean addItem(double value, String name, String type, int term, String date){
        try{
            finan2ArrayList.add(new Finan2(value, name, type, term, date));
            notifyDataSetChanged();
        }catch (Exception err){
            err.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeItem(int position){
        try{
            finan2ArrayList.remove(position);
            notifyItemRemoved(position);
            return true;
        }catch (Exception err){
            err.printStackTrace();
            return false;
        }
    }

    public interface MyOnLongClickListener{
        void MyOnLongClick (int position);
    }

    public interface MyOnItemClickListener{
        void MyOnItemClick(int position);
    }

    public  void setMyOnLongClickListener(MyOnLongClickListener listener){
        this.myOnLongClickListener = listener;
    }

    public void setMyOnItemClickListener(MyOnItemClickListener listener){
        this.myOnItemClickListener = listener;
    }

}
