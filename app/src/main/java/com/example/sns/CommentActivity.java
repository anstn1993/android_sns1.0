package com.example.sns;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sns.LoginActivity.Nickname;
import static com.example.sns.NotificationActivity.notificationItemArrayList;

public class CommentActivity extends AppCompatActivity implements CommentListAdapter.CommentRecyclerViewClickListener{
    CircleImageView CircleImageView_profile;
    ImageView ImageView_back;
    TextView TextView_nickname, TextView_article, TextView_addcomment;
    EditText EditText_comment;
    Bundle bundle=new Bundle();
    RecyclerView RecyclerView_comment_list;
    RecyclerView.LayoutManager layoutManager;
    CommentListAdapter commentListAdapter;
    static ArrayList<CommentItem> commentItemArrayList=new ArrayList<>();
    public static Uri uri;
    Bitmap bitmap;
    int postPosition;

//    interface commentActivityInterface{
//    void saveCommentArray(int position);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        ImageView_back=findViewById(R.id.ImageView_back);
        CircleImageView_profile=findViewById(R.id.CircleImageView_profile);
        TextView_nickname=findViewById(R.id.TextView_nickname);
        TextView_article=findViewById(R.id.TextView_article);
        TextView_addcomment=findViewById(R.id.TextView_addcomment);


        createCommentList();
        setRecyclerVier();

        Intent intent=getIntent();
        bundle=intent.getExtras();


        //이 변수는 메인의 게시물 인덱스를 담고 있다.
        postPosition=bundle.getInt("Position");
        Log.d("메인에서 넘어온 인덱스:", String.valueOf(postPosition));

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=8;

        try {//사진을 비트맵으로 얻기
            //이미지 뷰에 내가 가져온 사진 넣어주기
            uri= Uri.parse(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostProfile"+postPosition,null));
            bitmap = BitmapFactory.decodeFile(uri.getPath(),options);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
            CircleImageView_profile.setRotation(90);//사진을 90도 회전
            CircleImageView_profile.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

        } catch(NullPointerException e){
            e.printStackTrace();
        }

        TextView_nickname.setText(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+postPosition,null));
        TextView_article.setText(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostArticle"+postPosition,null));
//        try {//사진을 비트맵으로 얻기
//            //이미지 뷰에 내가 가져온 사진 넣어주기
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
//            CircleImageView_profile.setRotation(90);//사진을 90도 회전
//            CircleImageView_profile.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch(NullPointerException e){
//            e.printStackTrace();
//        }






        //메인에서 받는 인텐트(리사이클러 뷰 적용 이후 인텐트로 인해 오류가 생겨서 일단 주석 처리 함)
//        Intent intent=getIntent();
//        bundle=intent.getExtras();
//        String Article=bundle.getString("Text");
//        String Nickname=bundle.getString("Nickname");
//        String Comment=bundle.getString("Comment");
//        String CommentNickname=bundle.getString("CommentNickname");
//        //게시물을 쓴 사람의 닉네임 뷰에 닉네임 셋
//        TextView_nickname.setText(Nickname);
//        //게시물 글의 뷰에 게시글 셋
//        TextView_article.setText(Article);
//        //게시물의 댓글 텍스트에 댓글 셋



        //'게시'(댓글 달기) 버튼을 눌렀을 시의 동작 정의
        TextView_addcomment.setClickable(true);
        TextView_addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                final Dialog dialog=new Dialog(CommentActivity.this);
                //타이틀 바 없애기
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //다이얼로그에 연결시킬 레이아웃 설정
                dialog.setContentView(R.layout.comment_apply_box);




                //다이얼로그 띄우기
                dialog.show();


                TextView TextView_apply,TextView_cancel;

                EditText_comment=dialog.findViewById(R.id.EditText_comment);
                TextView_apply=dialog.findViewById(R.id.TextView_apply);
                TextView_cancel=dialog.findViewById(R.id.TextView_cancel);


                TextView_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(EditText_comment.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "댓글을 입력하세요.", Toast.LENGTH_LONG).show();
                        }else{
                            int position=commentItemArrayList.size();
                            addComment(position);


                            //게시물의 좋아요 버튼을 누르면 알림액티비티의 리사이클러뷰의 리스트가 추가되게 만들었다.
                            int index= notificationItemArrayList.size();
                            String nickname=getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+postPosition,null);
                            NotificationListAdapter notificationListAdapter = new NotificationListAdapter(notificationItemArrayList);


