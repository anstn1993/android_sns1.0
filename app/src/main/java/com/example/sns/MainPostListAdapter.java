package com.example.sns;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class MainPostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    Bundle bundle;
    Context context;
    private ArrayList<PostItem> postItemArrayList;






    //리사이클러뷰의 클릭을 했을 때 그것을 인식할 리스너 설정(객체 변수라고 생각하면 됨)
    PostRecyclerViewClickListener mListener;




    //인터페이스를 통해서 리스너가 발동됐을 때 동작할 메소드를 설정(메인에서 어댑터를 implements하면 이 메소드가 오버라이드 됨)
    interface PostRecyclerViewClickListener{
        //삭제버튼을 눌렀을 때의 동작을 담당할 메소드
        void onRemoveButtonClicked(int position);

        //댓글 버튼을 눌렀을 때의 동작을 담당할 메소드
        void onCommentButtonClicked(int position);

        //좋아요 버튼을 눌렀을 때의 동작을 담당할 메소드
        void onLikeButtonClicked(int position);

//        좋아요 버튼을 취소했을 때의 동작을 담당할 메소드
        void onLikeButtonCanceled(int position);

        //수정 버튼을 눌렀을 때의 동작을 담당할 메소드
        void onEditPostButtonClicked(int position);

        //닉네임 버튼을 눌렀을 때의 동작을 담당할 메소드
        void onNicknameButtonClicked(int position);

        //프로필 보기 버튼을 눌렀을 때의 동작을 담당할 메소드
        void onProfileButtonClicked(int position);
    }

    //
    public void setOnClickListener(PostRecyclerViewClickListener listener){
        mListener=listener;
    }







    MainPostListAdapter(ArrayList<PostItem> postItemArrayList, Context context){
        this.postItemArrayList=postItemArrayList;
        this.context=context;
    }

    @Override
    public int getItemViewType(int position) {
        if(postItemArrayList.get(position).textLine<2){
            return 0;
        }else{
            return 1;
        }


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType==0){
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item,viewGroup,false);
            return new viewHolder(view);
        }else{
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item_multiline,viewGroup,false);
            return new viewHolderMultiple(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

       if(postItemArrayList.get(position).textLine<2){

           final int index=holder.getAdapterPosition();
           Date date = new Date();
           BitmapFactory.Options postPictureOptions=new BitmapFactory.Options();
           postPictureOptions.inSampleSize=4;

           BitmapFactory.Options profileOptions=new BitmapFactory.Options();
           profileOptions.inSampleSize=30;




           ((viewHolder)holder).CircleImageView_profile_picture.setRotation(90);
           ((viewHolder)holder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(postItemArrayList.get(position).profilePicutreUri,profileOptions));
           ((viewHolder)holder).TextView_nickname.setText(postItemArrayList.get(position).nickname);
           ((viewHolder)holder).TextView_article.setText(postItemArrayList.get(position).article);
           ((viewHolder)holder).imageButton_comment.setImageResource(postItemArrayList.get(position).commentID);
           ((viewHolder)holder).TextView_comment_count.setText(String.valueOf(postItemArrayList.get(position).commentCount));
           ((viewHolder)holder).ImageButton_more.setImageResource(postItemArrayList.get(position).moreID);
           ((viewHolder)holder).ImageView_uploaded_picture.setRotation(90);
           ((viewHolder)holder).ImageView_uploaded_picture.setImageBitmap(BitmapFactory.decodeFile(postItemArrayList.get(position).postPictureUri,postPictureOptions));



           SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           try {
               date=dateFormat.parse(postItemArrayList.get(position).date);
           } catch (ParseException e) {
               e.printStackTrace();
           } catch (NullPointerException e){
               e.printStackTrace();
           }

           ((viewHolder)holder).TextView_upload_time.setText(beforeTime(date));



           //버튼을 클릭 했을 때의 동작 정의
           if(mListener!=null){
               //해당 포지션의 수정, 추가, 삭제 버튼을 누르면 해당 아이템의 수정, 추가, 삭제가 가능하게 어댑터의 뷰홀더 포지션을 받아옴

               //더 보기 버튼을 클릭할 시의 동작 정의
               ((viewHolder)holder).ImageButton_more.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Log.d("포커스 상태", String.valueOf(v.getContext().getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,true)));
                       //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                       final Dialog dialog=new Dialog(context);
                       //타이틀 바 없애기
                       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                       //다이얼로그에 연결시킬 레이아웃 설정
                       dialog.setContentView(R.layout.more_box);
                       //다이얼로그 띄우기
                       dialog.show();

                       TextView TextView_profile, TextView_edit, TextView_delete;


                       TextView_profile=dialog.findViewById(R.id.TextView_profile);
                       TextView_edit=dialog.findViewById(R.id.TextView_edit);
                       TextView_delete=dialog.findViewById(R.id.TextView_delete);

                       if(v.getContext().getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,true)==false){
                           TextView_edit.setVisibility(View.GONE);
                           TextView_delete.setVisibility(View.GONE);
                       }else{
                           TextView_edit.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   mListener.onEditPostButtonClicked(index);
                                   dialog.dismiss();
                               }
                           });

                           TextView_delete.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   mListener.onRemoveButtonClicked(index);
                                   dialog.dismiss();
                               }
                           });
                       }

                       TextView_profile.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mListener.onProfileButtonClicked(index);
                               dialog.dismiss();
                           }
                       });

                   }
               });

               ((viewHolder)holder).imageButton_comment.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentButtonClicked(index);



                   }
               });

               ((viewHolder)holder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onProfileButtonClicked(index);
                   }
               });



               ((viewHolder)holder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onNicknameButtonClicked(index);
                   }
               });

