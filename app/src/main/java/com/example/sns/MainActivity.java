package com.example.sns;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.sns.CommentActivity.commentItemArrayList;
import static com.example.sns.LoginActivity.Nickname;
import static com.example.sns.PostActivity.postItemArrayList;
import static com.example.sns.PostActivity.postPictureItemArrayList;


public class MainActivity extends AppCompatActivity implements MainPostListAdapter.PostRecyclerViewClickListener, Runnable {
    ImageButton Button_search, Button_post, Button_notification, Button_account, ImageButton_messenger;
    ImageView ImageView_logo;
    public Bundle bundle=new Bundle();
    RecyclerView RecyclerView_main_post_list;
    RecyclerView.LayoutManager layoutManager;
    LinearLayout linearLayout_suggestupload;


    MainPostListAdapter mainPostListAdapter;
    AccountPostGridAdapter accountPostGridAdapter;
    CommentListAdapter commentListAdapter;
    int postRequest=1000;
    String profilePhotoUri;
    public static ArrayList<PostItem> postItemArrayList1=new ArrayList<>();
    //AccountActivity에서 로그아웃을 했을 때 메인 액티비티를 따로 종료시키기 위해서 만든 static 변수(AccountActivity 참조)
    public static Activity mainActivity;
    public Uri bitmapUri;
    static int animationState=0;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation=new AlphaAnimation(0,1);
            animation.setDuration(1000);
            linearLayout_suggestupload.setVisibility(View.VISIBLE);
            linearLayout_suggestupload.setAnimation(animation);

        }
    };

    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation disappearAnimation;
            disappearAnimation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.disappear);
            linearLayout_suggestupload.setVisibility(View.GONE);
            linearLayout_suggestupload.setAnimation(disappearAnimation);

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button_search = findViewById(R.id.Button_search);
        Button_post = findViewById(R.id.Button_post);
        Button_notification = findViewById(R.id.Button_notification);
        Button_account = findViewById(R.id.Button_account);
        ImageButton_messenger = findViewById(R.id.ImageButton_messenger);
        ImageView_logo = findViewById(R.id.ImageView_logo);
        linearLayout_suggestupload=findViewById(R.id.linearLayout_suggestupload);

        //위에서 선언한 static변수가 이 액티비티를 의미한다는 것을 선언-로그아웃할 때 이 메인액티를 지우기 위해 선언했음
        mainActivity=MainActivity.this;

        //  번들을 받는 인텐트
        Intent intent = getIntent();
        bundle = intent.getExtras();
        try{
            profilePhotoUri=bundle.getString("ProfileUri");

        }catch (NullPointerException e){

        }

        //리사이클러뷰의 레이아웃 메니저와 어댑터 설정 및 저장된 리트스 불러오기
        setRecyclerView();

        try{
            clearMyPostArray(MainActivity.this);
            fillMyPostArray(MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //포스트 업로드 추천 말풍선이 뜨게 하고 사라지게 하는 스레드 실행
        Thread thread=new Thread(MainActivity.this);
        thread.setDaemon(true);
        thread.start();







        linearLayout_suggestupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_suggestupload.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                handler1.removeMessages(0);

            }
        });



        //하단의 검색 버튼을 누르면 검색 액티비티로 넘어가는 동작
        Button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });


        //하단의 포스트 버튼을 누르면 포스트 액티비티로 넘어가는 동작
        Button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_suggestupload.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                handler1.removeMessages(0);

            }
        });

        //알림 버튼을 누르면 알림 액티비티로 넘어가는 동작
        Button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //계정 버튼을 누르면 계정 액티비티로 넘어가는 동작
        Button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //메신저 버튼을 누르면 메신저 액티비티로 넘어가는 동작
        ImageButton_messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setRecyclerView();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intent=getIntent();
        bundle=intent.getExtras();

    }

    private void setRecyclerView() {
        //리사이클러뷰 객체 핸들
        RecyclerView_main_post_list=findViewById(R.id.RecyclerView_main_post_list);
        RecyclerView_main_post_list.setHasFixedSize(true);
        //리사이클러뷰 레이아웃 매니저를 리니어 레이아웃 매니저로 설정
        layoutManager=new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        RecyclerView_main_post_list.setLayoutManager(layoutManager);


//        Gson gson = new Gson();
//        Type listType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//        String postList = getSharedPreferences(ID+"MainPost", MODE_PRIVATE).getString("PostArray", null);
//
//        postItemArrayList1=gson.fromJson(postList, listType);
//        postItemArrayList1=postItemArrayList;
        PostActivity.loadPostArray(this);
        PostActivity.loadGridPostArray(this,Nickname);


        mainPostListAdapter=new MainPostListAdapter(postItemArrayList,this);
        mainPostListAdapter.setOnClickListener(MainActivity.this);

        RecyclerView_main_post_list.setAdapter(mainPostListAdapter);
    }


    //PostRecyclerViewClickListener인터페이스에서 오버라이드된 메소드
    @Override
    public void onRemoveButtonClicked(final int position) {
        //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
        final Dialog dialog=new Dialog(MainActivity.this);
        //타이틀 바 없애기
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그에 연결시킬 레이아웃 설정
        dialog.setContentView(R.layout.post_delete_check_box);
        //다이얼로그 띄우기
        dialog.show();

        TextView TextView_yes, TextView_no;


        TextView_yes=dialog.findViewById(R.id.TextView_yes);
        TextView_no=dialog.findViewById(R.id.TextView_no);

        //'예' 버튼을 클릭
        TextView_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostActivity.loadPostArray(MainActivity.this);
                PostActivity.loadGridPostArray(MainActivity.this, Nickname);

                //포스트가 지워짐
                postItemArrayList.remove(position);
                //내 계정의 그리드 뷰의 게시물도 함께 지워짐

                postPictureItemArrayList.remove(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
                Log.d("지워진 그리드 인덱스", String.valueOf(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
                deleteCommentPackage(position);

                mainPostListAdapter.notifyItemRemoved(position);
                mainPostListAdapter.notifyItemRangeChanged(position, postItemArrayList.size());
                accountPostGridAdapter=new AccountPostGridAdapter(postPictureItemArrayList);
                accountPostGridAdapter.notifyItemRemoved(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
                accountPostGridAdapter.notifyItemRangeChanged(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0), postPictureItemArrayList.size());
                commentListAdapter=new CommentListAdapter(MainActivity.this, commentItemArrayList);
                commentListAdapter.notifyItemRemoved(position);
                commentListAdapter.notifyItemRangeChanged(position, commentItemArrayList.size());

                //변화된 포스트 Array를 저장
                PostActivity.savePostArray(MainActivity.this);
                //변화된 그리드 포스트 Array를 저장
                PostActivity.saveGridPostArray(MainActivity.this,Nickname);
                //변화된 포스트 Array를 load
                PostActivity.loadPostArray(MainActivity.this);
                PostActivity.loadGridPostArray(MainActivity.this, Nickname);

                //나의 포스트 Array를 비우고
                clearMyPostArray(MainActivity.this);
                //변화된 나의 포스트 Array를 저장
                fillMyPostArray(MainActivity.this);



                dialog.dismiss();

                //Sharedpreferences를 사용해서 저장하기 전에는 사용하지 말 것!


//        //게시물을 지우고나면 ArrayList의 내용이 달라지기 때문에 달라진 부분을 덮어씌워서 저장하기 위한 과정이 필요하다.
//        Gson postGson=new Gson();
//        Type postListType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//
//        //이미 만들어둔 파일에 변경사항을 저장
//        SharedPreferences postSharedPreferences=getSharedPreferences(ID+"MainPost",MODE_PRIVATE);
//        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
//        //Gson을 이용하여 어레이리스트를 문자열로 변환
//        String postList=postGson.toJson(postItemArrayList1,postListType);
//        //문자열로 바뀐 ArrayList를 저장
//        postEditor.putString("PostArray",postList);
//        postEditor.commit();
//
//
//        Gson gridGson=new Gson();
//        Type gridListType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//
//        SharedPreferences gridSharedpreferences=getSharedPreferences(ID+"GridPost",MODE_PRIVATE);
//        SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
//
//
//        String gridList=gridGson.toJson(postPictureItemArrayList1,gridListType);
//        gridEditor.putString("GridArray",gridList);
//        gridEditor.commit();

            }
        });

        TextView_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //PostRecyclerViewClickListener인터페이스에서 오버라이드된 메소드
    @Override
    public void onCommentButtonClicked(int position) {
        Intent intent=new Intent(MainActivity.this, CommentActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("Position",position);
        Log.d("인덱스",String.valueOf(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLikeButtonClicked(int position) {



//        String realProfilePhotoUri=getSharedPreferences(ID,MODE_PRIVATE).getString("ProfileUri",null);
//        Cursor c = getContentResolver().query(Uri.parse(realProfilePhotoUri), null,null,null,null);
//        c.moveToNext();
//        String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
//        bitmapUri = Uri.fromFile(new File(path));
//        Log.d("프로필uri",bitmapUri.toString());
//        Log.e("URI", realProfilePhotoUri.toString());
//        c.close();

        //게시물의 좋아요 버튼을 누르면 알림액티비티의 리사이클러뷰의 리스트가 추가되게 만들었다.
//        int index= notificationItemArrayList.size();
//        String nickname=getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null);
//            NotificationListAdapter notificationListAdapter = new NotificationListAdapter(notificationItemArrayList);
//            if(Nickname.equals(nickname)){
//                notificationItemArrayList.add(new NotificationItem(Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(), "회원님이 본인의 게시물을 좋아합니다.", Nickname));
//            }else{
//                notificationItemArrayList.add(new NotificationItem(Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(), Nickname+"님이 회원님의 게시물을 좋아합니다.", Nickname));
//
//            }
//            notificationListAdapter.notifyItemInserted(index);
//            notificationListAdapter.notifyItemRangeChanged(index, notificationItemArrayList.size());
//
//            NotificationActivity.saveNotificationArray(this, nickname);
    }

    @Override
    public void onLikeButtonCanceled(int position) {

    }


    @Override
    public void onEditPostButtonClicked(int position) {
        Intent intent=new Intent(MainActivity.this, EditPostActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("Position",position);
        Log.d("인덱스",String.valueOf(position));
        Log.d("그리드 인덱스",String.valueOf(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
        bundle.putString("PostPicture",getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostPicture"+position,null));
        bundle.putString("PostArticle",getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostArticle"+position,null));
        bundle.putString("PostAddress",getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostAddress"+position,null));
        bundle.putFloat("PostLatitude",getSharedPreferences("MainPost",MODE_PRIVATE).getFloat("PostLatitude"+position, 1000));
        bundle.putFloat("PostLongitude",getSharedPreferences("MainPost",MODE_PRIVATE).getFloat("PostLongitude"+position, 1000));

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onNicknameButtonClicked(int position) {
        if(Nickname.equals(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null))){
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else{
            Intent intent=new Intent(MainActivity.this, AccountOthersActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle=new Bundle();
            bundle.putString("PostNickname", getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null));
            Log.d("닉네임: ",  getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null));
            bundle.putInt("Position",getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
            Log.d("인덱스: ",  String.valueOf(getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onProfileButtonClicked(int position) {
        //현재 로그인한 유저의 닉네임이 검색리스트에서 클릭한 닉네임과 일치하는 경우에는 자신의 계정 액티비티로 이동
        if(Nickname.equals(getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null))){
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);

            //현재 로그인한 유저의 닉네임과 검색리스트에서 클릭한 닉네임이 일치하지 않는 경우에는 다른 유저의 계정 액티비티로 이동
        }else{
            Intent intent=new Intent(MainActivity.this, AccountOthersActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle=new Bundle();
            bundle.putString("PostNickname", getSharedPreferences("MainPost",MODE_PRIVATE).getString("PostNickname"+position,null));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

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
            commentEditor.putInt("ViewType"+i,commentItemArrayList.get(i).viewType);

        }

        commentEditor.commit();

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


    @Override
    public void run() {
        for(int i=0; i<2; i++) {
            if (animationState == 0) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                animationState = 1;
            } else {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler1.sendEmptyMessage(0);
            }
        }
    }

    public static void fillMyPostArray(Context context){
        int value=0;
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MyPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
        for(int i=0; i<context.getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostSize",0); i++){

            if(postItemArrayList.get(i).focusState==true){
                Log.d("메인에서 인덱스",String.valueOf(i));
                Log.d("메인에서의 내 게시물 포커스",String.valueOf(postItemArrayList.get(i).focusState));

            if(postPictureItemArrayList.size()!=0) {
                postEditor.putInt("PostIndex" + i, value);
                postEditor.commit();
                Log.d("그리드에서의 내 게시물 인덱스", String.valueOf(postPictureItemArrayList.get(value).index));
                value += 1;
            }

            }
        }

    }

    public static void clearMyPostArray(Context context){
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MyPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
        postEditor.clear();
        postEditor.commit();
    }
}
