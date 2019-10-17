package com.example.sns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sns.CommentActivity.commentItemArrayList;
import static com.example.sns.PostActivity.loadPostArray;
import static com.example.sns.PostActivity.myPostArrayList;
import static com.example.sns.PostActivity.postItemArrayList;

public class OthersPostActivity extends AppCompatActivity{


    CircleImageView CircleImageView_profile_picture;
    TextView TextView_nickname, TextView_article, TextView_comment_text, TextView_comment_count, TextView_upload_time;
    ImageButton CheckBox_like, imageButton_comment;
    ImageView ImageView_uploaded_picture;
    Bundle bundle=new Bundle();

    MainPostListAdapter mainPostListAdapter;
    AccountPostGridAdapter accountPostGridAdapter;
    CommentListAdapter commentListAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_post);

        CircleImageView_profile_picture=findViewById(R.id.CircleImageView_profile_picture);
        TextView_nickname=findViewById(R.id.TextView_nickname);
        TextView_article=findViewById(R.id.TextView_article);


        imageButton_comment=findViewById(R.id.imageButton_comment);
        ImageView_uploaded_picture=findViewById(R.id.ImageView_uploaded_picture);
        TextView_comment_text=findViewById(R.id.TextView_comment_text);
        TextView_comment_count=findViewById(R.id.TextView_comment_count);
        TextView_upload_time=findViewById(R.id.TextView_upload_time);

        Intent intent=getIntent();
        bundle=intent.getExtras();


        loadPostArray(OthersPostActivity.this);

        //액티비티에 진입할 때마다 계속 arraylist를 클리어해줘야 하는 이유는 타인의 포스트와 나의 포스트 상의 댓글 액티비티를 공유하기 때문이다.
        //댓글 액티비티를 공유하는 상황이기 때문에 메인에서의 게시물 인덱스를 항상 새롭게 갱신해주기 위해서 선언하는 것!
        clearOthersGridPostArray(this);
        fillOthersGridPostArray(this);

        setOthersPost();





        imageButton_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OthersPostActivity.this, CommentActivity.class);
                int position=getSharedPreferences("OthersGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),0);
                Bundle bundle=new Bundle();
                bundle.putInt("Position",position);
                Log.d("메인에서의 인덱스",String.valueOf(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        TextView_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=getSharedPreferences("OthersGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),0);
                    Intent intent=new Intent(OthersPostActivity.this, AccountOthersActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                    intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle bundle=new Bundle();
                    bundle.putString("PostNickname", getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null));
                    intent.putExtras(bundle);
                    startActivity(intent);

            }
        });

        CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=getSharedPreferences("OthersGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),0);
                Intent intent=new Intent(OthersPostActivity.this, AccountOthersActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle=new Bundle();
                bundle.putString("PostNickname", getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null));
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        setOthersPost();
    }


    public void setOthersPost(){
        int position=bundle.getInt("Position");
        String nickname=bundle.getString("PostNickname");
        SharedPreferences postSharedPreferences=getSharedPreferences(nickname+"GridPost",MODE_PRIVATE);
        BitmapFactory.Options postPictureOptions=new BitmapFactory.Options();
        postPictureOptions.inSampleSize=4;

        BitmapFactory.Options profileOptions=new BitmapFactory.Options();
        profileOptions.inSampleSize=30;
        CircleImageView_profile_picture.setRotation(90);
        CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(postSharedPreferences.getString("PostProfile"+position,null),profileOptions));
        TextView_nickname.setText(postSharedPreferences.getString("PostNickname"+position,null));
        TextView_article.setText(postSharedPreferences.getString("PostArticle"+position,null));
        imageButton_comment.setImageResource(postSharedPreferences.getInt("PostComment"+position,0));
        TextView_comment_count.setText(String.valueOf(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+getSharedPreferences("OthersGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),0)));
        ImageView_uploaded_picture.setRotation(90);
        ImageView_uploaded_picture.setImageBitmap(BitmapFactory.decodeFile(postSharedPreferences.getString("PostPicture"+position,null),postPictureOptions));

        Date uploadTime=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=postSharedPreferences.getString("PostTime"+position,null);
        try {
            uploadTime=dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        TextView_upload_time.setText(beforeTime(uploadTime));
    }


    public void saveCommentArray(int position) {
        Log.d("인덱스",String.valueOf(position));
        //게시글 입력란에 작성한 게시글을 담는 String 변수
        SharedPreferences postSharedPreferences=getSharedPreferences("Comment"+position,MODE_PRIVATE);
        SharedPreferences.Editor commentEditor=postSharedPreferences.edit();
        commentEditor.putInt("CommentSize",commentItemArrayList.size());


        for(int i=0; i<commentItemArrayList.size(); i++){
            commentEditor.putString("CommentProfile"+i,commentItemArrayList.get(i).profilePicture);
            commentEditor.putString("CommentNickname"+i,commentItemArrayList.get(i).nickname);
            commentEditor.putString("CommentText"+i,commentItemArrayList.get(i).comment);
            commentEditor.putString("CommentDelete"+i,commentItemArrayList.get(i).commentDelete);
            commentEditor.putString("CommentEdit"+i,commentItemArrayList.get(i).commentEdit);

        }

        commentEditor.commit();

    }

    public void fillOthersGridPostArray(Context context){
        //이 변수는 타인의 그리드뷰의 인덱스다. 처음으로 메인에서 넘어온 게시물이 곧 타인의 첫번째 게시물이기 때문에 0으로 설정한 후
        //최초로 넘어온 게시물의 인덱스를 PostIndex0에 저장하기 위해서 선언
        int value=0;
        SharedPreferences postSharedPreferences=context.getSharedPreferences("OthersGridPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();

        for(int i=0; i<context.getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostSize",0); i++){

            //메인의 게시물 포커스 상태가 false면 그게 곧 타인의 게시물임을 의미하기 때문에 타인의 게시물만 필터링하기 위해서 만든 조건문이다.

            if(postItemArrayList.get(i).focusState==false && postItemArrayList.get(i).nickname.equals(bundle.getString("PostNickname"))){

                Log.d("메인에서의 게시물 포커스",String.valueOf(postItemArrayList.get(i).focusState));
                //나의 게시물에 해당하는 경우에만 myGridPostArrayList에 추가를 해준다.
//                myGridPostArrayList.add(new PostItem(
//                        postItemArrayList.get(i).profilePicutreUri,
//                        postItemArrayList.get(i).postPictureUri,
//                        postItemArrayList.get(i).likeID,
//                        postItemArrayList.get(i).moreID,
//                        postItemArrayList.get(i).commentID,
//                        postItemArrayList.get(i).nickname,
//                        postItemArrayList.get(i).article,
//                        postItemArrayList.get(i).likeState,
//                        postItemArrayList.get(i).index,
//                        postItemArrayList.get(i).focusState,
//                        postItemArrayList.get(i).latitude,
//                        postItemArrayList.get(i).longitude,
//                        postItemArrayList.get(i).address
//                ));
                Log.d("메인에서의 게시물 인덱스",String.valueOf(i));

                postEditor.putInt("PostSize",myPostArrayList.size());
                postEditor.putString("PostProfile"+value,postItemArrayList.get(i).profilePicutreUri);
                postEditor.putString("PostPicture"+value,postItemArrayList.get(i).postPictureUri);
                postEditor.putInt("PostLike"+value,postItemArrayList.get(i).likeID);
                postEditor.putInt("PostComment"+value,postItemArrayList.get(i).commentID);
                postEditor.putInt("PostMore"+value,postItemArrayList.get(i).moreID);postEditor.putString("PostNickname"+i,postItemArrayList.get(i).nickname);
                postEditor.putString("PostArticle"+value,postItemArrayList.get(i).article);
                postEditor.putBoolean("PostLikeState"+value,postItemArrayList.get(i).likeState);
                postEditor.putInt("PostIndex"+value,i);
                postEditor.putBoolean("PostFocusState" + value, postItemArrayList.get(i).focusState);
                postEditor.putFloat("PostLatitude"+value,postItemArrayList.get(i).latitude);
                postEditor.putFloat("PostLongitude"+value,postItemArrayList.get(i).longitude);
                postEditor.putString("PostAddress"+value,postItemArrayList.get(i).address);
                postEditor.commit();
                //0에 저장이 됐으면 그 다음에는 1에 저장되어야 하고 이 과정이 반복되어야 하기 때문에 value를 1씩 증가시킨다.
                value=value+1;


            }
        }

    }

    public static void clearOthersGridPostArray(Context context){
//        myGridPostArrayList.clear();
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MyGridPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
        postEditor.clear();
        postEditor.commit();
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