                            NotificationActivity.loadNotificationArray(CommentActivity.this, nickname);

                            clearNotificationIndex();
                            fillNotificationIndex();

                            if(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)==null){
                                //내 게시물에 내가 댓글을 다는 경우에는 알림이 나한테 오지 않도록 예외처리를 해줬다.
                                if(!nickname.equals(Nickname)){
                                    Uri defaultProfileUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.circle) + '/' + getResources().getResourceTypeName(R.drawable.circle) + '/' + getResources().getResourceEntryName(R.drawable.circle));
                                    Date date=new Date();
                                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String notificationTime=dateFormat.format(date);
                                    notificationItemArrayList.add(new NotificationItem(defaultProfileUri.getPath(), Nickname+"님이 회원님의 게시물에 댓글을 달았습니다.",Nickname,"\""+EditText_comment.getText().toString()+"\"", getSharedPreferences("NotificationIndex",MODE_PRIVATE).getInt("PostIndex" + postPosition,0),notificationTime));

                                }

                            }else{
                                //내 게시물에 내가 댓글을 다는 경우에는 알림이 나한테 오지 않도록 예외처리를 해줬다.
                                if(!nickname.equals(Nickname)){
                                    Date date=new Date();
                                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String notificationTime=dateFormat.format(date);
                                    notificationItemArrayList.add(new NotificationItem(Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(), Nickname+"님이 회원님의 게시물에 댓글을 달았습니다.",Nickname,"\""+EditText_comment.getText().toString()+"\"",getSharedPreferences("NotificationIndex",MODE_PRIVATE).getInt("PostIndex" + postPosition,0),notificationTime));
                                    Log.d("알림 인덱스: ", String.valueOf(getSharedPreferences("NotificationIndex",MODE_PRIVATE).getInt("PostIndex" + postPosition,0)));
                                    Log.d("댓글: ","\""+EditText_comment.getText().toString()+"\"" );

                                }

                            }



                            notificationListAdapter.notifyItemInserted(index);
                            notificationListAdapter.notifyItemRangeChanged(index, notificationItemArrayList.size());

                            NotificationActivity.saveNotificationArray(CommentActivity.this, nickname);

                            SharedPreferences postSharedpreferences=getSharedPreferences("MainPost",MODE_PRIVATE);
                            SharedPreferences.Editor postEditor=postSharedpreferences.edit();
                            postEditor.putInt("PostCommentCount"+postPosition, postSharedpreferences.getInt("PostCommentCount"+postPosition,0)+1);
                            postEditor.commit();

                            SharedPreferences gridSharedpreferences=getSharedPreferences(nickname+"GridPost",MODE_PRIVATE);
                            SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
                            gridEditor.putInt("PostCommentCount"+postPosition, gridSharedpreferences.getInt("PostCommentCount"+postPosition,0)+1);
                            gridEditor.commit();

                            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(EditText_comment.getWindowToken(),0);
                            EditText_comment.getText().clear();

                            dialog.dismiss();
                        }
                    }
                });

                TextView_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        //'뒤로가기'버튼을 눌렀을 시의 동작 정의
        ImageView_back.setClickable(true);
        ImageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(CommentActivity.this, MainActivity.class);
