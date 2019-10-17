package com.example.sns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import static com.example.sns.CommentActivity.commentItemArrayList;
import static com.example.sns.LoginActivity.Nickname;
import static com.example.sns.PostActivity.loadPostArray;
import static com.example.sns.PostActivity.postItemArrayList;
import static com.example.sns.PostActivity.postPictureItemArrayList;

public class MyPostActivity extends AppCompatActivity {


    CircleImageView CircleImageView_profile_picture;
    TextView TextView_nickname, TextView_article, TextView_comment_text, TextView_comment_count, TextView_upload_time;
    ImageButton ImageButton_more, CheckBox_like, imageButton_comment;
    ImageView ImageView_uploaded_picture;
    Bundle bundle=new Bundle();
    public static ArrayList<PostItem> myGridPostArrayList=new ArrayList<>();
    MainPostListAdapter mainPostListAdapter;
    AccountPostGridAdapter accountPostGridAdapter;
    CommentListAdapter commentListAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        CircleImageView_profile_picture=findViewById(R.id.CircleImageView_profile_picture);
        TextView_nickname=findViewById(R.id.TextView_nickname);
        TextView_article=findViewById(R.id.TextView_article);
        ImageButton_more=findViewById(R.id.ImageButton_more);

        imageButton_comment=findViewById(R.id.imageButton_comment);
        ImageView_uploaded_picture=findViewById(R.id.ImageView_uploaded_picture);
        TextView_comment_text=findViewById(R.id.TextView_comment_text);
        TextView_comment_count=findViewById(R.id.TextView_comment_count);
        TextView_upload_time=findViewById(R.id.TextView_upload_time);

        Intent intent=getIntent();
        bundle=intent.getExtras();

        //계정 액티비티에서 그리드 뷰의 인덱스를 받아서 해당 인덱스를 로드
        loadPostArray(MyPostActivity.this);

        //액티비티에 진입할 때마다 계속 arraylist를 클리어해줘야 하는 이유는 타인의 포스트와 나의 포스트 상의 댓글 액티비티를 공유하기 때문이다.
        //댓글 액티비티를 공유하는 상황이기 때문에 메인에서의 게시물 인덱스를 항상 새롭게 갱신해주기 위해서 선언하는 것!
        clearMyGridPostArray(MyPostActivity.this);
        fillMyGridPostArray(MyPostActivity.this);



        setMyPost();

        //더 보기 버튼을 클릭했을 때의 동작 정의
        ImageButton_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                final Dialog dialog=new Dialog(MyPostActivity.this);
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

                TextView_profile.setVisibility(View.GONE);


                TextView_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //계정의 그리드뷰의 인덱스
                        int position=bundle.getInt("Position");
                        Intent intent=new Intent(MyPostActivity.this, EditPostInMyAccountActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("Position",position);
                        Log.d("인덱스",String.valueOf(position));
                        bundle.putString("PostPicture",getSharedPreferences(Nickname+"GridPost",MODE_PRIVATE).getString("PostPicture"+position,null));
                        bundle.putString("PostArticle",getSharedPreferences(Nickname+"GridPost",MODE_PRIVATE).getString("PostArticle"+position,null));
                        bundle.putString("PostAddress",getSharedPreferences(Nickname+"GridPost",MODE_PRIVATE).getString("PostAddress"+position,null));
                        bundle.putFloat("PostLatitude",getSharedPreferences("MainPost",MODE_PRIVATE).getFloat("PostLatitude"+position, 1000));
                        bundle.putFloat("PostLongitude",getSharedPreferences("MainPost",MODE_PRIVATE).getFloat("PostLongitude"+position, 1000));



                        intent.putExtras(bundle);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                //더보기에서 삭제버튼을 누를 경우의 동작을 정의
                TextView_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                        final Dialog dialog1=new Dialog(MyPostActivity.this);
                        //타이틀 바 없애기
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //다이얼로그에 연결시킬 레이아웃 설정
                        dialog1.setContentView(R.layout.post_delete_check_box);
                        //다이얼로그 띄우기
                        dialog1.show();

                        TextView TextView_yes, TextView_no;


                        TextView_yes=dialog1.findViewById(R.id.TextView_yes);
                        TextView_no=dialog1.findViewById(R.id.TextView_no);

                        //'예' 버튼을 클릭
                        TextView_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position=bundle.getInt("Position");

                                PostActivity.loadPostArray(MyPostActivity.this);
                                PostActivity.loadGridPostArray(MyPostActivity.this, Nickname);

                                MainActivity.clearMyPostArray(MyPostActivity.this);
                                MainActivity.fillMyPostArray(MyPostActivity.this);

                                clearMyGridPostArray(MyPostActivity.this);
                                fillMyGridPostArray(MyPostActivity.this);

                                //포스트가 지워짐
                                postItemArrayList.remove(getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
                                Log.d("삭제하려는 메인 게시물의 인덱스", String.valueOf(getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
                                //내 계정의 그리드 뷰의 게시물도 함께 지워짐
                                postPictureItemArrayList.remove(position);
                                //포스트에 담겨있는 댓글이 모두 지워짐
                                deleteCommentPackage(getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+position,0));

                                mainPostListAdapter=new MainPostListAdapter(postItemArrayList, MainActivity.mainActivity);
                                mainPostListAdapter.notifyItemRemoved(getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
                                mainPostListAdapter.notifyItemRangeChanged(getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+position,0), postItemArrayList.size());
                                accountPostGridAdapter=new AccountPostGridAdapter(postPictureItemArrayList);
                                accountPostGridAdapter.notifyItemRemoved(position);
                                accountPostGridAdapter.notifyItemRangeChanged(position, postPictureItemArrayList.size());
                                commentListAdapter=new CommentListAdapter(MyPostActivity.this, commentItemArrayList);
                                commentListAdapter.notifyItemRemoved(position);
                                commentListAdapter.notifyItemRangeChanged(position, commentItemArrayList.size());

                                //변화된 포스트 Array를 저장
                                PostActivity.savePostArray(MyPostActivity.this);
                                PostActivity.loadPostArray(MyPostActivity.this);
                                //변화된 그리드 포스트 Array를 저장
                                PostActivity.saveGridPostArray(MyPostActivity.this,Nickname);
                                PostActivity.loadGridPostArray(MyPostActivity.this,Nickname);

                                clearMyGridPostArray(MyPostActivity.this);
                                fillMyGridPostArray(MyPostActivity.this);

                                MainActivity.clearMyPostArray(MyPostActivity.this);
                                MainActivity.fillMyPostArray(MyPostActivity.this);


                                dialog1.dismiss();
                                finish();

                            }
                        });

