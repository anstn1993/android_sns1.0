package com.example.sns;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<SearchItem> searchItemArrayList;

    SearchRecyclerViewClickListner mListener;

    interface SearchRecyclerViewClickListner{
        void onListClicked(int position);

    }

    public void setOnClickListener(SearchRecyclerViewClickListner listener){
        mListener=listener;
    }

    public SearchAdapter(ArrayList<SearchItem> searchItemArrayList) {
        this.searchItemArrayList=searchItemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item,viewGroup,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (mListener != null) {
                final int index=holder.getAdapterPosition();
                ((viewHolder)holder).TextView_nickname.setText(searchItemArrayList.get(position).nickname);
                ((viewHolder)holder).TextView_name.setText(searchItemArrayList.get(position).name);

                ((viewHolder)holder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onListClicked(index);
                    }
                });

                ((viewHolder)holder).TextView_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onListClicked(index);
                    }
                });
            }
        }




    @Override
    public int getItemCount() {
        return searchItemArrayList.size();
    }

    private static class viewHolder extends RecyclerView.ViewHolder{

        TextView TextView_nickname, TextView_name;

        public viewHolder(@NonNull View view) {
            super(view);

            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_name=view.findViewById(R.id.TextView_name);
        }
    }
}