//                //댓글 달기 액티비티는 한 번 호출되고 나가게 되면 더 이상 존재할 필요가 없기 때문에 이전 액티비티가 호출되면서 사라지게 함
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                //댓글 입력 칸에 입력된 텍스트를 담는 변수
//                String Comment=EditText_comment.getText().toString();
//                //댓글 단 닉네임 텍스트를 담는 변수
//                bundle.putString("Comment", Comment);
//
//                intent.putExtras(bundle);
//                startActivity(intent);
                finish();
                return;
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCommentArray();
    }

    private void createCommentList() {
        //뷰 홀더에 들어갈 데이터들을 ArrayList에 담기
        commentItemArrayList=new ArrayList<>();
    }

    private void setRecyclerVier() {
        //리사이클러 뷰 핸들 주기
        RecyclerView_comment_list=findViewById(R.id.RecyclerView_comment_list);
        RecyclerView_comment_list.setHasFixedSize(true);

        //레이아웃 메니저 리니어 형식으로 주기
        layoutManager=new LinearLayoutManager(this);
        RecyclerView_comment_list.setLayoutManager(layoutManager);

        loadCommentArray();

        //리사이클러 뷰의 어댑터 설정
        commentListAdapter=new CommentListAdapter(this,commentItemArrayList);
        commentListAdapter.setOnClickListener(CommentActivity.this);
        RecyclerView_comment_list.setAdapter(commentListAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        bundle=intent.getExtras();



        String Article=bundle.getString("Article");
        TextView_article.setText(Article);


        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=8;

        try {//사진을 비트맵으로 얻기
            //이미지 뷰에 내가 가져온 사진 넣어주기
            uri= Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null));
            bitmap = BitmapFactory.decodeFile(uri.getPath(),options);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
            CircleImageView_profile.setRotation(90);//사진을 90도 회전
            CircleImageView_profile.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

        } catch(NullPointerException e){
            e.printStackTrace();
        }



    }

    @Override
    public void onRemoveButtonClicked(final int position) {
        //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
        final Dialog dialog=new Dialog(CommentActivity.this);
        //타이틀 바 없애기
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그에 연결시킬 레이아웃 설정
        dialog.setContentView(R.layout.comment_delete_check_box);

        //다이얼로그의 크기를 조절하기 위해서 사용한 코드
        //params변수를 다이얼로그의 속성으로 넣어주고
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        //다이얼로그의 속성이 들어간 param변수를 통해 다이얼로그의 width를 matchparent로 설정
        params.width= WindowManager.LayoutParams.MATCH_PARENT;
        //다이얼로그에 다시 params를 넣어줘서 속성 세팅
        dialog.getWindow().setAttributes(params);

        //다이얼로그 띄우기
        dialog.show();

        //Dialog의 레이아웃에 있는 텍스트뷰 객체를 선언
        TextView TextView_yes, TextView_no;

        //객체를 레이아웃의 뷰와 연결
        TextView_yes=dialog.findViewById(R.id.TextView_yes);
        TextView_no=dialog.findViewById(R.id.TextView_no);

        //
        TextView_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentItemArrayList.remove(position);
                commentListAdapter.notifyItemRemoved(position);
                commentListAdapter.notifyItemRangeChanged(position, commentItemArrayList.size());
                saveCommentArray();

                SharedPreferences postSharedpreferences=getSharedPreferences("MainPost",MODE_PRIVATE);
                SharedPreferences.Editor postEditor=postSharedpreferences.edit();
                postEditor.putInt("PostCommentCount"+postPosition, postSharedpreferences.getInt("PostCommentCount"+postPosition,0)-1);
                postEditor.commit();

                String nickname=getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+postPosition,null);
                SharedPreferences gridSharedpreferences=getSharedPreferences(nickname+"GridPost",MODE_PRIVATE);
                SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
                gridEditor.putInt("PostCommentCount"+postPosition, gridSharedpreferences.getInt("PostCommentCount"+postPosition,0)-1);
                gridEditor.commit();
                dialog.dismiss();



            }
        });

        TextView_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onEditButtonClicked(final int position) {
        final int index=bundle.getInt("Position");

        //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
       final Dialog dialog=new Dialog(this);
        //타이틀 바 없애기
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그에 연결시킬 레이아웃 설정
        dialog.setContentView(R.layout.comment_edit_box);



        //다이얼로그 띄우기
        dialog.show();

        TextView TextView_edit,TextView_cancel;
        final EditText EditText_comment;

        TextView_edit=dialog.findViewById(R.id.TextView_edit);
        TextView_cancel=dialog.findViewById(R.id.TextView_cancel);
        EditText_comment=dialog.findViewById(R.id.EditText_comment);

        EditText_comment.setText(getSharedPreferences("Comment"+index,MODE_PRIVATE).getString("CommentText"+position,null));

        //다이얼로그 속의 수정 버튼을 눌렀을 때의 동작 정의
        TextView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //댓글 수정 다이얼로그에서 수정된 댓글의 텍스트를 가져와서 담는 변수
                String editedComment=EditText_comment.getText().toString();
                Uri realProfileUri=Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null));
                //댓글 데이터를 수정하기 위해서 아이템의 데이터를 담는 클래스 객체 생성


                CommentItem commentItem=new CommentItem(
                        realProfileUri.getPath(),
                        getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                        editedComment,
                        "삭제",
                        "수정",
                        0,
                        getSharedPreferences("Comment"+index,MODE_PRIVATE).getString("CommentTime"+position,null),
                        EditText_comment.getLineCount()
                        );
                //위에서 만든 아이템 데이터를 담는 객체를 ArrayList의 해당 index에 설정
                commentItemArrayList.set(position,commentItem);
                //어댑터에 파라미터로 들어간 인덱스의 아이템 데이터가 변경됐다는 알림을 보내는 메소드
                commentListAdapter.notifyItemChanged(position);

                saveCommentArray();


                InputMethodManager inputMethodManager= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(EditText_comment.getWindowToken(),0);
                EditText_comment.getText().clear();
                dialog.dismiss();
            }
        });

        //취소버튼을 눌렀을 때의 동작 정의
        TextView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이얼로그 끄기
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onCommentClicked(int position) {
        int index=bundle.getInt("Position");
        if(Nickname.equals(getSharedPreferences("Comment"+index,MODE_PRIVATE).getString("CommentNickname"+position,null))){
            Intent intent = new Intent(CommentActivity.this, AccountActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else{
            Intent intent=new Intent(CommentActivity.this, AccountOthersActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle=new Bundle();
            bundle.putString("PostNickname", getSharedPreferences("Comment"+index,MODE_PRIVATE).getString("CommentNickname"+position,null));
            Log.d("닉네임: ",   getSharedPreferences("Comment"+index,MODE_PRIVATE).getString("CommentNickname"+position,null));
            bundle.putInt("Position",getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostIndex"+index,0));
            Log.d("인덱스: ",  String.valueOf(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostIndex"+index,0)));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void addComment(int position){

        Date date=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String commentDate=dateFormat.format(date);

        if(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)==null){
            Uri defaultProfileUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.circle) + '/' + getResources().getResourceTypeName(R.drawable.circle) + '/' + getResources().getResourceEntryName(R.drawable.circle));

            commentItemArrayList.add(new CommentItem(
                    defaultProfileUri.getPath(),
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    EditText_comment.getText().toString(),
                    "삭제",
                    "수정",
                    0,
                    commentDate,
                    EditText_comment.getLineCount()
                    ));
            saveCommentArray();
        }else{
            commentItemArrayList.add(new CommentItem(
                    Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(),
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    EditText_comment.getText().toString(),
                    "삭제",
                    "수정",
                    0,
                    commentDate,
                    EditText_comment.getLineCount()));
            saveCommentArray();
        }

        commentListAdapter.notifyItemInserted(position);
        commentListAdapter.notifyItemRangeChanged(position, commentItemArrayList.size());
    }

    //메인의 해당 게시물에 할당된 댓글 리사이클러뷰를 저장하는 메소드
    public void saveCommentArray(){
        Intent intent=getIntent();
        bundle=intent.getExtras();
        //메인 게시물의 인덱스를 담고 있는 변수
        int position=bundle.getInt("Position");
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
            commentEditor.putInt("ViewType"+i,commentItemArrayList.get(i).viewType);
            commentEditor.putString("CommentTime"+i,commentItemArrayList.get(i).date);
            commentEditor.putInt("CommentLine"+i,commentItemArrayList.get(i).textLine);

        }

        commentEditor.commit();
    }

    //메인의 해당 게시물에 할당된 댓글 리사이클러뷰를 로드하는 메소드
    //메인에서 특정 게시물의 댓글 버튼을 누르면 해당 게시물의 인덱스를 받아서 그 게시물의 댓글들을 로드한다.
    public void loadCommentArray(){
        Intent intent=getIntent();
        bundle=intent.getExtras();
        int position=bundle.getInt("Position");
        SharedPreferences postSharedPreferences=getSharedPreferences("Comment"+position,MODE_PRIVATE);
        commentItemArrayList.clear();
        int commentSize=postSharedPreferences.getInt("CommentSize",0);
        Log.d("인덱스", String.valueOf(position));
        for(int i=0; i<commentSize; i++){
            commentItemArrayList.add(new CommentItem(
                    postSharedPreferences.getString("CommentProfile"+i,null),
                    postSharedPreferences.getString("CommentNickname"+i,null),
                    postSharedPreferences.getString("CommentText"+i,null),
                    postSharedPreferences.getString("CommentDelete"+i,null),
                    postSharedPreferences.getString("CommentEdit"+i,null),
                    postSharedPreferences.getInt("ViewType"+i,0),
                    postSharedPreferences.getString("CommentTime"+i,null),
                    postSharedPreferences.getInt("CommentLine"+i,0)
            ));
        }
    }


    //이 메소드는 알림의 내용을 클릭했을 때 해당 게시물로 이동하기 위해서 만들어진 메소드다.
    //이 메소드는 특정 게시물에 댓글이 작성되면 그 게시물의 주인의 계정 상의 그리드 뷰의 인덱스를 반환하기 위해 필요하다.
    //가령 만수가 라임에게 댓글을 달면 라임의 알림에 알림이 추가되는데 이때 그 알림에 댓글이 달린 라임의 게시물이 라임의 그리드 게시물 상에서 몇 번째 게시물인지를 알아야
    //MyPostActivity에 인덱스로 넘겨줘서 게시물을 set할 수 있기에 이 메소드가 필요하다.
    public void fillNotificationIndex(){
        //value변수는 댓글이 달린 게시물의 그리드 리사이클러뷰 상에서의 인덱스를 의미한다.
        int value=0;
        SharedPreferences notificationIndexSharedPreferences=getSharedPreferences("NotificationIndex",MODE_PRIVATE);
        SharedPreferences.Editor notificationIndexEditor=notificationIndexSharedPreferences.edit();
        //그리고 i 변수는 댓글이 달린 게시물의 메인 리사이클러뷰 상에서의 인덱스를 의미한다.
        for(int i=0; i<getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostSize",0); i++){
            //메인리사이클러뷰의 게시물의 모든 닉네임 중에서 내가 댓글을 단 게시물의 주인 닉네임과 일치하는 경우만 필터링하기 위해서 해당 조건문을 설정했다.
            if(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+i,null).equals(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+postPosition,null))){
                //그렇게 해서 메인의 인덱스를 키 값에 넣어주고 그 인덱스를 가진 키의 값은 그리드 뷰의 인덱스를 담게 된다.
                notificationIndexEditor.putInt("PostIndex" + i, value);
                notificationIndexEditor.commit();
                    //그리드의 인덱스를 하나 채웠으면 이제 value를 1추가하여 다음 그리드 뷰의 인덱스를 담는다.
                    value += 1;
                }

            }
        }

    //이 메소드는 매번 알림을 저장할 때마다 fillNotificationindex()메소드를 초기화해서 실행시키기 위해서 필요한 메소드다.
    public void clearNotificationIndex(){
        SharedPreferences notificationIndexSharedPreferences=getSharedPreferences("NotificationIndex",MODE_PRIVATE);
        SharedPreferences.Editor notificationIndexEditor=notificationIndexSharedPreferences.edit();
        notificationIndexEditor.clear();
        notificationIndexEditor.commit();
    }
}