                        //'아니요' 버튼 클릭
                        TextView_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });

                        //더 보기 다이얼로그를 종료
                        dialog.dismiss();
                    }
                });
            }
        });

        TextView_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPostActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPostActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });




        imageButton_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyPostActivity.this, CommentActivity.class);
                //이 변수에 담긴 값은 메인의 게시물 인덱스다.
                //MyGridPost파일의 데이터는 해당 액티비티 하단에서 추가된다.(fillMyGridPostArray메소드)
                //해당 변수는 메인 게시물의 인덱스를 담고 있다.
                int position=getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1);
                Bundle bundle=new Bundle();
                bundle.putInt("Position",position);
                Log.d("인덱스",String.valueOf(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        setMyPost();
    }

    public void setMyPost(){
//        int position=bundle.getInt("Position");
//        SharedPreferences postSharedPreferences=getSharedPreferences(Nickname+"GridPost",MODE_PRIVATE);
        BitmapFactory.Options postPictureOptions=new BitmapFactory.Options();
        postPictureOptions.inSampleSize=4;

        BitmapFactory.Options profileOptions=new BitmapFactory.Options();
        profileOptions.inSampleSize=30;
        CircleImageView_profile_picture.setRotation(90);
        CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostProfile"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),null),profileOptions));
        TextView_nickname.setText(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),null));
        TextView_article.setText(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostArticle"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),null));
        ImageButton_more.setImageResource(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostMore"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),0));
        imageButton_comment.setImageResource(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostComment"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),0));
        TextView_comment_count.setText(String.valueOf(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),0)));
        ImageView_uploaded_picture.setRotation(90);
        ImageView_uploaded_picture.setImageBitmap(BitmapFactory.decodeFile(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostPicture"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),null),postPictureOptions));

        Date uploadTime=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostTime"+getSharedPreferences("MyGridPost",MODE_PRIVATE).getInt("PostIndex"+bundle.getInt("Position"),1),null);
        try {
            uploadTime=dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        TextView_upload_time.setText(beforeTime(uploadTime));
    }




    public static void fillMyGridPostArray(Context context){
        //이 변수는 자신의 그리드뷰의 인덱스다. 처음으로 메인에서 넘어온 게시물이 곧 나의 첫번째 게시물이기 때문에 0으로 설정한 후
        //최초로 넘어온 게시물의 인덱스를 PostIndex0에 저장하기 위해서 선언
        int value=0;
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MyGridPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();

        for(int i=0; i<context.getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostSize",0); i++){

            //메인의 게시물 포커스 상태가 true면 그게 곧 나의 게시물임을 의미하기 때문에 나의 게시물만 필터링하기 위해서 만든 조건문이다.
            //그럼 만약 다른 사람의 게시물에 들어가서 댓글을 보려면 이렇게 하면 잘못된 인덱스가 넘어가는 거 아닌가?
            if(postItemArrayList.get(i).focusState==true){
                Log.d("메인에서의 내 게시물 포커스",String.valueOf(i));
                //나의 게시물에 해당하는 경우에만 인덱스로 추가를 해준다.
                Log.d("메인에서의 내 게시물 인덱스",String.valueOf(postItemArrayList.get(i).index));
                postEditor.putInt("PostIndex"+value,i);
                postEditor.commit();
                //0에 저장이 됐으면 그 다음에는 1에 저장되어야 하고 이 과정이 반복되어야 하기 때문에 value를 1씩 증가시킨다.
                value=value+1;


            }
        }

    }

    public static void clearMyGridPostArray(Context context){
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MyGridPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
        postEditor.clear();
        postEditor.commit();
    }

    //게시물이 삭제되는 순간 그 게시물에 달린 댓글을 삭제하고 기존의 댓글 꾸러미 인덱스를 재정렬하기 위한 메소드
    public void deleteCommentPackage(int position){
        //여기서의 position은 삭제된 게시물의 인덱스를 의미한다. 게시물의 인덱스와 댓글 꾸러미의 인덱스는 같기 때문에
        //게시물이 삭제되면 댓글 꾸러미도 삭제해준다.
        SharedPreferences commentSharedPreferences=getSharedPreferences("Comment"+position,MODE_PRIVATE);
        SharedPreferences.Editor commentEditor=commentSharedPreferences.edit();
        commentEditor.clear();
        commentEditor.commit();


        //그리고 삭제된 인덱스부터 가장 높은 숫자를 가진 인덱스까지 인덱스 숫자가 하나씩 줄어들게 되기 때문에 그것들을 재정렬해줘야 삭제된 이후에도 게시물에 맞는 댓글이 붙어있게 된다.
        //가령 개시물 인덱스가 0~9까지 있는데(즉 게시물이 10개) 내가 그 중에서 3번째 게시물(인덱스2)을 삭제하면 댓글 꾸러미도 함께 지워진다. 그럼 인덱스3부터 인덱스 9까지의 게시물은
        ///인덱스가 2부터 8이 되게 된다. 근데 이미 2~8은 고유의 댓글 꾸러미를 가지고 있기 때문에 이에 대한 조치를 하지 않으면 게시물에 맞는 댓글이 보이지 않고 하나씩 밀려서 보이게 된다.
        //이걸 재정렬 해줘야 하는 것이다. 즉 삭제되기 전에 comment3에 저장되어있던 댓글들은 이제 comment2에 저장을 시켜야 하는 것이다. comment4는 comment3에.......
        //지금부터 나오는 코드가 그것에 대한 작업이다.

        //우선 메인 포스트 파일을 가져와서
        SharedPreferences postSharedPreferences=getSharedPreferences("MainPost",MODE_PRIVATE);
        //삭제된 인덱스부터 마지막 인덱스까지 재정렬작업을 반복문을 통해서 해준다.
        for(int i=position; i<postSharedPreferences.getInt("PostSize",0); i++){
            SharedPreferences revisedCommentSharedPreferences=getSharedPreferences("Comment"+i,MODE_PRIVATE);
            SharedPreferences.Editor revisedCommentEditor=revisedCommentSharedPreferences.edit();
            //i번째 인덱스에 i+1번째 인덱스에 있던 댓글을 저장해야 하기 때문에 우선 i+1의 댓글 사이즈를 가져와서 저장시켜준다.
            //그래야 나중에 load메소드를 통해서 load를 할 때 반복문의 범위를 설정해줄 수 있다.
            revisedCommentEditor.putInt("CommentSize",getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getInt("CommentSize",0));

            //이 반복문은 i번째 댓글꾸러미의 댓글들을 i+1번째 댓글꾸러미의 댓글에서 가져와 하나씩 옮겨주기 위해 설정했다.
            //즉 위의 반복문은 댓글 꾸러미의 인덱스 재정렬을 위해 존재하고 그게 끝났으면 이제 이 반복문을 위해 그 꾸러미 속의 댓글을 하나씩 다 옮겨주는 것이다.
            for(int j=0; j<getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getInt("CommentSize",0);j++){
                revisedCommentEditor.putString("CommentProfile"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getString("CommentProfile"+j,null));
                revisedCommentEditor.putString("CommentNickname"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getString("CommentNickname"+j,null));
                revisedCommentEditor.putString("CommentText"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getString("CommentText"+j,null));
                revisedCommentEditor.putString("CommentDelete"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getString("CommentDelete"+j,null));
                revisedCommentEditor.putString("CommentEdit"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getString("CommentEdit"+j,null));
                revisedCommentEditor.putInt("ViewType"+j,getSharedPreferences("Comment"+(i+1),MODE_PRIVATE).getInt("ViewType"+j,0));
            }

            revisedCommentEditor.commit();
        }

        //전부 옮겼으면 이제 삭제되기 전에 가장 마지막 인덱스에 자리하고 있던 댓글 꾸러미를 지워줘서 이후에 게시물을 써서 그 자리를 채워도 댓글이 생기는 이상한 현상이 벌어지지 않게 한다.
        //게시물 사이즈에서 1을 빼주는 이유는 당연히 인덱스는 0부터 시작하기 때문이다.
        SharedPreferences lastCommentSharedPreferences=getSharedPreferences("Comment"+(postSharedPreferences.getInt("PostSize",0)-1),MODE_PRIVATE);
        SharedPreferences.Editor lastCommentEditor=lastCommentSharedPreferences.edit();
        lastCommentEditor.clear();
        lastCommentEditor.commit();
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
