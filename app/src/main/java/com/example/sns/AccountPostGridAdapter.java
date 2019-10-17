package com.example.sns;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class AccountPostGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    PostGridRecyclerViewClickListener mListener;

    //인터페이스를 통해서 리스너가 발동됐을 때 동작할 메소드를 설정(메인에서 어댑터를 implements하면 이 메소드가 오버라이드 됨)
    interface PostGridRecyclerViewClickListener{
        //사진을 클릭했을 때 동작할 메소드 설정
        void onGridImageViewClicked(int position);

    }

    //
    public void setOnClickListener(PostGridRecyclerViewClickListener listener){
        mListener=listener;
    }



    private ArrayList<PostItem> PostPictureItemArrayList;
    public AccountPostGridAdapter(ArrayList<PostItem> PostPictureItemArrayList) {
        this.PostPictureItemArrayList=PostPictureItemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_picture_item,viewGroup,false);

        return new RowCell(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=10;

        ((RowCell)viewHolder).ImageView_post_picture.setRotation(90);
        ((RowCell)viewHolder).ImageView_post_picture.setImageBitmap(BitmapFactory.decodeFile(PostPictureItemArrayList.get(position).postPictureUri,options));

        if(mListener!=null){
            final int index=viewHolder.getAdapterPosition();

            ((RowCell)viewHolder).ImageView_post_picture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.onGridImageViewClicked(index);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return (PostPictureItemArrayList==null) ? 0 : PostPictureItemArrayList.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {

        ImageView ImageView_post_picture;

        public RowCell(View view) {
            super(view);
            ImageView_post_picture=view.findViewById(R.id.ImageView_post_picture);
        }
    }


}
