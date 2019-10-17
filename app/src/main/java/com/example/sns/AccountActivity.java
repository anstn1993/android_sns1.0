package com.example.sns;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


import static com.example.sns.LoginActivity.Nickname;
import static com.example.sns.PostActivity.postPictureItemArrayList;


public class AccountActivity extends AppCompatActivity implements AccountPostGridAdapter.PostGridRecyclerViewClickListener, Runnable{
    TextView TextView_nickname, TextView_name,TextView_statemessage, TextView_logout;
    CircleImageView CircleImageView_profile_picture;
    ImageButton Button_home, Button_search, Button_post, Button_notification;
    Button Button_editaccount, Button_postcount;
    Bundle bundle;
    Bitmap bitmap;
    RecyclerView RecyclerView_account_post_grid;
    RecyclerView.LayoutManager layoutManager;
    Uri uri;
    //계정 액티비티에서 실행될 스레드의 상태 제어를 하기 위한 boolean형 변수
    Boolean threadrun=true;
    int animationState=0;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    String name=getSharedPreferences(Nickname,MODE_PRIVATE).getString("Name",null);
                    TextView_name.setText(name);
                    Log.d("쓰레드 ","실행중");
                    break;
                case 1:
                    String stateMessage=getSharedPreferences(Nickname,MODE_PRIVATE).getString("StateMessage",null);
                    TextView_name.setText(stateMessage);
                    Log.d("쓰레드 ","실행중");
                    break;
            }


        }
    };


    //그리드 리사이클러뷰의 ArrayList의 static객체를 생성한 이유는 모든 액티비티에서 해당 ArrayList를 공유해서
    //실시간으로 해당 ArrayList를 저장하기 위해서임
    public static ArrayList<PostItem> postPictureItemArrayList1=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.d("onCreate", "진입");

        //메인(홈) 버튼 뷰를 객체와 연동
        Button_home=findViewById(R.id.Button_home);
        //검색 버튼 뷰를 객체와 연동
        Button_search=findViewById(R.id.Button_search);
        //업로드 버튼 뷰를 객체와 연동
        Button_post=findViewById(R.id.Button_post);
        //알림 버튼 뷰를 객체와 연동
        Button_notification=findViewById(R.id.Button_notification);
        //게시물 버튼 뷰를 객체와 연동
        Button_postcount=findViewById(R.id.Button_postcount);
        //닉네임 텍스트 뷰를 객체와 연동
        TextView_nickname=findViewById(R.id.TextView_nickname);
        //이름 텍스트 뷰를 객체와 연동
        TextView_name=findViewById(R.id.TextView_name);
        //로그아웃 텍스트 뷰를 객체와 연동
        TextView_logout=findViewById(R.id.TextView_logout);
        //프로필 편집 버튼 뷰를 객체와 연동
        Button_editaccount=findViewById(R.id.Button_editaccount);
        //프로필 이미지 뷰를 객체와 연동
        CircleImageView_profile_picture=findViewById(R.id.CircleImageView_profile_picture);





        setRecyclerView();



        //번들에 담긴 데이터를 받는 인텐트
        Intent intent=getIntent();
        bundle = intent.getExtras();





        //홈 버튼을 누르면 계정 액티비티에서 홈 액티비티로 이동
        Button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //검색 버튼을 누르면 계정 액티비티에서 검색 액티비티로 이동
        Button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, SearchActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //포스트 버튼을 누르면 계정 액티비티에서 포스트 액티비티로 이동
        Button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, PostActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //알림 버튼을 누르면 계정 액티비티에서 알림 액티비티로 이동
        Button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, NotificationActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        //프로필 수정 버튼을 누르면 프로필 수정 액티비티로 이동
        Button_editaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, EditAccountActivity.class);
                //한번 호출된 액티비티를 재활용하는 플래그
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });


        //계정 액티비티의 '로그아웃' 뷰를 클릭하면 로그아웃을 할지 확인하기 위한 Dialog생성
        TextView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                final Dialog dialog=new Dialog(AccountActivity.this);
                //타이틀 바 없애기
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //다이얼로그에 연결시킬 레이아웃 설정
                dialog.setContentView(R.layout.logout_check_box);
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

                        SharedPreferences sharedPreferences=getSharedPreferences("AutoLogin",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent=new Intent(AccountActivity.this, LoginActivity.class);

//                        SharedPreferences sharedPreferences1=getSharedPreferences("SaveLoginId",MODE_PRIVATE);
//                        SharedPreferences.Editor editor1=sharedPreferences1.edit();
//                        editor1.clear();
//                        editor1.commit();


                        SharedPreferences sharedPreferences2=getSharedPreferences("LoginState",MODE_PRIVATE);
                        SharedPreferences.Editor editor2=sharedPreferences2.edit();
                        editor2.putBoolean("LogedIn",false);
                        editor2.commit();

                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();

                        //메인액티비티를 객체를 MainActivity에서 선언해둔 static변수로 선언하고 로그아웃을 할 때 메인액티비티를 강제 종료
                        MainActivity MA= (MainActivity) MainActivity.mainActivity;
                        MA.finish();

                                            }
                });

                TextView_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });




    }

    private void setRecyclerView(){
        //리사이클러 뷰 객체와 리사이클러뷰 핸들
        RecyclerView_account_post_grid=findViewById(R.id.RecyclerView_account_post_grid);
        RecyclerView_account_post_grid.setHasFixedSize(true);
        //리사이클러 뷰의 레이아웃 매니저를 그리드 레이아웃으로 설정
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        RecyclerView_account_post_grid.setLayoutManager(layoutManager);


//        Gson gson=new Gson();
//        Type listType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//        String gridList=getSharedPreferences(ID+"GridPost",MODE_PRIVATE).getString("GridArray",null);
//        postPictureItemArrayList1=gson.fromJson(gridList,listType);

        PostActivity.loadGridPostArray(this, Nickname);
//        postPictureItemArrayList1=postPictureItemArrayList;


        //리사이클러 뷰의 어댑터 설정
        AccountPostGridAdapter accountPostGridAdapter=new AccountPostGridAdapter(postPictureItemArrayList);
        accountPostGridAdapter.setOnClickListener(AccountActivity.this);
        RecyclerView_account_post_grid.setAdapter(accountPostGridAdapter);
    }



    //재사용되는 액티비티에 인텐트를 넘겨받기 위해서 필요한 메소드



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Intent intent=getIntent();
        //번들에 받아온 데이터 입력
        bundle=intent.getExtras();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //리사이클러뷰의 레이아웃 메니저와 어댑터를 세팅해주는 메소드
        setRecyclerView();
        Log.d("onStart", "진입");


    }

    //자장된 데이터를 뷰들에 뿌려줌
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "진입");
        setRecyclerView();
        //최초 액티비티 실행이나 재진입을 할 경우에 스레드 실행을 캐치하기 위해서 onResume에서 true로 바꾸고 스레드 실행
        threadrun=true;
        Thread thread=new Thread(AccountActivity.this);
        thread.setDaemon(true);
        thread.start();

        //계정 액티비티의 항목에 들어갈 프로필 사진의 용량을 줄이기 위해 비트맵 팩토리를 이용
        BitmapFactory.Options options=new BitmapFactory.Options();
        //원본의 1/8크기로 압축
        options.inSampleSize=8;

        try {//사진을 비트맵으로 얻기
            //이미지 뷰에 내가 가져온 사진 넣어주기
            uri= Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null));
            bitmap = BitmapFactory.decodeFile(uri.getPath(),options);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
            CircleImageView_profile_picture.setRotation(90);//사진을 90도 회전
            CircleImageView_profile_picture.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

        } catch(NullPointerException e){
            e.printStackTrace();
        }

        //사용자의 닉네임을 미리 저장해준 데이터에서 가져와서 넣어줌
        String nickname=getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null);
        //사용자의 이름을 미리 저장해준 데이터에서 가져와서 넣어줌
        String name=getSharedPreferences(Nickname,MODE_PRIVATE).getString("Name",null);




        //닉네임 텍스트 뷰에 저장된 닉네임 데이터 입력
        TextView_nickname.setText(nickname);
        //이름 텍스트 뷰에 저장된 이름 데이터 입력
        TextView_name.setText(name);

        try{//게시물의 개수를 표시
            int count=postPictureItemArrayList.size();
            Button_postcount.setText(String.valueOf(count));
        }catch (NullPointerException e){
            e.printStackTrace();
        }




    }

    @Override
    protected void onStop() {
        super.onStop();
        //다른 액티비티로 이동하거나 앱을 잠시 나갔을 때 스레드를 종료시키기 위해서 onStop에서 false로 바꿔줌
        threadrun=false;
    }

    //그리드 뷰 사진을 클릭했을 때 포스트 아이템과 동일한 모양의 레이아웃을 가진 액티비티로 넘어가게 함
    @Override
    public void onGridImageViewClicked(int position) {
        Intent intent=new Intent(AccountActivity.this, MyPostActivity.class);
        Bundle bundle=new Bundle();
        //이 값은 그리드뷰의 인덱스를 던져준다.
        bundle.putInt("Position",position);
        Log.d("인덱스",String.valueOf(position));

        intent.putExtras(bundle);
        startActivity(intent);

    }


    @Override
    public void run() {
        String stateMessage=getSharedPreferences(Nickname,MODE_PRIVATE).getString("StateMessage","");
        while(threadrun && stateMessage.length()!=0){
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(1);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
