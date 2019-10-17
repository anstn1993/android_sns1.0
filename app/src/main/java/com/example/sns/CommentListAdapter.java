package com.example.sns;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    CommentRecyclerViewClickListener mListener;
    Activity activity;
    Dialog dialog;

    interface CommentRecyclerViewClickListener{

        //삭제버튼을 눌렀을 때의 동작을 담당할 메소드
        void onRemoveButtonClicked(int position);

        void onEditButtonClicked(int position);

        void onCommentClicked(int position);


    }

    public void setOnClickListener(CommentRecyclerViewClickListener listener){
        mListener=listener;
    }

    private ArrayList<CommentItem> commentItemArrayList;
    //어댑터의 생성자에 아이템 리스트 객체와 어떤 액티비티에서 동작할지를 선언해줘서 다른 액티비티와 연결시킴
    CommentListAdapter(Activity activity, ArrayList<CommentItem> commentItemArrayList) {
        this.activity=activity;
        this.commentItemArrayList=commentItemArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        //나의 게시물이면서 댓글이 한줄에서 끝나는 경우
        if(commentItemArrayList.get(position).viewType==0 && commentItemArrayList.get(position).textLine<2){
            return 0;
        }
        //타인의 게시물이면서 댓글이 한줄에서 끝나는 경우
        else if(commentItemArrayList.get(position).viewType==1 && commentItemArrayList.get(position).textLine<2){
            return 1;
        }
        //나의 게시물이면서 댓글이 한줄이 넘어가는 경우
        else if(commentItemArrayList.get(position).viewType==0 && commentItemArrayList.get(position).textLine>=2){
            return 2;
        }
        //타인의 게시물이면서 댓글이 한줄이 넘어가는 경우
        else {
            return 3;
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType==0){
            return new myCommentView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_comment_item,viewGroup,false));

        }else if(viewType==1){
            return new othersCommentView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.others_comment_item,viewGroup,false));
        }else if(viewType==2){
            return new myCommentViewMultiple(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_comment_item_multiline,viewGroup,false));
        }else{
            return new othersCommentViewMultiple(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.others_comment_item_multiline,viewGroup,false));

        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {

        //내가 쓴 댓글일 경우
        if(commentItemArrayList.get(position).viewType==0 && commentItemArrayList.get(position).textLine<2){
           BitmapFactory.Options options=new BitmapFactory.Options();
           options.inSampleSize=16;
            Date date=new Date();

           ((myCommentView)viewHolder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(commentItemArrayList.get(position).profilePicture,options));
           ((myCommentView)viewHolder).TextView_nickname.setText(commentItemArrayList.get(position).nickname);
           ((myCommentView)viewHolder).TextView_comment.setText(commentItemArrayList.get(position).comment);
           ((myCommentView)viewHolder).TextView_comment_delete.setText(commentItemArrayList.get(position).commentDelete);
           ((myCommentView)viewHolder).TextView_comment_edit.setText(commentItemArrayList.get(position).commentEdit);


           SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date=dateFormat.parse(commentItemArrayList.get(position).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            ((myCommentView)viewHolder).TextView_comment_time.setText(beforeTime(date));

           //버튼을 클릭 했을 때의 동작 정의
           if(mListener!=null){
               final int index=viewHolder.getAdapterPosition();

               ((myCommentView)viewHolder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });

               ((myCommentView)viewHolder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });

               ((myCommentView)viewHolder).TextView_comment.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });

               //댓글 삭제 버튼을 눌렀을 때의 정의
               ((myCommentView)viewHolder).TextView_comment_delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onRemoveButtonClicked(index);
                   }
               });

               //댓글 수정 버튼을 눌렀을 때의 정의
               ((myCommentView)viewHolder).TextView_comment_edit.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onEditButtonClicked(index);


                   }
               });

           }


       }
       //남이 쓴 댓글인 경우
       else if(commentItemArrayList.get(position).viewType==1 && commentItemArrayList.get(position).textLine<2){
           BitmapFactory.Options options=new BitmapFactory.Options();
           options.inSampleSize=16;
           Date date=new Date();
           ((othersCommentView)viewHolder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(commentItemArrayList.get(position).profilePicture,options));
           ((othersCommentView)viewHolder).TextView_nickname.setText(commentItemArrayList.get(position).nickname);
           ((othersCommentView)viewHolder).TextView_comment.setText(commentItemArrayList.get(position).comment);

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date=dateFormat.parse(commentItemArrayList.get(position).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            ((othersCommentView)viewHolder).TextView_comment_time.setText(beforeTime(date));

           if(mListener!=null){
               final int index=viewHolder.getAdapterPosition();
               ((othersCommentView)viewHolder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });

               ((othersCommentView)viewHolder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });

               ((othersCommentView)viewHolder).TextView_comment.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentClicked(index);
                   }
               });
           }
       }
       //내가 쓴 댓글이면서 댓글이 한줄 이상일 때
       else if(commentItemArrayList.get(position).viewType==0 && commentItemArrayList.get(position).textLine>=2){
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize=16;
            Date date=new Date();

            ((myCommentViewMultiple)viewHolder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(commentItemArrayList.get(position).profilePicture,options));
            ((myCommentViewMultiple)viewHolder).TextView_nickname.setText(commentItemArrayList.get(position).nickname);
            ((myCommentViewMultiple)viewHolder).TextView_comment.setText(commentItemArrayList.get(position).comment);
            ((myCommentViewMultiple)viewHolder).TextView_comment_delete.setText(commentItemArrayList.get(position).commentDelete);
            ((myCommentViewMultiple)viewHolder).TextView_comment_edit.setText(commentItemArrayList.get(position).commentEdit);


            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date=dateFormat.parse(commentItemArrayList.get(position).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            ((myCommentViewMultiple)viewHolder).TextView_comment_time.setText(beforeTime(date));

            //버튼을 클릭 했을 때의 동작 정의
            if(mListener!=null){
                final int index=viewHolder.getAdapterPosition();

                ((myCommentViewMultiple)viewHolder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                ((myCommentViewMultiple)viewHolder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                ((myCommentViewMultiple)viewHolder).TextView_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                //댓글 삭제 버튼을 눌렀을 때의 정의
                ((myCommentViewMultiple)viewHolder).TextView_comment_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onRemoveButtonClicked(index);
                    }
                });

                //댓글 수정 버튼을 눌렀을 때의 정의
                ((myCommentViewMultiple)viewHolder).TextView_comment_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onEditButtonClicked(index);


                    }
                });

                ((myCommentViewMultiple)viewHolder).TextView_see_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((myCommentViewMultiple)viewHolder).TextView_comment.setMaxLines(Integer.MAX_VALUE);
                        ((myCommentViewMultiple)viewHolder).TextView_see_more.setVisibility(View.GONE);
                    }

                });

            }

        }
        //타인이 쓴 댓글이면서 댓글이 한줄 이상일 때
        else{
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize=16;
            Date date=new Date();
            ((othersCommentViewMultiple)viewHolder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(commentItemArrayList.get(position).profilePicture,options));
            ((othersCommentViewMultiple)viewHolder).TextView_nickname.setText(commentItemArrayList.get(position).nickname);
            ((othersCommentViewMultiple)viewHolder).TextView_comment.setText(commentItemArrayList.get(position).comment);

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date=dateFormat.parse(commentItemArrayList.get(position).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            ((othersCommentViewMultiple)viewHolder).TextView_comment_time.setText(beforeTime(date));

            if(mListener!=null){
                final int index=viewHolder.getAdapterPosition();
                ((othersCommentViewMultiple)viewHolder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                ((othersCommentViewMultiple)viewHolder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                ((othersCommentViewMultiple)viewHolder).TextView_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCommentClicked(index);
                    }
                });

                ((othersCommentViewMultiple)viewHolder).TextView_see_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((othersCommentViewMultiple)viewHolder).TextView_comment.setMaxLines(Integer.MAX_VALUE);
                        ((othersCommentViewMultiple)viewHolder).TextView_see_more.setVisibility(View.GONE);
                    }

                });

            }

        }



    }





    @Override
    public int getItemCount() {
        return commentItemArrayList.size();
    }

    private static class myCommentView extends RecyclerView.ViewHolder {

        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_comment, TextView_comment_delete, TextView_comment_edit, TextView_comment_time;


        public myCommentView(View view) {
            super(view);
            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_comment=view.findViewById(R.id.TextView_comment);
            TextView_comment_delete=view.findViewById(R.id.TextView_comment_delete);
            TextView_comment_edit=view.findViewById(R.id.TextView_comment_edit);
            TextView_comment_time=view.findViewById(R.id.TextView_comment_time);

        }
    }

    private static class othersCommentView extends RecyclerView.ViewHolder {

        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_comment, TextView_comment_time;


        public othersCommentView(View view) {
            super(view);
            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_comment=view.findViewById(R.id.TextView_comment);
            TextView_comment_time=view.findViewById(R.id.TextView_comment_time);
        }
    }

    private static class myCommentViewMultiple extends RecyclerView.ViewHolder {
        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_comment,TextView_see_more, TextView_comment_delete, TextView_comment_edit, TextView_comment_time;

        public myCommentViewMultiple(View view) {
            super(view);

            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_comment=view.findViewById(R.id.TextView_comment);
            TextView_see_more=view.findViewById(R.id.TextView_see_more);
            TextView_comment_delete=view.findViewById(R.id.TextView_comment_delete);
            TextView_comment_edit=view.findViewById(R.id.TextView_comment_edit);
            TextView_comment_time=view.findViewById(R.id.TextView_comment_time);
        }
    }

    private class othersCommentViewMultiple extends RecyclerView.ViewHolder {
        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_comment,TextView_see_more, TextView_comment_time;

        public othersCommentViewMultiple(View view) {
            super(view);
            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_comment=view.findViewById(R.id.TextView_comment);
            TextView_see_more=view.findViewById(R.id.TextView_see_more);
            TextView_comment_time=view.findViewById(R.id.TextView_comment_time);
        }
    }



    public String beforeTime(Date date){

        //캘린더 클레스는 추상 클레스라서 객체를 생성할 수 없다.
        //대신 getInstance()메소드를 통해서 객체 생성이 가능하다.
        Calendar c = Calendar.getInstance();

        //캘린더 객체에서 getTimeInMillis()메소드를 사용해 현재 시간을 가져옴
        long now = c.getTimeInMillis();
        //date에서 시간만 가져온다. 여기서 중요한 점은 now변수는 계속해서 현재시간을 반환하기 때문에 변하는 수이고
        //date는 내가 선언한 순간의 시간을 가져오기 때문에 고정된 시간이다.
        long dateM = date.getTime();

        //이 변수는 위에서 봤듯이 현재의 시간에서 내가 이 메소드를 호출한 시간을 뺀 시간을 의미한다.
        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long)(gap/1000);
        long hour = gap/3600;
        gap = gap%3600;
        long min = gap/60;
        long sec = gap%60;

        if(hour > 24){
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        else if(hour > 0){
            ret = hour+"시간 전";
        }
        else if(min > 0){
            ret = min+"분 전";
        }
        else if(sec > 0){
            ret = sec+"초 전";
        }
        else{
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        return ret;

    }



}
