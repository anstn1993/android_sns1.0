package com.example.sns;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sns.PostActivity.postPictureItemArrayList;


public class AccountOthersActivity extends AppCompatActivity implements AccountPostGridAdapter.PostGridRecyclerViewClickListener, Runnable{
    TextView TextView_nickname, TextView_name;
    CircleImageView CircleImageView_profile_picture;
    ImageButton Button_home, Button_search, Button_post, Button_notification;
    Button Button_postcount;
    Bundle bundle;
    Bitmap bitmap;
    RecyclerView RecyclerView_account_post_grid;
    RecyclerView.LayoutManager layoutManager;
    Uri uri;
    Boolean threadrun=true;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    String name=getSharedPreferences(bundle.getString("PostNickname"),MODE_PRIVATE).getString("Name",null);
                    TextView_name.setText(name);
                    Log.d("쓰레드 ","실행중");
                    break;
                case 1:
                    String stateMessage=getSharedPreferences(bundle.getString("PostNickname"),MODE_PRIVATE).getString("StateMessage",null);
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
        setContentView(R.layout.activity_others_account);


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

        //프로필 이미지 뷰를 객체와 연동
        CircleImageView_profile_picture=findViewById(R.id.CircleImageView_profile_picture);
        //그리드 뷰를 객체와 연동


        setRecyclerView();





        //번들에 담긴 데이터를 받는 인텐트
        Intent intent=getIntent();
        bundle=new Bundle();
        bundle = intent.getExtras();





        //홈 버튼을 누르면 계정 액티비티에서 홈 액티비티로 이동
        Button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountOthersActivity.this, MainActivity.class);
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
                Intent intent=new Intent(AccountOthersActivity.this, SearchActivity.class);
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
                Intent intent=new Intent(AccountOthersActivity.this, PostActivity.class);
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
                Intent intent=new Intent(AccountOthersActivity.this, NotificationActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
        Intent intent=getIntent();
        Bundle bundle=new Bundle();
        bundle=intent.getExtras();
        PostActivity.loadGridPostArray(this,bundle.getString("PostNickname"));
//        postPictureItemArrayList1=postPictureItemArrayList;


        //리사이클러 뷰의 어댑터 설정
        AccountPostGridAdapter accountPostGridAdapter=new AccountPostGridAdapter(postPictureItemArrayList);
        accountPostGridAdapter.setOnClickListener(AccountOthersActivity.this);
        RecyclerView_account_post_grid.setAdapter(accountPostGridAdapter);
    }



    //재사용되는 액티비티에 인텐트를 넘겨받기 위해서 필요한 메소드

    //자장된 데이터를 뷰들에 뿌려줌
    @Override
    protected void onResume() {
        super.onResume();

        setRecyclerView();

        threadrun=true;
        Thread thread=new Thread(AccountOthersActivity.this);
        thread.setDaemon(true);
        thread.start();

        //계정 액티비티의 항목에 들어갈 프로필 사진의 용량을 줄이기 위해 비트맵 팩토리를 이용
        BitmapFactory.Options options=new BitmapFactory.Options();
        //원본의 1/8크기로 압축
        options.inSampleSize=8;

        try {//사진을 비트맵으로 얻기
            //이미지 뷰에 내가 가져온 사진 넣어주기
            String nickname=bundle.getString("PostNickname");
            uri= Uri.parse(getSharedPreferences(nickname,MODE_PRIVATE).getString("ProfileUri",null));
            bitmap = BitmapFactory.decodeFile(uri.getPath(),options);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
            CircleImageView_profile_picture.setRotation(90);//사진을 90도 회전
            CircleImageView_profile_picture.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

        } catch(NullPointerException e){
            e.printStackTrace();
        }

        //사용자의 닉네임을 미리 저장해준 데이터에서 가져와서 넣어줌
        String nickname=getSharedPreferences(bundle.getString("PostNickname"),MODE_PRIVATE).getString("Nickname",null);
        //사용자의 이름을 미리 저장해준 데이터에서 가져와서 넣어줌
        String name=getSharedPreferences(bundle.getString("PostNickname"),MODE_PRIVATE).getString("Name",null);




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

    }

    //그리드 뷰 사진을 클릭했을 때 포스트 아이템과 동일한 모양의 레이아웃을 가진 액티비티로 넘어가게 함
    @Override
    public void onGridImageViewClicked(int position) {
        String nickname=bundle.getString("PostNickname");

        Intent intent=new Intent(AccountOthersActivity.this, OthersPostActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("Position",position);
        bundle.putString("PostNickname",nickname);
        Log.d("인덱스",String.valueOf(position));

        intent.putExtras(bundle);
        startActivity(intent);
        Log.d("인덱스",String.valueOf(position));
    }

    @Override
    public void run() {
        String stateMessage=getSharedPreferences(bundle.getString("PostNickname"),MODE_PRIVATE).getString("StateMessage","");
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