//            Log.d("게시글 라인 수: ", String.valueOf(((viewHolder)holder).TextView_article.getLineCount()));
//            if(((viewHolder)holder).TextView_article.getLineCount()==0){
//                ((viewHolder)holder).TextView_see_more.setVisibility(View.VISIBLE);
//            }
//            ((viewHolder)holder).TextView_see_more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ((viewHolder)holder).TextView_article.setMaxLines(Integer.MAX_VALUE);
//                        ((viewHolder)holder).TextView_see_more.setHeight(0);
//
//                    }
//
//                });



           }
       }else{
           final int index=holder.getAdapterPosition();
           Date date = new Date();
           BitmapFactory.Options postPictureOptions=new BitmapFactory.Options();
           postPictureOptions.inSampleSize=4;

           BitmapFactory.Options profileOptions=new BitmapFactory.Options();
           profileOptions.inSampleSize=30;




           ((viewHolderMultiple)holder).CircleImageView_profile_picture.setRotation(90);
           ((viewHolderMultiple)holder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(postItemArrayList.get(position).profilePicutreUri,profileOptions));
           ((viewHolderMultiple)holder).TextView_nickname.setText(postItemArrayList.get(position).nickname);
           ((viewHolderMultiple)holder).TextView_article.setText(postItemArrayList.get(position).article);
           ((viewHolderMultiple)holder).imageButton_comment.setImageResource(postItemArrayList.get(position).commentID);
           ((viewHolderMultiple)holder).TextView_comment_count.setText(String.valueOf(postItemArrayList.get(position).commentCount));
           ((viewHolderMultiple)holder).ImageButton_more.setImageResource(postItemArrayList.get(position).moreID);
           ((viewHolderMultiple)holder).ImageView_uploaded_picture.setRotation(90);
           ((viewHolderMultiple)holder).ImageView_uploaded_picture.setImageBitmap(BitmapFactory.decodeFile(postItemArrayList.get(position).postPictureUri,postPictureOptions));



           SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           try {
               date=dateFormat.parse(postItemArrayList.get(position).date);
           } catch (ParseException e) {
               e.printStackTrace();
           } catch (NullPointerException e){
               e.printStackTrace();
           }

           ((viewHolderMultiple)holder).TextView_upload_time.setText(beforeTime(date));



           //버튼을 클릭 했을 때의 동작 정의
           if(mListener!=null){
               //해당 포지션의 수정, 추가, 삭제 버튼을 누르면 해당 아이템의 수정, 추가, 삭제가 가능하게 어댑터의 뷰홀더 포지션을 받아옴

               //더 보기 버튼을 클릭할 시의 동작 정의
               ((viewHolderMultiple)holder).ImageButton_more.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Log.d("포커스 상태", String.valueOf(v.getContext().getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,true)));
                       //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                       final Dialog dialog=new Dialog(context);
                       //타이틀 바 없애기
                       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                       //다이얼로그에 연결시킬 레이아웃 설정
                       dialog.setContentView(R.layout.more_box);
                       //다이얼로그 띄우기
                       dialog.show();

                       TextView TextView_profile, TextView_edit, TextView_delete;


                       TextView_profile=dialog.findViewById(R.id.TextView_profile);
                       TextView_edit=dialog.findViewById(R.id.TextView_edit);
                       TextView_delete=dialog.findViewById(R.id.TextView_delete);

                       if(v.getContext().getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,true)==false){
                           TextView_edit.setVisibility(View.GONE);
                           TextView_delete.setVisibility(View.GONE);
                       }else{
                           TextView_edit.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   mListener.onEditPostButtonClicked(index);
                                   dialog.dismiss();
                               }
                           });

                           TextView_delete.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   mListener.onRemoveButtonClicked(index);
                                   dialog.dismiss();
                               }
                           });
                       }

                       TextView_profile.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mListener.onProfileButtonClicked(index);
                               dialog.dismiss();
                           }
                       });

                   }
               });

               ((viewHolderMultiple)holder).imageButton_comment.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onCommentButtonClicked(index);



                   }
               });

               ((viewHolderMultiple)holder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onProfileButtonClicked(index);
                   }
               });



               ((viewHolderMultiple)holder).TextView_nickname.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.onNicknameButtonClicked(index);
                   }
               });



            ((viewHolderMultiple)holder).TextView_see_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((viewHolderMultiple)holder).TextView_article.setMaxLines(Integer.MAX_VALUE);
                        ((viewHolderMultiple)holder).TextView_see_more.setVisibility(View.GONE);
                    }

                });



           }
       }


    }




    @Override
    public int getItemCount() {
        return (postItemArrayList==null) ? 0 : postItemArrayList.size();
    }








    private static class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_article, TextView_comment_count, TextView_comment_text, TextView_upload_time;
        ImageButton ImageButton_more, imageButton_comment;
        ImageView ImageView_uploaded_picture;


        public viewHolder(final View view) {
            super(view);
            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_article=view.findViewById(R.id.TextView_article);
            ImageButton_more=view.findViewById(R.id.ImageButton_more);
            imageButton_comment=view.findViewById(R.id.imageButton_comment);
            ImageView_uploaded_picture=view.findViewById(R.id.ImageView_uploaded_picture);
            TextView_comment_count=view.findViewById(R.id.TextView_comment_count);
            TextView_comment_text=view.findViewById(R.id.TextView_comment_text);
            TextView_upload_time=view.findViewById(R.id.TextView_upload_time);





        }
    }

    private static class viewHolderMultiple extends RecyclerView.ViewHolder {
        CircleImageView CircleImageView_profile_picture;
        TextView TextView_nickname, TextView_article, TextView_see_more, TextView_comment_count, TextView_comment_text, TextView_upload_time;
        ImageButton ImageButton_more, imageButton_comment;
        ImageView ImageView_uploaded_picture;


        public viewHolderMultiple(View view) {
            super(view);

            CircleImageView_profile_picture=view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_nickname=view.findViewById(R.id.TextView_nickname);
            TextView_article=view.findViewById(R.id.TextView_article);
            TextView_see_more=view.findViewById(R.id.TextView_see_more);
            ImageButton_more=view.findViewById(R.id.ImageButton_more);
            imageButton_comment=view.findViewById(R.id.imageButton_comment);
            ImageView_uploaded_picture=view.findViewById(R.id.ImageView_uploaded_picture);
            TextView_comment_count=view.findViewById(R.id.TextView_comment_count);
            TextView_comment_text=view.findViewById(R.id.TextView_comment_text);
            TextView_upload_time=view.findViewById(R.id.TextView_upload_time);

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
